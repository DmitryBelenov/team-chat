package chat.socket.server.service;

import chat.socket.server.TeamChatServerManager;

import java.io.*;
import java.net.Socket;

public class OnDutyHandler implements Runnable {

    private Socket socket;

    OnDutyHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = socket.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(inputStream);

            String alias = dataInputStream.readUTF();
            
            OutputStream outputStream = socket.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

            dataOutputStream.writeUTF(aliasFactory(alias));

            dataOutputStream.flush();
            socket.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        TeamChatServerManager.threadList.remove(Thread.currentThread());
    }

    private String aliasFactory(String alias){
        if (alias.equals("connection")){
            return "ok";
        } else {
            return "no_service";
        }
    }
}
