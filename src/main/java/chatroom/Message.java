package chatroom;

import java.util.Date;

public class Message {
    private User author;
    private String mes;
    private Date timestamp;

    public Message(User author, String mes, Date timestamp) {
        this.author = author;
        this.mes = mes;
        this.timestamp = timestamp;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
