<%@ page import="java.util.Date" %>
<%--
  Created by IntelliJ IDEA.
  Chatroom.User: Vika and Roxolana
  Date: 13.01.2015
  Time: 22:41
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>eChat</title>
  <link rel="stylesheet" href="css/style.css" type="text/css">
</head>
<body>
  <div class ="chat-wrapper">
      <div id="textpos" class="text-block"></div>
      <div class = "chat-body">
      <h2> <%=request.getSession().getAttribute("user").toString()%>, вітаємо в чаті!</h2>
      <div class = "top-chat">
        <div class="user-inf user1">
          <span class = "user-inf-text">Друг</span>
          <div id="joy-text2" class="advice display_none">Так тримати! Усмішка продовжує життя :)</div>
          <div id="sad-text2" class="advice display_none">Вам варто заспокоїтися. Послухайте приємну музику чи погладьте котика :)</div>
          <div id="joy-image2" class="user-img-joy display_none"></div>
          <div id="sad-image2" class="user-img-sad display_none"></div>
        </div>
        <form action="<%=request.getContextPath()%>/logout" method="post">
          <input class="user-inf leave-chat-button" type="submit" id="exit" value="Покинути чат">
        </form>
        <div class="user-inf user2">
          <span class="user-inf-text"><%=request.getSession().getAttribute("user").toString()%></span>
          <div id="joy-text1" class="advice display_none">Так тримати! Усмішка продовжує життя :)</div>
          <div id="sad-text1" class="advice display_none">Вам варто заспокоїтися. Послухайте приємну музику чи погладьте котика :)</div>
          <div id="joy-image1" class="user-img-joy display_none"></div>
          <div id="sad-image1" class="user-img-sad display_none"></div>
        </div>
      </div>
      <div class="replics" >
        <div class="message-block" id="chat"></div>
        <div class="add">
          <div class="add-some add-smile"><img src="img/smile.jpg"></div>
          <ul class="smile-list display_none">
            <li><div class="smile-btn"><img src="smiles/aa.gif"></div></li>
            <li><div class="smile-btn"><img src="smiles/ab.gif"></div></li>
            <li><div class="smile-btn"><img src="smiles/ac.gif"></div></li>
            <li><div class="smile-btn"><img src="smiles/ad.gif"></div></li>
            <li><div class="smile-btn"><img src="smiles/ae.gif"></div></li>
            <li><div class="smile-btn"><img src="smiles/af.gif"></div></li>
            <li><div class="smile-btn"><img src="smiles/ag.gif"></div></li>
            <li><div class="smile-btn"><img src="smiles/ah.gif"></div></li>
            <li><div class="smile-btn"><img src="smiles/ai.gif"></div></li>
            <li><div class="smile-btn"><img src="smiles/aj.gif"></div></li>
            <li><div class="smile-btn"><img src="smiles/ak.gif"></div></li>
            <li><div class="smile-btn"><img src="smiles/al.gif"></div></li>
            <li><div class="smile-btn"><img src="smiles/am.gif"></div></li>
            <li><div class="smile-btn"><img src="smiles/an.gif"></div></li>
            <li><div class="smile-btn"><img src="smiles/ao.gif"></div></li>
            <li><div class="smile-btn"><img src="smiles/ap.gif"></div></li>
            <li><div class="smile-btn"><img src="smiles/aq.gif"></div></li>
            <li><div class="smile-btn"><img src="smiles/ar.gif"></div></li>
            <li><div class="smile-btn"><img src="smiles/as.gif"></div></li>
            <li><div class="smile-btn"><img src="smiles/at.gif"></div></li>
          </ul>
        </div>
        <textarea class="message-area" id="message" wrap="off" placeholder="Повідомлення"></textarea>
        <form id="sendmessage">
           <input type="submit" id="send" class="message-button">
        </form>
      </div>
    </div>
      <div class="text-block negative-block" id="textneg">Негативні слова</div>
  </div>
  <script type="text/javascript" src="<%=request.getContextPath()%>/jquery-2.1.3.js"></script>
  <script>
    $(document).ready(function(){
//      scroll to bottom
      window.setInterval(function() {
        var elem = document.getElementById('chat');
        elem.scrollTop = elem.scrollHeight;
      }, 500);

      setInterval(getMessages,500);

      function getMessages(){
        $.ajax({
          url: "<%=request.getContextPath()%>/main",
          type: "GET",
          data:"action=gettext"
        }).done(function(responsetext){
            var text = responsetext.split("*");

          var posresult = text[0].toString();
            if (posresult!=null){

                $("#textpos").html("");
                $("#textpos").append(posresult);
            }
            var negresult = text[1].toString();
            if (negresult!=null){

                $("#textneg").html("");
                $("#textneg").append(negresult);
            }
        }).fail(function(){
            //alert("Сервер не доступний");
        });

        $.ajax({
          url: "<%=request.getContextPath()%>/main",
          type: "GET",
          data:"action=getmood"
        }).done(function(responsetext){
          var mood = responsetext.split(";");
          switch(Number(mood[0]))
          {
            case 0:
              $('#joy-text1').removeClass('display_none');
              $('#sad-text1').addClass('display_none');
              $('#joy-image1').removeClass('display_none');
              $('#sad-image1').addClass('display_none');
              $('.chat-body').css('background-image', 'url(\'../img/happy-background.jpg\')');
              break;
            case 1:
              $('#joy-text1').addClass('display_none');
              $('#sad-text1').removeClass('display_none');
              $('#joy-image1').addClass('display_none');
              $('#sad-image1').removeClass('display_none');
              $('.chat-body').css('background-image', 'url(\'../img/sad-background.jpg\')');
              break;
            case -1:
              $('#joy-text1').addClass('display_none');
              $('#sad-text1').addClass('display_none');
              $('#joy-image1').addClass('display_none');
              $('#sad-image1').addClass('display_none');
              $('.chat-body').css('background-image', 'none');
              break;
            default :
              $('#joy-text1').addClass('display_none');
              $('#sad-text1').addClass('display_none');
              $('#joy-image1').addClass('display_none');
              $('#sad-image1').addClass('display_none');
              $('.chat-body').css('background-image', 'none)');
          }

          switch(Number(mood[1]))
          {
            case 0:
              $('#joy-text2').removeClass('display_none');
              $('#sad-text2').addClass('display_none');
              $('#joy-image2').removeClass('display_none');
              $('#sad-image2').addClass('display_none');
              break;
            case 1:
              $('#joy-text2').addClass('display_none');
              $('#sad-text2').removeClass('display_none');
              $('#joy-image2').addClass('display_none');
              $('#sad-image2').removeClass('display_none');
              break;
            case -1:
              $('#joy-text2').addClass('display_none');
              $('#sad-text2').addClass('display_none');
              $('#joy-image2').addClass('display_none');
              $('#sad-image2').addClass('display_none');
              break;
            default :
              $('#joy-text2').addClass('display_none');
              $('#sad-text2').addClass('display_none');
              $('#joy-image2').addClass('display_none');
              $('#sad-image2').addClass('display_none');
          }

        }).fail(function(){
          //alert("Сервер не доступний");
        });

        $.ajax({
          url: "<%=request.getContextPath()%>/main",
          type: "GET",
          data:"action=getmessages"
        }).done(function(responsetext){
          $('#chat').append(responsetext);
        }).fail(function(){
          //alert("Сервер не доступний");
        });
      }
      function sendMessage() {
        var message=$('#message').val();
        $.ajax({
          url: "<%=request.getContextPath()%>/main",
          type: "POST",
          data:"message="+message
        }).fail(function(){
          alert("Помилка відправлення!");
        });
      }
      $('#sendmessage').submit(function(e){
        if ($('#message').val() == "") {
          e.preventDefault();
          alert("Повідомлення не може бути порожнім!");
        } else {
          e.preventDefault();
          sendMessage();
          $('#message').val("");
        }
      });
    });

  </script>
  <script src="main.js"></script>
</body>
</html>
