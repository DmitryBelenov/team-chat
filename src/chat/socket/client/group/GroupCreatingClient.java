package chat.socket.client.group;

import chat.objects.GroupCommonData;
import chat.socket.server.ServerPortManager;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GroupCreatingClient {

    private String host;
    private GroupCommonData groupCommonData;

    public GroupCreatingClient(String host, GroupCommonData groupCommonData) {
        this.host = host;
        this.groupCommonData = groupCommonData;
    }

    public String createGroup() {
        String result;
        try {
            Socket socket = new Socket(host, ServerPortManager.GROUP_CREATING_PORT);

            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(groupCommonData);

            InputStream inputStream = socket.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(inputStream);

            result = dataInputStream.readUTF();

            oos.flush();
            socket.close();
        } catch (IOException e) {
            result = "При создании группы произошла ошибка доступа к серверу";
            e.printStackTrace();
        }
        return result;
    }
}
