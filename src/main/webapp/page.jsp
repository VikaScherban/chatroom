<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-type" content="text/html; charset=ISO-8859-1">
<title>Main</title>
</head>
<body>
   <%
   		String login = request.getParameter("login");
        String password = request.getParameter("password");
        String loginIn = request.getParameter("enter");
        String register = request.getParameter("register");
   %>
   <p><font color="green">Login: <%= login %></font></p>
   <p><font color="green">Password: <%= password %></font></p>
   <p><font color="black">button Login: <%= loginIn %></font></p>
   <p><font color="black">button Password: <%= register %></font></p>
   
   <a href="myServlet">kdhufgfyudr erfdgfy</a>
</body>
</html>