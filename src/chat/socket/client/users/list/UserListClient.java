package chat.socket.client.users.list;

import chat.database.entity.UserEntity;
import chat.socket.server.ServerPortManager;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class UserListClient {

    private String host;
    private String alias;

    public UserListClient(String host, String alias) {
        this.host = host;
        this.alias = alias;
    }

    @SuppressWarnings("unchecked")
    public synchronized List<UserEntity> get(){
        List<UserEntity> list = new ArrayList<>();
        try {
            if (host != null) {
                Socket socket = new Socket(host, ServerPortManager.USER_LIST_PORT);
                OutputStream outputStream = socket.getOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

                dataOutputStream.writeUTF(alias);

                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                try {
                    list = (List<UserEntity>) ois.readObject();
                } catch (ClassCastException cce){
                    cce.printStackTrace();
                }

                outputStream.flush();
                outputStream.close();
                socket.close();
            }
        } catch (IOException | ClassNotFoundException ioe) {
            ioe.printStackTrace();
        }
        return list;
    }
}
