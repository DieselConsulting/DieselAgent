package diesel;

import java.io.IOException;
import java.util.Vector;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.calypso.apps.common.CalypsoMLConfiguration;
import com.calypso.apps.common.adapter.DefaultCalypsoSessionAdapter;
import com.calypso.apps.importer.adapter.ImporterAdapter;
import com.calypso.tk.service.DSConnection;
import com.calypso.tk.service.DefaultCalypsoConnection;
import com.calypso.tk.service.DefaultCalypsoSession;

public class CalypsoMarkupLanguageServlet extends CalypsoServlet {
	
	private static final long serialVersionUID = -6721012433983998247L;

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		
		DSConnection ds = null;

		try {
			ds = getConnection(request);
			
			DefaultCalypsoSession dcs = new DefaultCalypsoSession(
				new DefaultCalypsoConnection(DSConnection.getDefault()));
			
			DefaultCalypsoSessionAdapter lcsa = new DefaultCalypsoSessionAdapter(dcs);
			
			CalypsoMLConfiguration mlConf = new CalypsoMLConfiguration();
			
			ImporterAdapter ia = mlConf.buildImporterAdapter(lcsa.getSession());
			
			String xml = request.getParameter("cml");
			
			ia.importXML(xml);
			
			String code = getTradeReference(xml);
			
			int tradeId = getRealTradeId(code, ds);
			
			JsonObject model = Json.createObjectBuilder()
			   .add("code", code)
			   .add("tradeId", tradeId)
			   .add("keywords", getKeywords(tradeId, ds))
			   .build();
			    
			response.setContentType("application/json");
			response.setCharacterEncoding("utf-8");
			
			response.getOutputStream().print(model.toString());
			
		} catch(Exception e) {
			returnJsonError(response, e);
		} finally {   
			if (ds != null)
				ds.disconnect();
		}
	}
	
	@SuppressWarnings("rawtypes")
	private JsonArrayBuilder getKeywords(long tradeId, DSConnection ds) 
		throws Exception {
		
		Vector v = ds.getRemoteAccess().executeSelectSQL(
				"select * from trade_keyword where trade_id = " + tradeId);
			
		JsonArrayBuilder keys = Json.createArrayBuilder();

		for(int i = 2; i < v.size(); i++) {
			Vector row = (Vector)v.elementAt(i);

			keys.add(Json.createObjectBuilder().add(
				"name", (String)row.elementAt(1)).add(
				"value", (String)row.elementAt(2)));
		}
		return keys;
	}
	
	@SuppressWarnings("rawtypes")
	private int getRealTradeId(String code, DSConnection ds) throws Exception {

		Vector v = ds.getRemoteAccess().executeSelectSQL(
			"select trade_id from trade_keyword where keyword_value = '" +
			"" + code +
			"' and keyword_name = 'CODIFIER-convention'");
		

		if(v.size() > 2) {
			Vector row = (Vector)v.elementAt(2);
			String cell = (String)row.elementAt(0);
			return Integer.parseInt(cell);
		}
		return 0;
	}
	
	private String getTradeReference(String xml) {

        String begin = "<calypso:keyword><calypso:name>CODIFIER-convention" +
        	"</calypso:name><calypso:value xsi:type=\"ns3:string\"" +
        	" xmlns:ns3=\"http://www.w3.org/2001/XMLSchema\">";
		
		xml = xml.substring(xml.indexOf(begin) + begin.length() , xml.length());
		
		String code = xml.substring(0, 
			xml.indexOf("</calypso:value></calypso:keyword>"));
		
		return code;
	}
}