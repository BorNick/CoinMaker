package connection;


import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import model.BlockChain;

public class Node {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private BlockChain blockChain;
    private boolean nodeOn;

    public Node(String filename) {
        try {
            serverSocket = new ServerSocket(5000);
        } catch (IOException ioe) {
            System.out.println("Could not create server socket on port 5000.");
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

    public Node(String ip, int port) throws IOException {
        recieveBlockChainFromUser(ip, port);
    }

    public void recieveBlockChainFromUser(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        PrintWriter out = null;
        out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        out.print("give me BlockChain");
        DataInputStream dIn = new DataInputStream(clientSocket.getInputStream());

        int length = dIn.readInt();
        if (length > 0) {
            byte[] message = new byte[length];
            dIn.readFully(message, 0, message.length);
        }
    }

}
