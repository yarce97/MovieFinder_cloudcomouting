<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Login Successful</title>
</head>
<body>
<% if ((session.getAttribute("Username")) == null) {%>
Unable to login in
<br>
<a href="index.jsp">Login</a>

<%} else{
%>
	Login Successful!<%=session.getAttribute("Username") %>

<a href="logout.jsp">Logout</a>
<%
} 
%>	
	
</body>
</html>