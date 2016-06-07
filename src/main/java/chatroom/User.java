package chatroom;

import java.util.Date;

public class User {
    private String name;
    private String password;
    private Date lastInterractionTime;

    public User(String name, String password,Date lastInterractionTime) {
        this.name = name;
        this.password=password;
        this.lastInterractionTime = lastInterractionTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getLastInterractionTime() {
        return lastInterractionTime;
    }

    public void setLastInterractionTime(Date lastInterractionTime) {
        this.lastInterractionTime = lastInterractionTime;
    }

}
