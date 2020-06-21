package chat.socket.server.group;

import chat.socket.server.ServerPortManager;
import chat.socket.server.TeamChatServerManager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class GroupListServer {
    private volatile boolean running;

    public GroupListServer() {
        setRunning(true);
    }

    public void startMultiThreadHandler(){
        Thread th = new Thread(() -> {
            try {
                ServerSocket ss = new ServerSocket(ServerPortManager.GROUP_LIST_PORT, 0,
                        InetAddress.getByName("0.0.0.0"));
                while (running) {
                    Socket socket = ss.accept();
                    Runnable connectionHandler = new GroupListHandler(socket);
                    Thread th1 = new Thread(connectionHandler);
                    th1.start();
                    TeamChatServerManager.threadList.add(th1);
                }
                ss.close();
            } catch (IOException uhe) {
                uhe.printStackTrace();
            }
        });
        th.start();
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
