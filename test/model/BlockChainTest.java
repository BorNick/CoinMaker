package model;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.LinkedList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class BlockChainTest {
    
    static Transaction transaction;
    static PrivateKey senderSK;
    static PublicKey senderPK;
    static Block block;
    
    public BlockChainTest() {
    }
    
    @BeforeClass
    public static void setUpClass() throws Exception {
        byte[] prevHash = new byte[256];
        block = new Block(prevHash, new BigInteger("20"));
        LinkedList<BigInteger> ll = new LinkedList<BigInteger>();
        ll.add(new BigInteger("9"));
        ll.add(new BigInteger("10"));
        KeyPairGenerator senderKeyGen = KeyPairGenerator.getInstance("DSA");
        SecureRandom sr = new SecureRandom();
        senderKeyGen.initialize(1024, sr);
        KeyPair keypair = senderKeyGen.generateKeyPair();
        senderSK = keypair.getPrivate();
        senderPK = keypair.getPublic();
        KeyPairGenerator receiverKeyGen = KeyPairGenerator.getInstance("DSA");
        receiverKeyGen.initialize(1024, sr);
        keypair = receiverKeyGen.generateKeyPair();
        PublicKey receiverPK = keypair.getPublic();
        transaction = new Transaction(ll, new BigInteger("100"), new BigInteger("50"), senderPK, receiverPK, senderSK);
        block.addTransaction(transaction);
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of addBlock method, of class BlockChain.
     */
    @Test
    public void testAddBlock() {
        System.out.println("addBlock");
        Block newBlock = block;
        BlockChain instance = new BlockChain(5);
        instance.addBlock(newBlock);
        if(instance.blocks.size() != 1){
            fail();
        }
    }

    /**
     * Test of checkBlock method, of class BlockChain.
     */
    @Test
    public void testCheckBlock() throws Exception {
        System.out.println("checkBlock");
        byte[] prevHash = block.hash();
        Block newBlock = new Block(prevHash, new BigInteger("21"));
        LinkedList<BigInteger> ll = new LinkedList<BigInteger>();
        ll.add(new BigInteger("21"));
        SecureRandom sr = new SecureRandom();
        KeyPairGenerator receiverKeyGen = KeyPairGenerator.getInstance("DSA");
        receiverKeyGen.initialize(1024, sr);
        KeyPair keypair = receiverKeyGen.generateKeyPair();
        PublicKey receiverPK = keypair.getPublic();
        Transaction newTransaction = new Transaction(ll, new BigInteger("50"), new BigInteger("50"), senderPK, receiverPK, senderSK);
        newBlock.addTransaction(newTransaction);
        newBlock = newBlock.mine(5);
        byte[] hash = newBlock.hash();
        BlockChain instance = new BlockChain(5);
        instance.addBlock(block);
        /*if(instance.checkBlock(block)){
            fail();
        }*/
        if(!instance.checkBlock(newBlock)){
            fail();
        }

    }

    /**
     * Test of getLastId method, of class BlockChain.
     */
    @Test
    public void testGetLastId() {
        System.out.println("getLastId");
        BlockChain instance = new BlockChain(5);
        instance.addBlock(block);
        BigInteger expResult = new BigInteger("21");
        BigInteger result = instance.getLastId();
        assertEquals(expResult, result);
    }

    /**
     * Test of toByteArray method, of class BlockChain.
     */
    @Test
    public void testToByteArray() throws Exception {
        System.out.println("toByteArray");
        BlockChain instance = new BlockChain(5);
        instance.addBlock(block);
        BlockChain result = new BlockChain(instance.toByteArray());
        if(!instance.blocks.equals(result.blocks)){
            fail();
        }
    }
    
}
