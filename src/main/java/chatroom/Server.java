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

    public ArrayList<String>  getPosPhrases(){
        return phrasespos;
    }

    public ArrayList<String>  getNegPhrases(){
        return phrasesneg;
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

    public void readFiles() throws FileNotFoundException{
        String s1= "";
        String s2= "";
        try (FileInputStream inp1 = new FileInputStream("E:/positive.txt")) {
            Scanner in1 = new Scanner(new InputStreamReader(inp1, "UTF-8")).useLocale(Locale.US);
            while(in1.hasNext()){
                s1 += in1.nextLine();
            }
            in1.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileInputStream inp2 = new FileInputStream("E:/negative.txt")) {
            Scanner in2 = new Scanner(new InputStreamReader(inp2, "UTF-8")).useLocale(Locale.US);
            while(in2.hasNext()){
                s2 += in2.nextLine();
            }
            in2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        phrasespos = getPhrases(s1);
        phrasesneg = getPhrases(s2);
    }
}
