<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Create Account</title>
<link rel="stylesheet" type="text/css" href="styles/styles.css">
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
<body style="background-color: #f6f4ef" >

	<header><h2>MovieFinder</h2></header>

<div class="border">

		
	<div class="user_input">
		<form method="post" action="Main">	
		<span class="createAccount_form_title"> 
					Create Account
		</span>
		<br>
		<div class="wrap_validate_input" data-validate=" First Name is required">
					<input class="input100" type="text" name="FirstName" placeholder="First Name" required>
					<span class="focus-input100"></span>
		</div>
		<br>
		<div class="wrap_validate_input" data-validate=" Last Name is required">
					<input class="input100" type="text" name="LastName" placeholder="Last Name" required>
					<span class="focus-input100"></span>
		</div>
		<br>
		<div class="wrap_validate_input" data-validate=" Username is required">
					<input class="input100" type="text" name="Username" placeholder="Username" required>
					<span class="focus-input100"></span>
		</div>
		<br>
		<div class="wrap_validate_input" data-validate=" Password is required">
					<input class="input100" type="password" name="Password" placeholder="Password" required>
					<span class="focus-input100"></span>
		</div>
		<div class="container_login_create">
			<div>
				<a href="index.jsp" class="txt1">Login</a>
			</div>
					
		</div>
		<br>
		<div class="submit_btn"><input type="submit" value="Create Account"/></div>
		
		<div class="incorrect">
					<%String output = "";
					if (session.getAttribute("AccountCreated") == "False")
					{
						output = session.getAttribute("AccountCreated_reason").toString();
					}
					 %>  
					<span style="color:red;font-weight:bold">
						<%=output %>
					 </span>
				</div>
		
		</form>
	</div>
</div>
</body>
</html>