<%--
  Created by IntelliJ IDEA.
  Chatroom.User:
  Date: 12.01.2015
  Time: 21:25
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="utf-8" %>
<html>
  <head>
      <meta http-equiv = "Content-type" content = "text/html; charset=UTF-8">
      <title>Main</title>
      <link rel="stylesheet" href="css/style.css" type="text/css">
      <script type="text/javascript" src="<%=request.getContextPath()%>/jquery-2.1.3.js"></script>
      <script>
          $(document).ready(function(){
              $('#enter').bind('click',function(e){
                  if (($('#login').val()=="")||($('#password').val()=="")){
                      alert("Введіть логін та пароль перед входом до чату!");
                      e.preventDefault();
                  }
              });
              $('#registrate').bind('click',function(e){
                  $('#regname').val($('#login').val());
                  $('#regpass').val($('#password').val());
                  if (($('#login').val()=="")||($('#password').val()=="")){
                      alert("Введіть логін та пароль для реєстрації!");
                      e.preventDefault();
                  }
                  else{
                      $('#registering').submit();
                  }
              });
          });
      </script>
  </head>
  <body>
      <div class="main-wrapper">
          <div class = "logo"><img src= "img/logo.png"></div>
          <div class="register">
          <form method="post" action="<%=request.getContextPath()%>/login" name="loginning" id="loginning">
                  <div class ="login-block">
                     <label for ="login" class ="register-lable">Логін</label>
                     <input type ="text" id="login" name ="login" class = "text">
                  </div>
                  <div class ="login-block">
                     <label for ="password" class ="register-lable">Пароль</label>
                     <input type ="password" id="password" name ="password" class = "text">
                  </div>
                  <div>
                     <input type = "submit" name ="enter" id = "enter" value="ВХІД" class ="enter-button">
                  </div>
                  <%if(request.getSession().getAttribute("error").equals(true)){%>
                  <H6><span style="color: red"><%=request.getSession().getAttribute("errormessage")%></span></H6>
                  <%}%>
          </form>
          <form method="post" action="<%=request.getContextPath()%>/registrate" name="registering" id="registering">
                 <div>
                    <input type = "button" name="registrate" id="registrate" value="ЗАРЕЄСТРУВАТИСЯ" class ="register-button">
                 </div>
                 <%if(request.getSession().getAttribute("success")!=null){%>
                 <H6><span style="color: lawngreen"><%=request.getSession().getAttribute("success")%></span></H6>
                 <%}%>
                 <div style = "display: none">
                   <input type ="text" id="regname" name ="regname" class = "text">
                   <input type ="text" id="regpass" name ="regpass" class = "text">
                 </div>
          </form>
          </div>
      </div>
  </body>
</html>
