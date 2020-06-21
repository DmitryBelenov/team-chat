package chat.socket.client.auth;

import chat.database.entity.UserEntity;
import chat.socket.server.ServerPortManager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class AuthorizationClient {

    private String host;
    private String[] authData;

    public AuthorizationClient(String host, String[] authData) {
        this.host = host;
        this.authData = authData;
    }

    public UserEntity auth(){
        UserEntity userEntity = null;
        try {
            Socket socket = new Socket(host, ServerPortManager.AUTHORIZATION_PORT);

            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(authData);

            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            userEntity = (UserEntity) ois.readObject();

            oos.flush();
            socket.close();
        } catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
        return userEntity;
    }
}
