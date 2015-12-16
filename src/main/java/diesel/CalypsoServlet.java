package diesel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.calypso.tk.core.Log;
import com.calypso.tk.service.DSConnection;
import com.calypso.tk.util.ConnectionUtil;

@SuppressWarnings("serial")
public class CalypsoServlet extends HttpServlet {


	protected DSConnection getConnection(HttpServletRequest request) {
    	try {            	
			String[] args = new String[] {
					"-user", request.getParameter("user"),
					"-password", request.getParameter("password"),
					"-env", request.getParameter("env")
				};
    		return ConnectionUtil.connect(args, "TestAgent");
    	}
    	catch (Throwable t) {
    		Log.error("TestAgent", "Failed to get a connection", t);
    		return null;
    	}
    }

	protected DSConnection getConnectionFromCookie(String[] args) {
    	try {            	
    		return ConnectionUtil.connect(args, "TestAgent");
    	}
    	catch (Throwable t) {
    		
    		if(args.length > 5) {
        		Log.error("TestAgent", args[1]);
        		Log.error("TestAgent", args[3]);
        		Log.error("TestAgent", args[5]);
    		}
    		
    		Log.error("TestAgent", "Failed to get a connection", t);
    		return null;
    	}
    }
	
	protected String[] getArgsFromCookies(Cookie[] cookies) {
		
		String user = null;
		String password = null;
		String env = null;
		
		if(cookies == null)
			return null;
		
		for(int i = 0; i < cookies.length; i++) {
			if(cookies[i].getName().equals("username")) {
				user = cookies[i].getValue();
			}
			if(cookies[i].getName().equals("password")) {
				password = cookies[i].getValue();
			}
			if(cookies[i].getName().equals("env")) {
				env = cookies[i].getValue();
			}
			
		}
		
		if(user == null || password == null || env == null) 
			return null;
		

		String[] args = new String[] {
			"-user", user,
			"-password", password,
			"-env", env
		};
		
		return args;
	}

	protected void returnJsonError(HttpServletResponse response, Exception e)
			throws IOException {
		JsonObject model = Json.createObjectBuilder()
		   .add("error", e.toString())
		   .build();
		    
		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");
		
		response.getOutputStream().print(model.toString());
	}
	
	// convert InputStream to String
	protected String getResourceFileAsString(String templateName) {
		
		InputStream is = 
			this.getClass().getClassLoader().getResourceAsStream(templateName);
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		try {

			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return sb.toString();

	}
}
