package pkg;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class UserMovies
 */
public class UserMovies extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserMovies() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * User selects movie to add to own account and sends response back to display users movies. 
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String movieId = request.getParameter("movie_selected");
		if (movieId != null)
		{
			boolean movie_added = new LoginDB().addMovie(request.getSession().getAttribute("Username").toString(), movieId);
			
		}
		response.sendRedirect("MainPage.jsp");		
	}

	/**
	 * User selects movie to delete from personal movie collection 
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String movieId = request.getParameter("user_movie_delete");
		if (movieId != null)
		{
			String username = request.getSession().getAttribute("Username").toString();
			boolean val = new LoginDB().removeMovie(username, movieId);
		}		
		response.sendRedirect("MainPage.jsp");
		
	}

}
