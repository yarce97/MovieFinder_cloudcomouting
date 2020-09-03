<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="UTF-8"%>
<%@ page import="pkg.*, java.util.*" %>
<!DOCTYPE html>
<html>
<head>
<link rel="stylesheet" type="text/css" href="styles/styles.css">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Home Page</title>
<link href='https://fonts.googleapis.com/css?family=Orbitron' rel='stylesheet'>
<style>
header {
    font-family: 'Orbitron';
    background-color: powderblue;
    text-align: left;
    font-size: 18px;
    color: #403866;
}
</style>
</head>
<%
//create a session so only runs once 
if (session.getAttribute("check_update") == null)
{
	if (!new LoginDB().tableExist("loginTable")){
		new LoginDB().createLoginTable(); }
	//check if movie db exists 
	if (!new LoginDB().tableExist("movie_list")) {
		new MovieDB().createMovieTable(); }
	//enter fetchMovieData to get upcoming movies, if  not null (db need to be updated)
	ArrayList<Movie> upcomingMovieList= new MovieAPI().fetchMovieData();
	if (upcomingMovieList != null)
	{
	new MovieDB().addMovies("movie_list", upcomingMovieList);
	}
}
%>
<body style="background-color: #f6f4ef" >
	<header>
		<h2>MovieFinder</h2>
		<div style="text-align: right; color: black; font-size: 20px; font-family: Arial, Helvetica, sans-serif;">
		<h5>Welcome to MovieFinder, a one stop destination to finding all the upcoming movie releases! </h5>
		</div>
		
	</header>
	<div class="border">
		<div class="container-login-form">
			<form method="get" action="Main">
				<span class="login_form_title"> 
					Login
				</span>
				
				<div class="wrap_validate_input" data-validate=" Username is required">
					<input class="input100" type="text" name="Username" placeholder="Username" required>
					<span class="focus-input100"></span>
				</div>
				
				<div class="wrap_validate_input" data-validate=" Password is required">
					<input class="input100" type="password" name="Password" placeholder="Password" required>
					<span class="focus-input100"></span>
				</div>
				
				<div class="container-create-account">
					<div>
						<a href="CreateLogin.jsp" class="txt1">Create Account</a>
					</div>
					
				</div>
				<div class="container-login-button">
					<input type="submit" value="login" style="font-size: 24px;"/>
				</div>
				<div class="incorrect">
					<%String output = "";
					if (session.getAttribute("LoginSuccess") == "False")
					{
						output = "Invalid username or password. Please try again.";
					}
					 %>  
					<span style="color:red;font-weight:bold">
						<%=output %>
					 </span>
				</div>
			</form>
		</div>
	</div>
	<footer>
	</footer>
</body>

</html>