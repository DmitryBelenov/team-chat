package chat.socket.client.message;

import chat.database.entity.MessageEntity;
import chat.socket.server.ServerPortManager;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class GroupMessageDeliveryClient {

    private String host;
    private String id;

    public GroupMessageDeliveryClient(String host, String id) {
        this.host = host;
        this.id = id;
    }

    @SuppressWarnings("unchecked")
    public synchronized List<MessageEntity> refresh(){
        List<MessageEntity> list = new LinkedList<>();
        try {
            if (host != null) {
                Socket socket = new Socket(host, ServerPortManager.GROUP_MESSAGE_DELIVERY_PORT);
                OutputStream outputStream = socket.getOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

                dataOutputStream.writeUTF(id);

                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                try {
                    list = (List<MessageEntity>) ois.readObject();
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
