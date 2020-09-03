package pkg;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class UserPage
 */
public class UserPage extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserPage() {
        super();
    }

	/**
	 * User selects genre to generate movie results for sets how to order the results (title or release date)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String genreSelected = request.getParameter("genreSelected");
		request.getSession().setAttribute("genreRequest", genreSelected);
		String sortby = request.getParameter("sortby");
		if (sortby != null)
		{
			request.getSession().setAttribute("sortby", sortby);
		}
		else {
			request.getSession().setAttribute("sortby", "title");
		}
		response.sendRedirect("MainPage.jsp");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{

	}

}
