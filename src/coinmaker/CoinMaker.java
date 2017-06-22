package coinmaker;

import connection.Node;
import java.io.File;
import java.math.BigInteger;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.LinkedList;
import model.*;

public class CoinMaker {

    public static void main(String[] args) throws Exception {
        File pkFile = new File("publicKey.txt");
        File skFile = new File("privateKey.txt");
        if (!(pkFile.exists() && !pkFile.isDirectory() && skFile.exists() && !skFile.isDirectory())) {
            Node.initNode();
        }
        Node node = new Node("blockChain", 5000);
        node.start();

        /*byte[] prevHash = new byte[256];
        Block block;
        block = new Block(prevHash, new BigInteger("20"));
        LinkedList<BigInteger> ll = new LinkedList<BigInteger>();
        ll.add(new BigInteger("0"));
        ll.add(new BigInteger("1"));
        Transaction transaction;
        KeyPairGenerator senderKeyGen = KeyPairGenerator.getInstance("DSA");
        PrivateKey senderSK = Node.getPrivateKey("privateKey.txt");
        PublicKey senderPK = Node.getPublicKey("publicKey.txt");
        PublicKey receiverPK = Node.getPublicKey("publicKey.txt");
        block.addMinerTransaction(BigInteger.valueOf(1000), senderPK, senderSK);
        BlockChain instance = new BlockChain(5);
        Block newBlock = block.mine(5);
        instance.addBlock(newBlock);
        instance.saveBlockChain("blockchain");*/

        /*BlockChain bl = BlockChain.loadBlockChain("blockchain");
        LinkedList<Block> blocks = bl.getBlocks();
        System.out.println(bl.checkBlock(blocks.getFirst(), BigInteger.valueOf(2)));
        System.out.println(bl.checkBlock(blocks.getLast(), BigInteger.valueOf(1)));*/

    }

}
