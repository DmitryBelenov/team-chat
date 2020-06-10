package objects;

import javafx.beans.property.SimpleStringProperty;

public class User {
    private final SimpleStringProperty nick;
    private final SimpleStringProperty state;

    public User(String nick, String state) {
        this.nick = new SimpleStringProperty(nick);
        this.state = new SimpleStringProperty(state);
    }

    public String getNick() {
        return nick.get();
    }

    public String getState() {
        return state.get();
    }

    public void setNick(String nick) {
        this.nick.set(nick);
    }

    public void setState(String state) {
        this.state.set(state);
    }
}
