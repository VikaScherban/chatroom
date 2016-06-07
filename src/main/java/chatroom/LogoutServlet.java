package chatroom;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@WebServlet("/logout")
public class LogoutServlet extends ServerAwareServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Server server=getServer();
        //server.getMessages().add(new Message(server.getEcho(),"<span style=\"color: green\">"+request.getSession().getAttribute("user").toString()+" вийшов з чату!</span>",new Date()));
        server.getActiveUsers().remove(request.getSession().getAttribute("user"));
        request.getSession().setAttribute("user",null);
        response.sendRedirect(request.getContextPath()+"/login");
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
