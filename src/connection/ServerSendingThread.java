package connection;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Block;
import model.Serializer;

class ServerSendingThread extends Thread {

    private Socket sendingSocket;
    private Block newBlock;
    public boolean readyBlock;

    public ServerSendingThread(Socket s) {
        sendingSocket = s;
        newBlock = null;

    }

    public void run() {
        BufferedOutputStream out;

        System.out.println("Accepted Client Address - " + sendingSocket.getInetAddress().getHostName());

        try {
            InputStream in = sendingSocket.getInputStream();
            sleep(50);
            byte command = (byte) in.read();
            if (command == 0) {
                int length = in.available();
                byte[] message = new byte[length];
                in.read(message);
                newBlock = (Block) Serializer.deserialize(message);

                try {
                    if (Node.blockChain.checkBlock(newBlock, BigInteger.valueOf(10)) == true) {
                        Node.blockChain.addBlock(newBlock);
                        Node.blockChain.saveBlockChain("blockChain");
                    }
                } catch (Exception ex) {
                    Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (command == 31) {
                OutputStream dOut = sendingSocket.getOutputStream();
                byte[] message = Node.blockChain.toByteArray();
                dOut.write(message);
            }

        } catch (IOException e) {
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerSendingThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(ServerSendingThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
