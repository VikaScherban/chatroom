package chatroom;

import javax.servlet.http.HttpServlet;

public class ServerAwareServlet extends HttpServlet{
    public Server getServer(){
        return (Server) getServletContext().getAttribute("server");
    }
}
