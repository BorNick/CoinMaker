package connection;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Block;
import model.BlockChain;
import model.Transaction;

public class Node extends Thread {

    private Socket clientSocket;
    private BlockChain blockChain;
    private Block block;
    private boolean nodeOn;
    private Scanner sysScanner;
    private Server server;

    public Node(String filename) {
        sysScanner = new Scanner(System.in);
        nodeOn = true;
        blockChain = new BlockChain(2);
        server = new Server(5000);
    }

    public void initNode() throws NoSuchAlgorithmException, IOException, NoSuchProviderException {
        KeyPairGenerator senderKeyGen = KeyPairGenerator.getInstance("DSA", "SUN");
        SecureRandom sr = new SecureRandom();
        senderKeyGen.initialize(1024, sr);
        //https://docs.oracle.com/javase/tutorial/security/apisign/vstep2.html
        KeyPair keypair = senderKeyGen.generateKeyPair();
        PrivateKey sK = keypair.getPrivate();
        PublicKey pK = keypair.getPublic();
        FileOutputStream fos = new FileOutputStream("privateKey.txt");
        fos.write(sK.getEncoded());
        fos.flush();
        fos.close();
        fos = new FileOutputStream("secretKey.txt");
        fos.write(pK.getEncoded());
        fos.flush();
        fos.close();
    }

    public void run() {
        while (nodeOn) {
            if (sysScanner.hasNextLine()) {
                try {
                    checkInput(sysScanner.nextLine());
                } catch (NoSuchAlgorithmException ex) {
                    System.out.println("NoSuchAlgorithmException");
                } catch (IOException ex) {
                    System.out.println("IOException");
                } catch (Exception ex) {
                    Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void createTransaction() throws Exception {
        int amount;
        String publicKey;
        System.out.println("Amount?");
        amount = sysScanner.nextInt();
        //checkWallet(PublicKey p);
        System.out.println("Public key of the recipient:");
        publicKey = sysScanner.nextLine();
        LinkedList<BigInteger> list = new LinkedList();
        //Transaction newTransaction = new Transaction(list, left, BigInteger.valueOf(amount), getPublicKey("publicKey.txt"), recieverPcK, getPrivateKey("privateKey.txt"));
        Transaction newTransaction = new Transaction(list, BigInteger.ZERO, BigInteger.valueOf(amount), getPublicKey("publicKey.txt"), getPublicKey("publicKey.txt"), getPrivateKey("privateKey.txt"));
        System.out.println(newTransaction.toString() + "/n make this transaction? /n Y/N:");
        if (sysScanner.nextLine().equals("Y")) {
            block.addTransaction(newTransaction);
            //sendTransactionToUsers(newTransaction);
        }
    }

    public PrivateKey getPrivateKey(String filename) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        FileInputStream keyfis = new FileInputStream("privateKey.txt");
        byte[] sK = new byte[keyfis.available()];
        keyfis.read(sK);
        PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(sK);
        KeyFactory keyFactory = KeyFactory.getInstance("DSA", "SUN");
        PrivateKey privateKey = keyFactory.generatePrivate(privKeySpec);
        return privateKey;
    }

    public PublicKey getPublicKey(String filename) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException {
        FileInputStream keyfis = new FileInputStream("privateKey.txt");
        byte[] pK = new byte[keyfis.available()];
        keyfis.read(pK);
        X509EncodedKeySpec publKeySpec = new X509EncodedKeySpec(pK);
        KeyFactory keyFactory = KeyFactory.getInstance("DSA", "SUN");
        PublicKey publicKey = keyFactory.generatePublic(publKeySpec);
        return publicKey;
    }

    public void checkInput(String input) throws Exception {
        if (input.equals("create transaction")) {
            createTransaction();
        }
        if (input.equals("init")) {
            initNode();
            System.out.println("you have been initialized");
        }
        if (input.equals("start server")) {
            server.start();
            System.out.println("Server has been started");
        }
        if (input.equals("get blockChain"))
        {
            //blockChain = recieveBlockChainFromUser("10", 10);
        }
    }

    public BlockChain recieveBlockChainFromUser(String ip, int port) throws IOException {
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
        return new BlockChain(3);
    }

}
