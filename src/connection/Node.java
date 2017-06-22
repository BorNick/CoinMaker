package connection;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Block;
import model.BlockChain;
import model.Serializer;
import model.Transaction;

public class Node extends Thread {

    private Socket clientSocket;
    public static BlockChain blockChain;
    private Block block;
    private boolean nodeOn;
    private Scanner sysScanner;
    private Server server;

    public Node(String filename, int port) throws Exception {
        sysScanner = new Scanner(System.in);
        nodeOn = true;
        blockChain = new BlockChain(5);
        blockChain = BlockChain.loadBlockChain(filename);
        LinkedList<Block> bl = blockChain.getBlocks();
        Block lastBlock = bl.getLast();
        block = new Block(lastBlock.hash(), lastBlock.getLastId());
        server = new Server(blockChain, port);

    }

    public void initNode() throws Exception {
        KeyPairGenerator senderKeyGen = KeyPairGenerator.getInstance("DSA");
        senderKeyGen.initialize(1024);
        //https://docs.oracle.com/javase/tutorial/security/apisign/vstep2.html
        KeyPair keypair = senderKeyGen.generateKeyPair();
        PrivateKey sK = keypair.getPrivate();
        PublicKey pK = keypair.getPublic();
        FileOutputStream fos = new FileOutputStream("privateKey.txt");
        fos.write(sK.getEncoded());
        fos.flush();
        fos.close();
        fos = new FileOutputStream("publicKey.txt");
        fos.write(pK.getEncoded());
        fos.flush();
        fos.close();
    }

    public void run() {
        Block newBlock;
        while (nodeOn) {
            if (sysScanner.hasNextLine()) {
                try {
                    checkInput(sysScanner.nextLine());
                } catch (NoSuchAlgorithmException ex) {
                    System.out.println("NoSuchAlgorithmException");
                } catch (IOException ex) {
                    System.out.println("Try again");
                } catch (Exception ex) {
                    Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (server.readyBlock == true) {
                newBlock = server.getBlock();
                try {
                    if (blockChain.checkBlock(newBlock, BigInteger.valueOf(10)) == true) {
                        blockChain.addBlock(newBlock);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void createTransaction() throws Exception {
        int amount;
        LinkedList<Block> bl = blockChain.getBlocks();
        Block lastBlock = bl.getLast();
        block = new Block(lastBlock.hash(), lastBlock.getLastId());
        String pathToPublicKey = "";
        System.out.println("Amount?");
        amount = sysScanner.nextInt();
        sysScanner.nextLine();
        PrivateKey sK = getPrivateKey("privateKey.txt");
        PublicKey pK = getPublicKey("publicKey.txt");
        LinkedList<BigInteger> list = new LinkedList();
        BigInteger balance = blockChain.getBalance(pK, list);
        if (balance.compareTo(BigInteger.valueOf(amount)) == -1) {
            System.out.println("Not enough money");
            return;
        }
        System.out.println("Path to Public key of the recipient:");
        pathToPublicKey = sysScanner.nextLine();
        PublicKey recieverPK = getPublicKey(pathToPublicKey);
        Transaction newTransaction = new Transaction(list, balance.subtract(BigInteger.valueOf(amount)), BigInteger.valueOf(amount), pK, recieverPK, sK);

        System.out.println(newTransaction.toString() + "\n make this transaction? \n Y/N:");
        if (sysScanner.nextLine().equals("Y")) {
            block.addTransaction(newTransaction);
            System.out.println("start mining...");
            System.out.println("put amount of your mine");
            block.addMinerTransaction(BigInteger.valueOf(sysScanner.nextInt()), pK, sK);
            Block newBlock = block.mine(5);
            blockChain.addBlock(newBlock);
            System.out.println("Transaction was succesfully created");
            sendBlockToUsers("addresses.txt", newBlock);
        }
    }

    public static PrivateKey getPrivateKey(String filename) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        FileInputStream keyfis = new FileInputStream(filename);
        byte[] sK = new byte[keyfis.available()];
        keyfis.read(sK);
        PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(sK);
        KeyFactory keyFactory = KeyFactory.getInstance("DSA", "SUN");
        PrivateKey privateKey = keyFactory.generatePrivate(privKeySpec);
        return privateKey;
    }

    public static PublicKey getPublicKey(String filename) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException {
        FileInputStream keyfis = new FileInputStream(filename);
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
        if (input.equals("get blockChain")) {
            System.out.println("ip?");
            String ip = sysScanner.nextLine();
            System.out.println("Port?");
            int port = sysScanner.nextInt();
            sysScanner.nextLine();
            blockChain = recieveBlockChainFromUser(ip, port);
        }
        if (input.equals("balance")) {
            showBalance();
        }
    }

    public void showBalance() throws Exception {
        PublicKey pK = getPublicKey("publicKey.txt");
        LinkedList list = new LinkedList();
        System.out.println(blockChain.getBalance(pK, list));
    }

    public BlockChain recieveBlockChainFromUser(String ip, int port) throws Exception {
        clientSocket = new Socket(ip, port);
        OutputStream out = clientSocket.getOutputStream();
        out.write(31);
        sleep(600);
        InputStream dIn = clientSocket.getInputStream();
        int length = dIn.available();
        //System.out.println(length);
        byte[] message = new byte[length];
        dIn.read(message);
        //System.out.println(message.length);
        BlockChain bc = (BlockChain) Serializer.deserialize(message);
        extendBlockChain(blockChain,bc, BigInteger.valueOf(10));
        //System.out.println(extendBlockChain(blockChain,bc, BigInteger.valueOf(10)));
        bc.saveBlockChain("blockChain");
        return bc;
    }

    public boolean extendBlockChain(BlockChain shortChain, BlockChain longChain, BigInteger maxReward) throws Exception {
        ListIterator<Block> iter = longChain.getBlocks().listIterator(shortChain.getBlocks().size());
        while (iter.hasNext()) {
            Block newBlock = iter.next();
            if (shortChain.checkBlock(newBlock, maxReward)) {
                shortChain.addBlock(newBlock);
            } else {
                return false;
            }
        }
        return true;
    }

    public void sendBlockToUsers(String filename, Block block) {
        try {
            Scanner scanner = new Scanner(new File(filename));
            int size = scanner.nextInt();
            ClientNode[] threads = new ClientNode[size];
            scanner.nextLine();
            for (int i = 0; i < size; i++) {
                String address = scanner.nextLine();
                Scanner adrScanner = new Scanner(address);
                String ip = adrScanner.next();
                int port = adrScanner.nextInt();
                threads[i] = new ClientNode(ip, port, block);
                threads[i].start();
            }
        } catch (IOException e) {
        }
    }

}
