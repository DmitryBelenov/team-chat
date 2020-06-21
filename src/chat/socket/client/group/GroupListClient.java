package chat.socket.client.group;

import chat.database.entity.GroupEntity;
import chat.socket.server.ServerPortManager;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GroupListClient {

    private String host;
    private String userId;

    public GroupListClient(String host, String userId) {
        this.host = host;
        this.userId = userId;
    }

    @SuppressWarnings("unchecked")
    public synchronized List<GroupEntity> get(){
        List<GroupEntity> list = new ArrayList<>();
        try {
            if (host != null) {
                Socket socket = new Socket(host, ServerPortManager.GROUP_LIST_PORT);
                OutputStream outputStream = socket.getOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

                dataOutputStream.writeUTF(userId);

                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                try {
                    list = (List<GroupEntity>) ois.readObject();
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
