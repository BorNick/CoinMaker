package connection;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import model.BlockChain;

class ServerSendingThread extends Thread {

    private Socket sendingSocket;
    private BlockChain blockChain;

    public ServerSendingThread(Socket s, BlockChain blockChain) {
        sendingSocket = s;
        this.blockChain = blockChain;

    }

    public void run() {
        BufferedOutputStream out;

        System.out.println("Accepted Client Address - " + sendingSocket.getInetAddress().getHostName());

        try {
            DataOutputStream dOut = new DataOutputStream(sendingSocket.getOutputStream());
            byte[] message = blockChain.toByteArray();

            dOut.writeInt(message.length); // write length of the message
            dOut.write(message);

        } catch (IOException e) {
        }

    }
}
