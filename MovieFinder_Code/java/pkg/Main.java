package pkg;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Main
 */
public class Main extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Main() {
        super();
    }

	/**
	 *User login verifies if credentials enter correspond to user already registered. 
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String Username = request.getParameter("Username");
		String Password = request.getParameter("Password");
		request.getSession().setAttribute("check_update", "false");
		if (new LoginDB().validateLogin( Username, Password))
		{
			request.getSession().setAttribute("Username", Username);
			request.getSession().setAttribute("Password", Password);
			request.getSession().setAttribute("LoginSuccess", "True");
			response.sendRedirect("MainPage.jsp");
		}else
		{
			request.getSession().setAttribute("LoginSuccess", "False");
    		response.sendRedirect("index.jsp");
    	}
	}

	/**
	 * Create new account by checking if the username does not already exist in database, otherwise asks user to try again. 
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		String FirstName=request.getParameter("FirstName");
    	String LastName=request.getParameter("LastName");
    	String Username=request.getParameter("Username");
    	String Password=request.getParameter("Password");
		request.getSession().setAttribute("check_update", "false");
		if (Username.length() < 5 || Password.length() < 5)
		{
			request.getSession().setAttribute("AccountCreated", "False");
			request.getSession().setAttribute("AccountCreated_reason", "Username and Password must be at least 5 characters long.");
    		response.sendRedirect("CreateLogin.jsp");
		}
		else
		{
			boolean created = false;
	    	if (!new LoginDB().validateLogin(Username, Password))
	    	{
	    		created = new LoginDB().addUser( Username, Password, FirstName, LastName);
	    	}
	    	if(created == true)
	    	{
				request.getSession().setAttribute("Username", Username);
				request.getSession().setAttribute("Password", Password);
				request.getSession().setAttribute("LoginSuccess", "True");
	    		response.sendRedirect("RegistrationSuccessful.jsp");
	    	}
	    	else{
	    		request.getSession().setAttribute("AccountCreated", "False");
				request.getSession().setAttribute("AccountCreated_reason", "Username already exists, please try again.");
	    		response.sendRedirect("CreateLogin.jsp");
	    	}
		}
    	
	}

}
