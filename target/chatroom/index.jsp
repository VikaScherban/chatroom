<%@ page language = "java" contentType = "text/html; charset=UTF-8" pageEncoding = "UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
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
                    alert("Введіть логін та пароль перед входом до чату");
                    e.preventDefault();
                }
            });
            $('#register').bind('click',function(e){
                if (($('#login').val()=="")||($('#password').val()=="")){
                    alert("При реєстрації поля логіну та пароля не повинні бути порожніми!");
                    e.preventDefault();
                }

            });
        });
    </script>
</head>
<body>
<div class="main-wrapper">
    <div class = "logo"><img src= "img/logo.png"></div>
        <div class="register">
            <div class ="login-block">
                <label for ="login" class ="register-lable">Логін</label>
                <input type ="text" id="login" name ="login" class = "text">
            </div>
            <div class ="login-block">
                <label for ="password" class ="register-lable">Пароль</label>
                <input type ="text" id="password" name ="password" class = "text">
            </div>
            <form method="post" action="<%=request.getContextPath()%>/login" name="authorization" id="authorization">
            <div>
                <input type = "submit" name ="enter" id = "enter" value="ВХІД" class ="enter-button">
            </div>
            </form>
            <%if(request.getSession().getAttribute("error").equals(true)){%>
            <H6><span style="color: red"><%=request.getSession().getAttribute("errormessage")%></span></H6>
            <%}%>
            <form method="post" name="registration" id="registration">
            <div>
                <input type = "submit" action="<%=request.getContextPath()%>/register" name ="register" id = "register" value="РЕЄСТРАЦІЯ" class ="enter-button">
            </div>
            </form>
        </div>
    <div class="footer">
        <a class="left-text footer-link">Про чат</a>
        <a class = "right-text footer-link">Контакти</a>
    </div>
</div>
</body>
</head>
</html>