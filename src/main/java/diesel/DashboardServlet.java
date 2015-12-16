package diesel;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.calypso.tk.service.DSConnection;

public class DashboardServlet extends CalypsoServlet {
	
	private static final long serialVersionUID = -6721012433983998247L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		String[] args = getArgsFromCookies(request.getCookies());
		
		if(args != null) {

			DSConnection ds = null;
			
			String loginTemplate = getResourceFileAsString("dashboard.html");

			try {
				ds = getConnectionFromCookie(args);
				
				if(ds == null) {
					response.sendRedirect("login?login=failed");
					return;
				}
			} 
			finally {  
				if (ds != null)
					ds.disconnect();
			}
			
			response.getOutputStream().print(loginTemplate);
			return;
		}
		
		response.sendRedirect("login");
	}
}