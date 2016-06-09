package chatroom;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.lang.*;

public class Server {
    private HashMap<String, User> allUsers = new HashMap<String, User>();
    private HashMap<String, User> activeUsers = new HashMap<String, User>();
    private ArrayList<Message> messages = new ArrayList<Message>();
    private ArrayList<String> phrasespos = new ArrayList<String>();
    private ArrayList<String> phrasesneg = new ArrayList<String>();
    private ArrayList<String> texts = new ArrayList<String>();
    private ArrayList<String> postext = new ArrayList<>();
    private ArrayList<String> negtext = new ArrayList<>();

    private User echo = new User("echo", "", new Date());

    public HashMap<String, User> getAllUsers() {
        return allUsers;
    }

    public void setAllUsers(HashMap<String, User> allUsers) {
        this.allUsers = allUsers;
    }

    public HashMap<String, User> getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(HashMap<String, User> activeUsers) {
        this.activeUsers = activeUsers;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public ArrayList<String> getPhrases(String s){

        ArrayList<String> words = new ArrayList<String>();

        String[] a = s.split(";");
        for (int i=1; i< a.length-1; i++){
            words.add(a[i]);
        }
        String elem =  a[0].substring(1, a[0].length());
        words.add(elem);
        return words;
    }

    public boolean isLetter(char ch){
        if (ch != ' ' && ch != '.' && ch != ',' && ch != '?' && ch != '!' && ch != '(' &&
                ch != ')' && ch != ';' && ch != '\t' && ch != '\n' && ch!='0' && ch!='1' &&
                ch!='2' && ch!='3' && ch!='4' && ch!='5' && ch!='6' && ch!='7' && ch!='8' &&
                ch!='9' && ch != '"') return true;
        return false;
    }

    public ArrayList<String> getElem(String s){
        ArrayList<String> elems = new ArrayList<>();
        String word = "";
        int i=0;

        while(i < s.length()) {
            char ch = s.charAt(i);
            if (isLetter(ch)) word += s.charAt(i);
            else if (word!= "\uFEFF" && word != "" && !elems.contains(word)) {elems.add(word); word = "";}
           i++;
        }
        if (word!= "\uFEFF" && word != "") elems.add(word);
        elems.remove(0);
        return elems;
    }

    public String[] getElemS (String s){

        String[] elems = new String[100000];
        String word = "";
        int k = -1;
        int i=0;
        while(i < s.length()) {
            char ch = s.charAt(i);
            if (isLetter(ch)) word += s.charAt(i);
            else if (word != "") {elems[++k] = word; word ="";}
            i++;
        }
        if (word != "") elems[++k]= word;
        return elems;
    }

    public ArrayList<String>  getPosPhrases(){
        return phrasespos;
    }

    public ArrayList<String>  getNegPhrases(){
        return phrasesneg;
    }

    public ArrayList<String> getTexts(){
        return texts;
    }

    public ArrayList<String> getPosText(){
        return postext;
    }

    public ArrayList<String> getNegText(){
        return negtext;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public User getEcho() {
        return echo;
    }

    public void setEcho(User echo) {
        this.echo = echo;
    }

    public String readFiles(String fileName){
        String s = "";
        try (FileInputStream inp1 = new FileInputStream(fileName)) {
            Scanner in1 = new Scanner(new InputStreamReader(inp1, "UTF-8")).useLocale(Locale.US);
            while (in1.hasNext()) {
                s += in1.nextLine();
            }
            in1.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s.toLowerCase();
    }

    public void readElement(){
        phrasespos = getPhrases(readFiles("C:\\Users\\Vika\\Desktop\\chatroom\\src\\main\\resources\\positive.txt"));
        phrasesneg = getPhrases(readFiles("C:\\Users\\Vika\\Desktop\\chatroom\\src\\main\\resources\\negative.txt"));
        postext = getPhrases(readFiles("C:\\Users\\Vika\\Desktop\\chatroom\\src\\main\\resources\\positive2.txt"));
        negtext = getPhrases(readFiles("C:\\Users\\Vika\\Desktop\\chatroom\\src\\main\\resources\\negative2.txt"));
        texts = getElem(readFiles("C:\\Users\\Vika\\Desktop\\chatroom\\src\\main\\resources\\texts.txt"));
    }

}
