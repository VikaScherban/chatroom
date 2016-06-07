package chatroom;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

@WebServlet("/login")
public class LoginServlet extends ServerAwareServlet{

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        getServletContext();
        request.getSession().setAttribute("error", false);
        String login=request.getParameter("login");
        String password=request.getParameter("password");
        Server server = getServer();
        if(server.getAllUsers().containsKey(login)&&!(server.getActiveUsers().containsKey(login))){
            User currUser=server.getAllUsers().get(login);
            if(currUser.getPassword().equals(password)){
                server.getActiveUsers().put(login,currUser);
                request.getSession().setAttribute("user", login);
                //request.getSession().setAttribute("user1", -1);
                request.getSession().setAttribute("lastitertime",currUser.getLastInterractionTime().toString());
                //server.getMessages().add(new Message(server.getEcho(), "<span style=\"color: green\">"+currUser.getName() + " увійшов до чату.</span>",new Date()));
                response.sendRedirect(request.getContextPath() + "/main");
            }else{
                request.getSession().setAttribute("error",true);
                request.getSession().setAttribute("errormessage","Неправильно введені логін та пароль користувача!");
                request.getRequestDispatcher("login.jsp").forward(request,response);
            }
        }else{
            if(server.getActiveUsers().containsKey(login)){
                request.getSession().setAttribute("errormessage","Користувач з даним ім'ям уже знаходиться в чаті");
            }else{
                request.getSession().setAttribute("errormessage","Неправильно введені логін та пароль користувача!");
            }
            request.getSession().setAttribute("error",true);
            request.getRequestDispatcher("login.jsp").forward(request,response);
        }
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        if(request.getSession().getAttribute("user")!=null){
            response.sendRedirect(request.getContextPath() + "/main");
        }else{
            request.getRequestDispatcher("login.jsp").forward(request,response);
        }

    }
}
