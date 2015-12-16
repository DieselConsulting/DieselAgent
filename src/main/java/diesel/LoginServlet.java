package diesel;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginServlet extends CalypsoServlet {
	
	private static final long serialVersionUID = -4739744765131708215L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		
		String loginTemplate = getResourceFileAsString("login.html");
		
		loginTemplate = loginTemplate.replaceAll("#ENV_OPTIONS#", 
			createEnvOptions());
		
		response.getOutputStream().print(loginTemplate);
	}
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		
		String user = request.getParameter("user");
		String password = request.getParameter("password");
		String env = request.getParameter("env");
		
		if(user != null && password != null && env != null) {

			Cookie cookie1 = new Cookie("username", user);
			Cookie cookie2 = new Cookie("password", password);
			Cookie cookie3 = new Cookie("env", env);
			
			response.addCookie(cookie1);
			response.addCookie(cookie2);
			response.addCookie(cookie3);
		}
		
		response.sendRedirect("/test-agent");
	}
	
	private String createEnvOptions() {
		
		String envs = getResourceFileAsString("ENVS.txt");
		String s = "";
		
		String[] list = envs.split(",");
        for(final String name : list){
            s += "<option>" + name + "</option>";
        }
        
        return s;
	}
}