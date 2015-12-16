package diesel;

import java.io.IOException;
import java.util.Vector;

import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.calypso.tk.service.DSConnection;
import com.calypso.tk.util.TradeArray;

public class StatsServlet extends CalypsoServlet {

	private static final long serialVersionUID = 5158882135860515689L;

	@SuppressWarnings("rawtypes")
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		String[] args = getArgsFromCookies(request.getCookies());
		
		if(args != null) {
			DSConnection ds = null;
	
			try {
				ds = getConnectionFromCookie(args);
	
				Vector clients = ds.getRemoteAccess().getConnectedClients();
				
				TradeArray ta = ds.getRemoteTrade().getTrades("trade", 
					"trunc(entered_date) >= trunc(sysdate)", null, true, false);
				
				int tradesMidnight = ta.size();
				
				ta = ds.getRemoteTrade().getTrades("trade", 
					"trunc(entered_date) >= trunc(sysdate - 7)", null, true, false);
				
				int trades7days = ta.size();
				
				JsonObject model = Json.createObjectBuilder()
				   .add("users", clients.size())
				   .add("tradesToday", tradesMidnight)
				   .add("trades7days", trades7days)
				   .build();
				    
				response.setContentType("application/json");
				response.setCharacterEncoding("utf-8");
				
				response.getOutputStream().print(model.toString());
				return;
				
			} catch(Exception e) {
				returnJsonError(response, e);
				return;
			} finally {   
				if (ds != null)
					ds.disconnect();
			}
		}
		

		returnJsonError(response, new Exception("Nothing Happened"));
	}
}