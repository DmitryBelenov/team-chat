package chat.database.entity;

import java.io.Serializable;
import java.util.Date;

public class MessageEntity implements Serializable {
    private static final long serialVersionUID = -1094022528844232614L;

    private String id;
    private String msgText;
    private String fromId;
    private String toId;
    private byte[] file;
    private String fileName;
    private boolean groupMsg;
    private Date sendDate;
    private boolean received;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMsgText() {
        return msgText;
    }

    public void setMsgText(String msgText) {
        this.msgText = msgText;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isGroupMsg() {
        return groupMsg;
    }

    public void setGroupMsg(boolean groupMsg) {
        this.groupMsg = groupMsg;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public boolean isReceived() {
        return received;
    }

    public void setReceived(boolean received) {
        this.received = received;
    }
}
