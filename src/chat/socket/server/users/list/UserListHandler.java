package chat.socket.server.users.list;

import chat.database.DBConnection;
import chat.socket.server.TeamChatServerManager;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class UserListHandler implements Runnable {

    private Socket socket;

    UserListHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = socket.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(inputStream);

            String alias = dataInputStream.readUTF();

            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(DBConnection.getUsers(alias));

            oos.flush();
            socket.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        TeamChatServerManager.threadList.remove(Thread.currentThread());
    }
}
