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
                String lowmes = message.getMes();
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
                        word2 = words[i] + " " + words[i+1];
                        if (posWords.contains(word2)) numbOfPos++;
                        else if (negWords.contains(word2)) numbOfNeg++;
                            //словосполучення не міститься в жодній базі, перевіряємо комбінації слів
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
                String lowmes = message.getMes();
                String[] words = server.getElemS(lowmes);
                for (int i=0; i<words.length; i++) {
                    if (pos.contains(words[i])) posList.add(words[i]);
                    if (neg.contains(words[i])) negList.add(words[i]);
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
        for (int i = n; i<text.size(); i++){
            if (text.get(i) == word) {
                if (i != 0 && text.size() > i) phrase1 = text.get(i - 1) + " " + text.get(i) + " " + text.get(i + 1);
                if (i == 0 && text.size() >= i) phrase2 = text.get(i) + " " + text.get(i + 1);
                if (i != 0 && text.size() == i-1) phrase3 = text.get(i - 1) + " " + text.get(i);
            }
            if (phrase1 != "" && !result.contains(phrase1)) result.add(phrase1);
            if (phrase2 != "" && !result.contains(phrase2)) result.add(phrase2);
            if (phrase3 != "" && !result.contains(phrase3)) result.add(phrase3);
        }
        return  result;
    }

    //знаходить в текстах слова,які зустрічались в повідомленнях і виділяє словоспоолучення з ними
    private String textAnalysis(ArrayList<String> pos, ArrayList<String> neg) {
        Server server = getServer();
        ArrayList<String> texts = server.getTexts();
        ArrayList<String> posWords = new ArrayList<>();//містяться позитивні слова, які знайшлись в тексті
        ArrayList<ArrayList<String>> posPhraseList = new ArrayList<>();//містяться позитивні словосполучення по номеру відповідає позитивному слову зі списку posWords
        ArrayList<ArrayList<String>> negPhraseList = new ArrayList<>();
        String phrase1 = "";
        String phrase2 = "";
        String phrase3 = "";
        ArrayList<String> combinText = new ArrayList<>();
        int size = texts.size();

        //знаходимо словосполучення з одинарних слів(з повідомлення), які ми знаходимо в тексті. Зберігаємо в posPhrase negPhrase
        if (size == 2) {
            combinText.add(texts.get(0) + " " + texts.get(1));
        } else if (size > 2) {
            for (int i = 0; i < size - 2; i++) {
                combinText.add(texts.get(i) + " " + texts.get(i + 1));
                combinText.add(texts.get(i) + " " + texts.get(i + 1) + " " + texts.get(i + 2));
            }
            combinText.add(texts.get(size - 2) + " " + texts.get(size - 1));
        }
        //пошук позитивних слів та відповідний список словосполучень з ним в тексті

        for (int i = 0; i < texts.size(); i++) {
            if (pos.contains(texts.get(i))) {
                posWords.add(texts.get(i));
                if (i != 0 && texts.size() > i)
                    phrase1 = texts.get(i - 1) + " " + texts.get(i) + " " + texts.get(i + 1);
                if (i == 0 && texts.size() >= i) phrase2 = texts.get(i) + " " + texts.get(i + 1);
                if (i != 0 && texts.size() == i - 1) phrase3 = texts.get(i - 1) + " " + texts.get(i);
            }
            if (phrase1 != "") posPhraseList.get(posWords.size() - 1).add(phrase1);
            if (phrase2 != "") posPhraseList.get(posWords.size() - 1).add(phrase2);
            if (phrase3 != "") posPhraseList.get(posWords.size() - 1).add(phrase3);

            //шукаємо в тексті позтивне слово, яке вже виявили(шукаємо чи воно є іще)
            ArrayList<String> findother = findWordNext(i + 1, texts, texts.get(i));
            posPhraseList.get(posWords.size() - 1).addAll(findother);

            /*if (neg.contains(texts.get(i))) {
                if (i != 0 && texts.size() > i) phrase1 = texts.get(i - 1) + " " + texts.get(i) + " " + texts.get(i + 1);

                if (i == 0 && texts.size() >= i) phrase2 = texts.get(i) + " " + texts.get(i + 1);
                if (i!=0 && texts.size() == i-1) phrase3 = texts.get(i - 1) + " " + texts.get(i);
            }
            if (phrase1 != "") negPhrase.add(phrase1);
            if (phrase2 != "") negPhrase.add(phrase2);
            if (phrase3 != "") negPhrase.add(phrase3);*/
        }

        //шукаємо кількість одинарних слів(з повідомлень),які є в тексті. Зберігається в posCount negCount
        int [] posCount = new int[posWords.size()];
        //int [] negCount = new int[neg.size()];
        for(int i=0; i<posCount.length; i++)
            posCount[i] = 0;
        /*for(int i=0; i<negCount.length; i++)
            negCount[i] = 0;*/

        for (int i=0; i<texts.size(); i++)
        {
            for (int j=0; j<posWords.size(); j++)
                if (texts.get(i) == posWords.get(j))   posCount[j]++;
           /*for (int j=0; j<neg.size(); j++)
                if (texts.get(i) == neg.get(j)) negCount[j]++;*/
        }

        //шукаємо кількість подвійних і потрійних словосполучень(з повідомлень), які є в тексті. Зберігається в posPhraseCount negPhraseCount
        ArrayList<int []> posPhraseCount = new ArrayList<>();
        ArrayList<int []> negPhraseCount = new ArrayList<>();
        for (int i=0; i<posPhraseList.size();i++)
        {
            for(int j=0; j<posPhraseList.get(i).size(); j++)
                posPhraseCount.get(i)[j] = 0;
        }

        for (int i=0; i<combinText.size(); i++)
        {
            for (int k=0; k<posPhraseList.size(); k++)
                for (int j=0; j<posPhraseList.get(k).size(); j++) {
                    if (combinText.get(i) == posPhraseList.get(k).get(j)) posPhraseCount.get(k)[j] += 1;
                }

            /*for (int j=0; j<negPhrase.size(); j++)
            {
                if (combinText.get(i) == negPhrase.get(j)) negPhraseCount[j]++;
            }*/
        }

        //формуємо String в якому табличка результату : 1) слово/совосполучкння 2) кілбкісь з'явлень в текстах 3) відсоток від загальної кількості
        String result="";
        result = "Позитивні слова\n";
        for (int i=0; i<posPhraseList.size(); i++) {
            result += posWords.get(i) + " " + posCount[i] + "\n";
            for (int j = 0; j < posPhraseList.get(i).size(); j++)
                result += posPhraseList.get(i).get(j) + " " + posPhraseCount.get(i)[j] + " " + 100 / posPhraseList.get(i).size() * posPhraseCount.get(i)[j]+"%" + "\n";

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
