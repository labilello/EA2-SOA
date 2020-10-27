package ar.com.lbilello.soa.models;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Timer;

@SuppressWarnings("serial")
public class User implements Serializable {
    String token;
    String token_refresh;
    Calendar timeToken;

    public User(String token, String token_refresh) {
        this.token = token;
        this.token_refresh = token_refresh;
        this.timeToken = Calendar.getInstance();
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken_refresh() {
        return token_refresh;
    }

    public void setToken_refresh(String token_refresh) {
        this.token_refresh = token_refresh;
    }

    public Calendar getTimeToken() {
        return timeToken;
    }

    public void setTimeToken(Calendar timeToken) {
        this.timeToken = timeToken;
    }

    @Override
    public String toString() {
        return "User{" +
                "token='" + token + '\'' +
                ", token_refresh='" + token_refresh + '\'' +
                '}';
    }
}
