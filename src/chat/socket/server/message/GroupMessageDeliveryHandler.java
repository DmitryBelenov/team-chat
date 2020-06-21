package chat.socket.server.message;

import chat.database.DBConnection;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GroupMessageDeliveryHandler implements Runnable {

    private Socket socket;

    public GroupMessageDeliveryHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = socket.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(inputStream);

            String id = dataInputStream.readUTF();

            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(DBConnection.getGroupMessages(id));

            oos.flush();
            socket.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
