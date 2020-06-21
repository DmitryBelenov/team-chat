package chat.objects;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.Serializable;

public class GroupUserTableView implements Serializable {

    private static final long serialVersionUID = 5919478277520766974L;

    private final StringProperty nick;
    private final BooleanProperty add;
    private final StringProperty name;

    public GroupUserTableView(String nick, String name, boolean add) {
        this.nick = new SimpleStringProperty(nick);
        this.name = new SimpleStringProperty(name);
        this.add = new SimpleBooleanProperty(add);
    }

    public String getNick() {
        return nick.get();
    }

    public StringProperty nickProperty() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick.set(nick);
    }

    public boolean isAdd() {
        return add.get();
    }

    public BooleanProperty addProperty() {
        return add;
    }

    public void setAdd(boolean add) {
        this.add.set(add);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }
}
