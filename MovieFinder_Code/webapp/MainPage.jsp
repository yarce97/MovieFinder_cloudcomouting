<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="pkg.*, java.util.*" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>MovieFinder</title>
<link rel="stylesheet" type="text/css" href="styles/styles.css">
<link href='https://fonts.googleapis.com/css?family=Orbitron' rel='stylesheet'>
<link href='https://fonts.googleapis.com/css?family=Orbitron' rel='stylesheet'>
<style>
header {
    font-family: 'Orbitron';
    background-color: powderblue;
    text-align: left;
    font-size: 18px;
    color: #403866;
}
input[type="submit"] 
{
	font-size: 16px;
	color: #ffffff;
	width: 20%;
	line-height: 1.0;
	text-transform: uppercase;	
	justify-content: center;
	align-items: center;
	padding: 0 0px;
	height: 42px; 
	border-radius: 5px;
	background-color: #7b9d9c;
	font-weight:bold;
}
img {
    max-width: 100%;
    height: 400px;
    width: 270px; /* ie8 */
}

.image_overlay
{
	position: relative;	
}
.overlay
{
	position: absolute;
  	top: 0;
  	bottom: 0;
  	left: 0;
  	right: 0;  
  	height: 100%;
   width: 100%;
  	opacity: 0;
  	transition: .5s ease;
 	background-color: black;
}
.image_overlay:hover .overlay 
{
  	opacity: 0.8;
}
.text_overlay
{
  color: white;
  font-size: 16px;
  position: absolute; 
  text-align: center;
}
</style>
</head>
<body style="background-color: #f6f4ef" >
	<header >
		<h2>MovieFinder</h2></header>
		<span class="logout_main">
		<a href="Logout.jsp">Logout</a></span>
	<section>
		<header style="background-color: #403866; color: white;font-family: Impact, Charcoal, sans-serif;font-size: 28px;">&nbsp;&nbsp; <%=session.getAttribute("Username")%>'s Movies<br></header>
		<div class="saved_movies">
		
				
			<article>
				<span class="saved_movies_title"> 
				</span>
				
				
				<form method="post" action="UserMovies">
					<%
					ArrayList<Movie> userMovies = new MovieDB().userMovies(session.getAttribute("Username").toString());
					userMovies = new MovieDB().sortby(userMovies, "title");
					
					%>
				<table id="user_movies">
				<% 
				int s = userMovies.size();
				int n = (int) Math.ceil(s/4.0);
				int c = 0;
				for(int row=0; row < n; row++) 
				{ %>
    				<tr>
				<%      for(int col=0; col< 4; col++) 
						{
							if (c < s)
							{
								Movie temp = userMovies.get(c);
							%>
        					<td style="width: 25%">
								<div class="image_container">
        						<figure>
        							<div class="image_overlay">
        								<img src=<%=temp.posterpath %> alt=<%=temp.title %>>
        								<div class="overlay">
											<div class="text_overlay">Plot: <%=temp.overview%></div>
										</div>
									</div>
        							<figcaption>
        								<%=temp.title%> <br>
        								<%=temp.release_date%> <br>
        								<%=temp.genres.toString()%>
        							</figcaption>
        						</figure>					
        						<input type="radio" name="user_movie_delete" value=<%=temp.movie_id.intValue() %>> 
        						</div>
        					</td>
        					<%
        					c++;
        					}
        				} %>
    			</tr>
				<% } %>
				</table>
				<br>
				<input type="submit" value="Remove Movie" style="width: 150px">
				</form>
			</article>
		</div>
		
	</section>
	<section>
		<header style="background-color: #403866;color: white;font-family: Impact, Charcoal, sans-serif;font-size: 30px;">&nbsp;&nbsp;Search Movies</header>
		<div class="find_movies">
		<article >
			<br>
			<form method="get" action="UserPage">
				<label for="choose">Search by Genre: 
				</label>
				<br>
				<select name="genreSelected" style="justify-content: center">
					<option value="All">All</option>
					<option value="Action">Action</option>
					<option value="Comedy">Comedy</option>
					<option value="Animation">Animation</option>
					<option value="Drama">Drama</option>
					<option value="Thriller">Thriller</option>
					<option value="Adventure">Adventure</option>
					<option value="History">History</option>
					<option value="War">War</option>
					<option value="Fantasy">Fantasy</option>
					<option value="Science Fiction">Science Fiction</option>
					<option value="Music">Music</option>
					<option value="Family">Family</option>
					<option value="Horror">Horror</option>
					<option value="Romance">Romance</option>
					<option value="Documentary">Documentary</option>
					<option value="Mystery">Mystery</option>
					<option value="Crime">Crime</option>
				</select>	&nbsp;&nbsp;
					 &nbsp; &nbsp; Sort by:  &nbsp; &nbsp;
					<input type="radio" name="sortby" value="title">Title  &nbsp; &nbsp;
					<input type="radio" name="sortby" value="release_date">Release Date &nbsp; &nbsp;&nbsp; &nbsp;
					
					<input type="submit" value="Submit" style="width: 120px">
			</form>
			<%if (session.getAttribute("genreRequest") != null) 
			{
				String movieR = session.getAttribute("genreRequest").toString();
				ArrayList<Movie> movieRequest= null;
				if (movieR.equals("All"))
				{
					movieRequest = new MovieDB().queryAllGenres();
				}
				else {
					movieRequest = new MovieDB().queryByGenre( movieR); 
				}
				String sort = session.getAttribute("sortby").toString();
				movieRequest = new MovieDB().sortby(movieRequest, sort);
				%>
				<div class="content_query_results">
				<br>
				<span class="find_movies_title" style= "font-size: 20px;"> 
					Movie Results for <%=movieR %> : <%=movieRequest.size() %>
				</span>
				<form method="get" action="UserMovies">
				<table id="movies_request" style="width: 100%">
				<% 
				int size = movieRequest.size();
				int numRows = (int) Math.ceil(size/4.0);
				int counter = 0;
				for(int row=0; row < numRows; row++) 
				{ %>
    				<tr>
				<%      for(int col=0; col< 4; col++) 
						{
							if (counter < size)
							{
								Movie temp = movieRequest.get(counter);
							%>
							<td style="width: 25%">
								<div class="image_container">
        						<figure>
        							<div class="image_overlay">
        								<img src=<%=temp.posterpath %> alt=<%=temp.title %>>
        								<div class="overlay">
											<div class="text_overlay">Plot: <%=temp.overview%></div>
										</div>
									</div>
        							<figcaption>
        								<%=temp.title%> <br>
        								<%=temp.release_date%> <br>
        								<%=temp.genres.toString()%>
        							</figcaption>
        						</figure>					
        						<input type="radio" name="movie_selected" value=<%=temp.movie_id.intValue() %>> 
        						</div>
        					</td>
        					<%
        					}
        					
        					counter++;
        				} %>
    			</tr>
				<% } %>
				</table>
				<br>
				<input type="submit" value="Add Movie" style="width: 120px; justify-content: center;align-items: center;" >
				</form>
				</div>
				<br>
			<%} %>			
		</article>
		<br>
		</div>
	</section>
	<footer>
	</footer>
</body>
</html>