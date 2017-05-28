package connection;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientNode {

    static class clientSendingThread extends Thread {

        public Socket myClientSocket;
        private boolean ClientOn = true;

        public clientSendingThread(Socket s) {
            myClientSocket = s;
        }

        public void run() {
            PrintWriter out = null;

            try {
                out = new PrintWriter(new OutputStreamWriter(myClientSocket.getOutputStream()));
                Scanner scanner = new Scanner(System.in);

                while (ClientOn) {
                    String s = scanner.nextLine();
                    out.println(s);
                    out.flush();
                }
            } catch (IOException e) {
                System.out.println("IOException");
            }
            out.close();
        }

    }

}
