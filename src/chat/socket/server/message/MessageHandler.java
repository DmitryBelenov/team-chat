package chat.socket.server.message;

import chat.database.DBConnection;
import chat.database.entity.MessageEntity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class MessageHandler implements Runnable {

    private Socket socket;

    public MessageHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            MessageEntity messageEntity = (MessageEntity) ois.readObject();

            DBConnection.persist(messageEntity);

            socket.close();
        } catch (IOException | ClassNotFoundException ioe) {
            ioe.printStackTrace();
        }
    }
}
