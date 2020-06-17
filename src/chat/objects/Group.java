package chat.objects;

import javafx.beans.property.SimpleStringProperty;

public class Group {
    private final SimpleStringProperty name;

    public Group(String name) {
        this.name = new SimpleStringProperty(name);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String nick) {
        this.name.set(nick);
    }
}
