package connection;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Block;

public class ClientNode extends Thread {

    public Socket myClientSocket;
    Block block;

    public ClientNode(String ip, int port, Block block) throws IOException {
        myClientSocket = new Socket(ip, port);
        this.block = block;
    }

    @Override
    public void run() {

        OutputStream out;
        try {
            out = myClientSocket.getOutputStream();
            byte[] byteBlock = block.toByteArray();
            byte[] message = new byte[byteBlock.length + 1];
            message[0] = 0;
            System.arraycopy(byteBlock, 0, message, 1, byteBlock.length);
            out.write(message);
        } catch (IOException ex) {
            Logger.getLogger(ClientNode.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
