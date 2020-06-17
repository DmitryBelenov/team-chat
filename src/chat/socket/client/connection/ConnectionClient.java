package chat.socket.client.connection;

import chat.socket.server.ServerPortManager;
import javafx.scene.control.Alert;

import java.io.*;
import java.net.Socket;

public class ConnectionClient {
    private String host;

    public ConnectionClient(String host) {
        this.host = host;
    }

    public synchronized boolean check(){
        boolean res = false;
        try {
            if (host != null) {
                Socket socket = new Socket(host, ServerPortManager.SERVICE_PORT);
                OutputStream outputStream = socket.getOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

                dataOutputStream.writeUTF("connection");

                InputStream inputStream = socket.getInputStream();
                DataInputStream dataInputStream = new DataInputStream(inputStream);

                String alias = dataInputStream.readUTF();

                res = alias.equals("ok");
                socket.close();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            lostConnectionError();
        }
        return res;
    }

    private void lostConnectionError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Соединение прервано");
        alert.setContentText("Хост "+host+" не доступен.\nПопробуйте повторное подключение");

        alert.showAndWait();
    }
}
