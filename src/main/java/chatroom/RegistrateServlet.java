package chatroom;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@WebServlet("/registrate")
public class RegistrateServlet extends ServerAwareServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        getServletContext();
        request.getSession().setAttribute("error", false);
        request.getSession().setAttribute("success",null);
        String login=request.getParameter("regname");
        String password=request.getParameter("regpass");
        Server server = getServer();
        if(server.getAllUsers().containsKey(login)){
            request.getSession().setAttribute("error",true);
            request.getSession().setAttribute("errormessage","Користувач з даним ім'ям уже існує. Виберіть інше ім'я!");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }else{
            User newUser = new User(login,password,new Date());
            server.getAllUsers().put(login,newUser);
            request.getSession().setAttribute("success","Реєстрація пройшла успішно");
            response.sendRedirect(request.getContextPath() + "/login");
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
