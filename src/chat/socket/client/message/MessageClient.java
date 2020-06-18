package chat.socket.client.message;

import chat.database.entity.MessageEntity;
import chat.socket.server.ServerPortManager;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class MessageClient {

    private String host;
    private MessageEntity messageEntity;

    public MessageClient(String host, MessageEntity messageEntity) {
        this.host = host;
        this.messageEntity = messageEntity;
    }

    public void send(){
        try {
            if (host != null) {
                Socket socket = new Socket(host, ServerPortManager.MESSAGE_PORT);
                ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
                os.writeObject(messageEntity);

                socket.close();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
