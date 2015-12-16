package diesel;

import java.io.IOException;
import java.util.Vector;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.calypso.tk.service.DSConnection;

public class QueryServlet extends CalypsoServlet {

	private static final long serialVersionUID = -443712596630496383L;

	@SuppressWarnings("rawtypes")
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		
		DSConnection ds = null;

		try {
			ds = getConnection(request);
			
			String query = request.getParameter("query");
			
			Vector rows = ds.getRemoteAccess().executeSelectSQL(query);
			
			JsonArrayBuilder jsonRows = Json.createArrayBuilder();
			
			for(int i = 2; i < rows.size(); i++) {
				
				Vector header = (Vector)rows.get(0);
				
				Vector row = (Vector)rows.get(i);

				JsonObjectBuilder job = Json.createObjectBuilder();
				for(int x = 0; x < header.size(); x++) {
					
					String head = "EMPTY";
					if(header.get(x) != null)
						head = (String)header.get(x);
					
					Object val = "NOVAL";
					if(row.get(x) != null)
						val = row.get(x);
					
					job.add(head, val.toString());
				}
				
				jsonRows.add(job);
			}
			    
			response.setContentType("application/json");
			response.setCharacterEncoding("utf-8");
			
			response.getOutputStream().print(jsonRows.build().toString());
			
		} catch(Exception e) {
			returnJsonError(response, e);
		} finally {   
			if (ds != null)
				ds.disconnect();
		}
	}
}