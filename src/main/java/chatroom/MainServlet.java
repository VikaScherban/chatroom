package chatroom;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.DefaultCaret;
import java.io.*;
import java.lang.reflect.Array;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.Arrays;

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
                String lowmes = (message.getMes()).toLowerCase();
                String[] words = server.getElemS(lowmes);
                String word2;
                String word3;

                if (words.length == 1) {
                    if (posWords.contains(words[0])) numbOfPos++;
                    else if (negWords.contains(words[0])) numbOfNeg++;
                }

                if (words.length>=2){
                    for (int i=0; i<words.length - 1; i++)
                    {
                        if (words.length>=3 && i<words.length - 2) {
                            word3 = words[i] + " " + words[i+1] + " " + words[i+2];
                            if (posWords.contains(word3)) numbOfPos++;
                            else if (negWords.contains(word3)) numbOfNeg++;
                        }
                        word2 = words[i] + " " + words[i+1];
                        if (posWords.contains(word2)) numbOfPos++;
                        else if (negWords.contains(word2)) numbOfNeg++;
                            //словосполучення не міститься в жодній базі, перевіряємо окремо кожне слово
                        else  {if( posWords.contains(words[i])) numbOfPos++;
                               else if (negWords.contains(words[i])) numbOfNeg++;
                               if (i == words.length-2) if( posWords.contains(words[i+1])) numbOfPos++;
                               else if (negWords.contains(words[i+1])) numbOfNeg++;
                              }
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


    //знаходить списки позитивних та негативних слів, які знаходяться в повідомленнях
    private String mesFindWords(ArrayList <Message> messages){
        Server server=getServer();
        ArrayList<String> posList = new ArrayList<>();
        ArrayList<String> negList = new ArrayList<>();
        ArrayList <String> pos = server.getPosText();
        ArrayList<String> neg = server.getNegText();
        String result;

        if(messages.size()>0) {
            for (Message message : messages) {
                String lowmes = (message.getMes()).toLowerCase();
                String[] words = server.getElemS(lowmes);
                int i=0;
                while (words[i]!=null) {
                    if (pos.contains(words[i])) posList.add(words[i]);
                    if (neg.contains(words[i])) negList.add(words[i]);
                    i++;
                }
            }
        }

        result = textAnalysis(posList, negList);
        return result;
    }

    //пошук всіх таких word позитивних слів в тексті та їх оточення
    private ArrayList<String> findWordNext(int n, ArrayList<String> text, String word){
        ArrayList<String> result = new ArrayList<>();
        String phrase1 = "";
        String phrase2 = "";
        String phrase3 = "";
        for (int i = n; i<text.size()-1; i++){
            if (text.get(i).equalsIgnoreCase(word)) {
                if (i != 0) {phrase1 = text.get(i - 1) + " " + text.get(i) + " " + text.get(i + 1);
                                                phrase2 = text.get(i) + " " + text.get(i + 1);
                                                phrase3 = text.get(i - 1) + " " + text.get(i);}
            }
            if (phrase1 != "" && !result.contains(phrase1)) result.add(phrase1);
            if (phrase2 != "" && !result.contains(phrase2)) result.add(phrase2);
            if (phrase3 != "" && !result.contains(phrase3)) result.add(phrase3);
        }
        return  result;
    }

    private boolean checkTwo(String s){
        int rez = 0;
        for (int i=0; i<s.length(); i++)
        {
            if (s.charAt(i)== ' ')  rez++;
        }

        if (rez == 1) return true;
        return false;
    }

    //знаходить в текстах слова,які зустрічались в повідомленнях і виділяє словоспоолучення з ними
    private String textAnalysis(ArrayList<String> pos, ArrayList<String> neg) {
        Server server = getServer();
        ArrayList<String> texts = server.getTexts();
        ArrayList<String> posWords = new ArrayList<>();//містяться позитивні слова, які знайшлись в тексті
        ArrayList<String> negWords = new ArrayList<>();//містяться негативні слова, які знайшлись в тексті
        ArrayList<ArrayList<String>> posPhraseList = new ArrayList<>();//містяться позитивні словосполучення по номеру відповідає позитивному слову зі списку posWords
        ArrayList<ArrayList<String>> negPhraseList = new ArrayList<>();
        String phrase1 = "";
        String phrase2 = "";
        String phrase3 = "";
        ArrayList<String> combinText = new ArrayList<>();
        int size = texts.size();

        //знаходимо словосполучення з одинарних слів(з повідомлення)
        if (size == 2) {
            combinText.add(texts.get(0) + " " + texts.get(1));
        } else if (size > 2) {
            for (int i = 0; i < size - 2; i++) {
                combinText.add(texts.get(i) + " " + texts.get(i + 1));
                combinText.add(texts.get(i) + " " + texts.get(i + 1) + " " + texts.get(i + 2));
            }
            combinText.add(texts.get(size - 2) + " " + texts.get(size - 1));
        }

        //шукаємо кількість одинарних слів(з повідомлень),які є в тексті. Зберігається в posCount negCount
        int [] posCount = new int[100];
        int [] negCount = new int[100];
        for(int i=0; i<posCount.length; i++) {
            posCount[i] = 1;
            negCount[i] = 1;
        }

        ArrayList<String> posother = new ArrayList<>();
        ArrayList<String> negother = new ArrayList<>();
        for (int i = 0; i < texts.size(); i++) {
            //пошук позитивних слів та відповідний список словосполучень з ним в тексті
            if (pos.contains(texts.get(i)) && !posWords.contains(texts.get(i))) {
                posWords.add(texts.get(i));
                if (i != 0 && texts.size() > i) {
                    phrase1 = texts.get(i - 1) + " " + texts.get(i) + " " + texts.get(i + 1);
                    phrase2 = texts.get(i) + " " + texts.get(i + 1);
                    phrase3 = texts.get(i - 1) + " " + texts.get(i);
                }
                //шукаємо в тексті позтивне слово, яке вже виявили(шукаємо чи воно є ще)
                posother = findWordNext(i + 1, texts, texts.get(i));
                posPhraseList.add(posWords.size() - 1,posother);
            }
            else if (posWords.contains(texts.get(i))) posCount[posWords.size()-1]++;

            if (phrase1 != "" && !posother.contains(phrase1)) posPhraseList.get(posWords.size() - 1).add(phrase1);
            if (phrase2 != "" && !posother.contains(phrase2)) posPhraseList.get(posWords.size() - 1).add(phrase2);
            if (phrase3 != "" && !posother.contains(phrase3)) posPhraseList.get(posWords.size() - 1).add(phrase3);
            phrase1="";
            phrase2="";
            phrase3="";

            //пошук негативних слів та відповідний список словосполучень з ним в тексті
            if (neg.contains(texts.get(i)) && !negWords.contains(texts.get(i))) {
                    negWords.add(texts.get(i));
                    if (i != 0 && texts.size() > i) {
                        phrase1 = texts.get(i - 1) + " " + texts.get(i) + " " + texts.get(i + 1);
                        phrase2 = texts.get(i) + " " + texts.get(i + 1);
                        phrase3 = texts.get(i - 1) + " " + texts.get(i);
                    }
                    //шукаємо в тексті позтивне слово, яке вже виявили(шукаємо чи воно є іще)
                    negother = findWordNext(i + 1, texts, texts.get(i));
                    negPhraseList.add(negWords.size() - 1,negother);
                }
                else if (negWords.contains(texts.get(i))) negCount[negWords.size()-1]++;
            if (phrase1 != "" && !negother.contains(phrase1)) negPhraseList.get(negWords.size() - 1).add(phrase1);
            if (phrase2 != "" && !negother.contains(phrase2)) negPhraseList.get(negWords.size() - 1).add(phrase2);
            if (phrase3 != "" && !negother.contains(phrase3)) negPhraseList.get(negWords.size() - 1).add(phrase3);
            phrase1="";
            phrase2="";
            phrase3="";
        }


        //шукаємо кількість подвійних і потрійних словосполучень(з повідомлень), які є в тексті. Зберігається в posPhraseCount negPhraseCount
        ArrayList<int []> posPhraseCount = new ArrayList<>();
        ArrayList<int []> negPhraseCount = new ArrayList<>();

        for (int i=0; i<posPhraseList.size();i++)
        {
            int [] arraypos = new int [posPhraseList.get(i).size()];
            posPhraseCount.add(i,arraypos);
        }

        for (int i=0; i<negPhraseList.size();i++)
        {
            int [] arrayneg = new int [negPhraseList.get(i).size()];
            negPhraseCount.add(i,arrayneg);
        }

        for (int i=0; i<combinText.size(); i++)
        {
            for (int k=0; k<posPhraseList.size(); k++)
                for (int j=0; j<posPhraseList.get(k).size(); j++) {
                    if (combinText.get(i).equalsIgnoreCase(posPhraseList.get(k).get(j)))
                        posPhraseCount.get(k)[j]++;
                }
            for (int k=0; k<negPhraseList.size(); k++)
                for (int j=0; j<negPhraseList.get(k).size(); j++) {
                    if (combinText.get(i).equalsIgnoreCase(negPhraseList.get(k).get(j)))
                        negPhraseCount.get(k)[j]++;
                }
        }

        //формуємо String в якому табличка результату : 1) слово/совосполучкння 2) кілбкісь з'явлень в текстах 3) відсоток від загальної кількості
        String result="<p>Позитивні слова</p><table cellpadding=\"5\" cellspacing=\"0\">";
        result += "<tr><td id=\"col1\">СЛОВО</td><td id=\"col2\">К-ІСТЬ</td><td id=\"col3\">ЙМОВ</td></tr>";
        for (int i=0; i<posPhraseList.size(); i++) {
            result += "<tr><td id=\"col1\">" + posWords.get(i) + "</td><td id=\"col2\">" + posCount[i]+"</td><td id=\"col3\">"+ Math.rint(100000.0 * 100.0 /texts.size()*(double)posCount[i]) / 100000.0  +"%</td><td></td></tr>";
            for (int j = 0; j < posPhraseList.get(i).size(); j++)
                if (checkTwo(posPhraseList.get(i).get(j))) {
                    result += "<tr><td id=\"col1\">-" + posPhraseList.get(i).get(j) + "</td><td id=\"col2\">" + posPhraseCount.get(i)[j] + "</td><td id=\"col3\">" + 100 / posCount[i] * posPhraseCount.get(i)[j] + "%</td><td></td></tr>";
                }
                else result += "<tr><td id=\"col1\" class=\"red-col\">" + posPhraseList.get(i).get(j) + "</td><td id=\"col2\" class=\"red-col\">" + posPhraseCount.get(i)[j] + "</td><td id=\"col3\" class=\"red-col\">" + 100 / posCount[i] * posPhraseCount.get(i)[j] + "%</td><td></td></tr>";


        }
        result +="</table>";

        result +="*";

        result += "<p>Негативні слова</p><table cellpadding=\"5\" cellspacing=\"0\">";
        result += "<tr><td id=\"col1\">СЛОВО</td><td id=\"col2\">К-ІСТЬ</td><td id=\"col3\">ЙМОВ</td></tr>";
        for (int i=0; i<negPhraseList.size(); i++) {
            result += "<tr><td id=\"col1\">" + negWords.get(i) + "</td><td id=\"col2\">" + negCount[i]+"</td><td id=\"col3\">"+ Math.rint(100000.0 * 100.0 /texts.size()*(double)negCount[i]) / 100000.0+"%</td><td></td></tr>";
            for (int j = 0; j < negPhraseList.get(i).size(); j++)
                if (checkTwo(negPhraseList.get(i).get(j))) {
                    result += "<tr><td id=\"col1\">-" + negPhraseList.get(i).get(j) + "</td><td id=\"col2\">" + negPhraseCount.get(i)[j] + "</td><td id=\"col3\">" + 100 / negCount[i] * negPhraseCount.get(i)[j] + "%</td><td></td></tr>";
                }
            else result += "<tr><td id=\"col1\" class=\"red-col\"> " + negPhraseList.get(i).get(j) + "</td><td id=\"col2\" class=\"red-col\">" + negPhraseCount.get(i)[j] + "</td><td id=\"col3\" class=\"red-col\">" + 100 / negCount[i] * negPhraseCount.get(i)[j] + "%</td><td></td></tr>";

        }
        result +="</table>";
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
            //String resultUser = emotionAnalyser(getLastTimeMes(server.getActiveUsers().get(request.getSession().getAttribute("user")), new Date(new Date().getTime() - 3600000)));
            String resultUser = emotionAnalyser(getLastMes(server.getActiveUsers().get(request.getSession().getAttribute("user"))));
            String resultText = resultUser + ";-1";
            response.getWriter().println(resultText);
        }
        else if((request.getParameter("action")!=null)&&(request.getParameter("action").equals("gettext")))
        {
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/html");
            String resultTextAnalise = mesFindWords(getLastMes(server.getActiveUsers().get(request.getSession().getAttribute("user"))));
            response.getWriter().println(resultTextAnalise);
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
