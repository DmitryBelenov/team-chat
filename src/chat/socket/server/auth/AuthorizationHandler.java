package chat.socket.server.auth;

import chat.database.DBConnection;
import chat.database.entity.UserEntity;
import chat.socket.server.TeamChatServerManager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class AuthorizationHandler implements Runnable{

    private Socket socket;

    AuthorizationHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            String[] userAuthData = (String[]) ois.readObject();

            UserEntity authorizedUser = DBConnection.authorization(userAuthData);

            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(authorizedUser);

            oos.flush();
            socket.close();
        } catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
        TeamChatServerManager.threadList.remove(Thread.currentThread());
    }
}
