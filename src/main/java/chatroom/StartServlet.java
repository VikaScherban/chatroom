package chatroom;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/start")
public class StartServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
            if(request.getSession().getAttribute("user")!=null){
                response.sendRedirect(request.getContextPath() + "/main");
            }else{
                if(request.getSession().getAttribute("error")==null){
                    request.getSession().setAttribute("error",false);
                }
                response.sendRedirect(request.getContextPath() + "/login");
            }
    }
}
