package chat.socket.server.registration;

import chat.database.DBConnection;
import chat.database.entity.UserEntity;
import chat.socket.server.TeamChatServerManager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
public class RegistrationHandler implements Runnable {

    private Socket socket;

    RegistrationHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            UserEntity userEntity = (UserEntity) ois.readObject();

            DBConnection.persist(userEntity);

            socket.close();
        } catch (IOException | ClassNotFoundException ioe) {
            ioe.printStackTrace();
        }
        TeamChatServerManager.threadList.remove(Thread.currentThread());
    }
}
