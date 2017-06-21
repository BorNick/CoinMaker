package model;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
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
        newBlock.addMinerTransaction(BigInteger.TEN, senderPK, senderSK);
        newBlock = newBlock.mine(5);
        BlockChain instance = new BlockChain(5);
        instance.addBlock(block);
        if(instance.checkBlock(block, BigInteger.TEN)){
            fail();
        }
        if(!instance.checkBlock(newBlock, BigInteger.TEN)){
            fail();
        }
        prevHash = newBlock.hash();
        newBlock = new Block(prevHash, instance.getLastId());
        newTransaction = new Transaction(ll, new BigInteger("500"), new BigInteger("50"), senderPK, receiverPK, senderSK);
        newBlock.addTransaction(newTransaction);
        newBlock.addMinerTransaction(BigInteger.TEN, senderPK, senderSK);
        newBlock = newBlock.mine(5);
        if(instance.checkBlock(newBlock, BigInteger.TEN)){
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
    
    /**
     * Test of getBalance method, of class BlockChain.
     */
    @Test
    public void testGetBalance() throws Exception {
        System.out.println("getBalance");
        BlockChain instance = new BlockChain(5);
        instance.addBlock(block);
        byte[] prevHash = block.hash();
        Block newBlock = new Block(prevHash, new BigInteger("21"));
        LinkedList<BigInteger> ll = new LinkedList<BigInteger>();
        ll.add(new BigInteger("21"));
        SecureRandom sr = new SecureRandom();
        KeyPairGenerator receiverKeyGen = KeyPairGenerator.getInstance("DSA");
        receiverKeyGen.initialize(1024, sr);
        KeyPair keypair = receiverKeyGen.generateKeyPair();
        PublicKey receiverPK = keypair.getPublic();
        PrivateKey receiverSK = keypair.getPrivate();
        Transaction transaction = new Transaction(ll, new BigInteger("50"), new BigInteger("50"), senderPK, receiverPK, senderSK);
        newBlock.addTransaction(transaction);
        ll = new LinkedList<BigInteger>();
        transaction = new Transaction(ll, new BigInteger("50"), new BigInteger("11"), receiverPK, senderPK, receiverSK);
        newBlock.addTransaction(transaction);
        newBlock.addMinerTransaction(BigInteger.TEN, senderPK, senderSK);
        newBlock = newBlock.mine(5);
        instance.addBlock(newBlock);
        BigInteger result = instance.getBalance(senderPK, ll);
        if(result.compareTo(new BigInteger("71")) != 0){
            fail();
        }
        if(!(ll.get(0).equals(new BigInteger("22")) && ll.get(1).equals(new BigInteger("23"))&& ll.get(2).equals(new BigInteger("24")))){
            fail();
        }
    }
}
