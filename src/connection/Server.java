package connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import model.BlockChain;

public class Server extends Thread {

    private ServerSocket serverSocket;
    private BlockChain blockChain;
    private boolean nodeOn;
    private int port;

    public Server(int port) {
        this.port=port;
    }

    public void run() {
        nodeOn = true;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException ioe) {
            System.out.println("Could not create server socket on port " + port + ".");
            System.exit(-1);
        }
        while (nodeOn) {
            try {
                Socket clientSocket = serverSocket.accept();
                ServerSendingThread clientThread = new ServerSendingThread(clientSocket, blockChain);
                clientThread.start();

            } catch (IOException ioe) {
                System.out.println("IOException");
            }

        }
    }



}
