package chat.database.entity;

import java.io.Serializable;
import java.util.Date;

public class UserEntity implements Serializable {
    private static final long serialVersionUID = -7321857190872553382L;

    private String id;
    private String nickname;
    private String username;
    private String userRole;
    private String email;
    private String passwordHex;
    private boolean online;
    private boolean blocked;
    private Date createDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHex() {
        return passwordHex;
    }

    public void setPasswordHex(String passwordHex) {
        this.passwordHex = passwordHex;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
