package chatroom;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.DefaultCaret;
import java.io.*;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Pattern;

@WebServlet("/main")
public class MainServlet extends ServerAwareServlet {
    private String printMessages(Date last_Time,ArrayList<Message> messages){
        String chat="";
        if(messages.size() > 0)
        {
            String userActive = messages.get(0).getAuthor().getName().toString();
            for(Message message:messages){
                if(message.getTimestamp().after(last_Time)){
                    if (message.getAuthor().getName().toString()==userActive) chat+= "<div class='replic-user1'><div class='user-message'>"+message.getMes()+"</div>"+"<div class=\"user-avatar user1-avatar\"><div class=\"user-avatar-img user1-avatar-img\"></div></div></div>";
                    else   chat+= "<div class='replic-user2'><div class='user-message'>"+message.getMes()+"</div>"+"<div class=\"user-avatar user2-avatar\"><div class=\"user-avatar-img user2-avatar-img\"></div></div></div>";
                }
            }
        }
        return chat;
    }

    private String emotionAnalyser(ArrayList <Message> messages){
        Server server=getServer();
        String result = "-1"; //=0 - positive, =1 - negative, =-1 - neutral
        int numbOfPos = 0;
        int numbOfNeg = 0;
        double posresult = 0.0;
        double negresult = 0.0;
        int sizemes = messages.size();
        ArrayList<String> posWords = server.getPosPhrases();
        ArrayList<String> negWords = server.getNegPhrases();
        if(messages.size()>0)
        {
            for (Message message : messages)
            {
                String lowmes = message.getMes();
                String word2;
                String word3;
                String[] words =lowmes.split(" ");
                if (words.length == 1) {
                    if (posWords.contains(words[0])) numbOfPos++;
                    else if (negWords.contains(words[0])) numbOfNeg++;
                }

                if (words.length>=2){
                    for (int i=0; i<words.length - 1; i++)
                    {
                        word2 = words[i] + " " + words[i+1];
                        if (posWords.contains(word2)) numbOfPos++;
                        else if (negWords.contains(word2)) numbOfNeg++;
                            //словосполучення не міститься в жодній базі
                        else  {if( posWords.contains(words[i])) numbOfPos++;
                               else if (negWords.contains(words[i])) numbOfNeg++;
                               if (i == words.length-2) if( posWords.contains(words[i+1])) numbOfPos++;
                               else if (negWords.contains(words[i+1])) numbOfNeg++;
                              }
                    }
                }

                if (words.length>=3){
                    for (int i=0; i<words.length - 2; i++)
                    {
                        word3 = words[i] + " " + words[i+1] + " " + words[i+2];
                        if (posWords.contains(word3)) numbOfPos++;
                        else if (negWords.contains(word3)) numbOfNeg++;

                    }
                }
                posresult += numbOfPos*((double)sizemes/messages.size());
                negresult += numbOfNeg*((double)sizemes/messages.size());
                sizemes++;
                numbOfPos = 0;
                numbOfNeg = 0;
            }

            if (posresult > negresult) result = "0";
            else if (posresult < negresult) result = "1";
        }
        return result;
    }

    private ArrayList<Message> getLastMes (User user){
        ArrayList<Message> lastmes= new ArrayList<>();
        Server server=getServer();
        ArrayList<Message> allmes = server.getMessages();
        for (Message message:allmes){
            if (message.getAuthor().equals(user)) lastmes.add(message);
        }

        if (lastmes.size() != 0){
        Message curmes = lastmes.get(0);
            for (Message message:lastmes) {
            if (curmes.getTimestamp().before(message.getTimestamp())) {
                curmes = message;
            }
          }
            lastmes.clear();
            lastmes.add(curmes);
            return lastmes;
        }
        else return null;
    }

    private ArrayList<Message> getLastTimeMes (User user, Date lasthour ){
        ArrayList<Message> mess= new ArrayList<>();
        Server server=getServer();
        ArrayList<Message> allmes = server.getMessages();
        for (Message message:allmes){
            if (message.getAuthor().equals(user) && message.getTimestamp().after(lasthour)) mess.add(message);
        }
            return mess;
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        String message=request.getParameter("message");
        Server server=getServer();
        Message newMes= new Message(server.getActiveUsers().get(request.getSession().getAttribute("user")),message,new Date());
        server.getMessages().add(newMes);
        //request.getSession().setAttribute("user1", emotionAnalyser(getLastMes(server.getActiveUsers().get(request.getSession().getAttribute("user")))));
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        Server server=getServer();
        if((request.getParameter("action")!=null)&&(request.getParameter("action").equals("getmessages"))){
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/html");
            response.getWriter().println(printMessages(server.getActiveUsers().get(request.getSession().getAttribute("user")).getLastInterractionTime(), server.getMessages()));
            server.getActiveUsers().get(request.getSession().getAttribute("user")).setLastInterractionTime(new Date());

        }
        else if((request.getParameter("action")!=null)&&(request.getParameter("action").equals("getmood")))
        {
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/html");
            String resultUser = emotionAnalyser(getLastTimeMes(server.getActiveUsers().get(request.getSession().getAttribute("user")), new Date(new Date().getTime() - 3600000)));
            String resultText = resultUser + ";-1";
            response.getWriter().println(resultText);
        }
        else{
            if(request.getSession().getAttribute("user")!=null){
                request.getRequestDispatcher("main.jsp").forward(request,response);
            }else{
                response.sendRedirect(request.getContextPath() + "/login");
            }
        }

    }

}
