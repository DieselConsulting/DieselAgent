package diesel;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Vector;

import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.calypso.tk.bo.document.AdviceDocument;
import com.calypso.tk.service.DSConnection;

public class DocumentServlet extends CalypsoServlet {

	private static final long serialVersionUID = -443712596630496383L;

	@SuppressWarnings("rawtypes")
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		
		DSConnection ds = null;

		try {
			ds = getConnection(request);
			
			String id = request.getParameter("id");
			
			Vector raw = ds.getRemoteBO().getAdviceDocuments(
				"advice_id = " + Integer.parseInt(id), null);
			
			String text = "Unable to process";
			
			if(raw.size() > 0) {
				AdviceDocument ad = (AdviceDocument)raw.elementAt(0);
				
				if(ad.getIsBinary()) {
					
					text = new String(ad.getBinaryDocument(), "UTF-8");
					
					// OK try and pull out some XML.
					int xmlStart = text.indexOf("<?xml");
					if(xmlStart != -1) {
						text = text.substring(xmlStart);
						
						String lines[] = text.split("\\r?\\n");
						if(lines.length > 1) {
							String xmlName = lines[1].split(" ")[0];
							String endTag = "</" + xmlName.substring(1) + ">";
							
							int lastIndex = text.indexOf(endTag) + endTag.length();
							text = text.substring(0, lastIndex);
						}

						text = text.replaceAll("\\p{C}", "");
						text = formatXML(text);
					}
					
				} else {
					
					text = ad.getDocument().toString();
					
				}
			}
				
			JsonObject model = Json.createObjectBuilder()
			   .add("doc", text)
			   .add("adviceId", id)
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

	public static String formatXML(String input)
    {
        try 
        {
            Document doc = DocumentHelper.parseText(input);  
            StringWriter sw = new StringWriter();  
            OutputFormat format = OutputFormat.createPrettyPrint();  
            format.setIndent(true);
            format.setIndentSize(3); 
            XMLWriter xw = new XMLWriter(sw, format);  
            xw.write(doc);  
            
            return sw.toString();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return input;
        }
    }
}