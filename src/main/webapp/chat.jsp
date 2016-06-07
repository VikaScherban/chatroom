<%@ page language = "java" contentType = "text/html; charset=UTF-8" pageEncoding = "UTF-8"%>
<%@ page import="java.lang.String" %> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<meta http-equiv = "Content-type" content = "text/html; charset=ISO-8859-1">
	<title>Chat</title>
	<link rel="stylesheet" href="css/style.css" type="text/css">
</head>
<body>
	<div class ="chat-wrapper">
		<div class = "chat-body">
			<div class = "top-chat">
				<div class="user-inf user1">
				   <span class="user-inf-text">User 1</span>
				   <div class="advice">Вам варто заспокоїтися. Послухайте приємну музику чи погладьте котика :)</div>
				   <div class="user-img-sad"></div>
				 </div>
				<div class="user-inf leave-chat-button">Покинути чат</div>
				<div class="user-inf user2">
					<span class = "user-inf-text">User 2</span>
					<div class="advice">Так тримати! Усмішка продовжує життя :)</div>
					<div class="user-img-joy"></div>
				</div>
			</div>
			<div class="replics">
			     <div class="replic-user2">
			     	<div class="user-avatar user2-avatar"><div class="user-avatar-img user2-avatar-img"></div></div>
			     	<div class="user-message"><span>Привіт, друже! Як справи?</span></div>
			     </div>
			     <div class="replic-user1">
			     	<div class="user-message"><span>Сьогодні видався дуже поганий день! Мені поставили двійку з математики. Тепер батьки мене приб’ють!</span></div>
			     	<div class="user-avatar user1-avatar"><div class="user-avatar-img user1-avatar-img"></div></div>
			     </div>
			     <div class="add">
			     	<div class="add-some add-smile"><img src="img/smile.jpg"></div>
			     	<div class="add-some add-pick"><img src="img/pick.jpg"></div>
			     </div>
			     <div class="message-block">
					<textarea class="message-area"></textarea>
			     </div>
			     <div class="message-button"></div>
			</div>
		</div>
	</div>
</body>
</html>