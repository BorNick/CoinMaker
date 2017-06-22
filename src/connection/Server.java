package connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Block;
import model.BlockChain;

public class Server extends Thread {

    private ServerSocket serverSocket;
    private BlockChain blockChain;
    private Block newBlock;
    private boolean nodeOn;
    private int port;
    public boolean readyBlock;

    public Server(BlockChain blockChain, int port) {
        newBlock = null;
        this.blockChain = blockChain;
        this.port = port;
        readyBlock = false;
    }

    public Block getBlock() {
        readyBlock = false;
        return newBlock;
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
                ServerSendingThread clientThread = new ServerSendingThread(clientSocket);
                clientThread.start();
                clientThread.join();
            } catch (IOException ioe) {
                System.out.println("IOException");
            } catch (InterruptedException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }
}
