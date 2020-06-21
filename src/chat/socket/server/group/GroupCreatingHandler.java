package chat.socket.server.group;

import chat.database.DBConnection;
import chat.objects.GroupCommonData;
import chat.socket.server.TeamChatServerManager;

import java.io.*;
import java.net.Socket;

public class GroupCreatingHandler implements Runnable {

    private Socket socket;

    GroupCreatingHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            GroupCommonData groupCommonData = (GroupCommonData) ois.readObject();

            OutputStream outputStream = socket.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

            dataOutputStream.writeUTF(DBConnection.createGroup(groupCommonData));

            outputStream.flush();
            socket.close();
        } catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
        TeamChatServerManager.threadList.remove(Thread.currentThread());
    }
}
