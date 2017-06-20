package model;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.LinkedList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class BlockTest {
    
    public BlockTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
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
     * Test of addTransaction method, of class Block.
     */
    @Test
    public void testAddTransaction() throws Exception {
        System.out.println("addTransaction");
        LinkedList<BigInteger> ll = new LinkedList<BigInteger>();
        ll.add(new BigInteger("9"));
        ll.add(new BigInteger("10"));
        KeyPairGenerator senderKeyGen = KeyPairGenerator.getInstance("DSA");
        SecureRandom sr = new SecureRandom();
        senderKeyGen.initialize(1024, sr);
        KeyPair keypair = senderKeyGen.generateKeyPair();
        PrivateKey senderSK = keypair.getPrivate();
        PublicKey senderPK = keypair.getPublic();
        KeyPairGenerator receiverKeyGen = KeyPairGenerator.getInstance("DSA");
        receiverKeyGen.initialize(1024, sr);
        keypair = receiverKeyGen.generateKeyPair();
        PublicKey receiverPK = keypair.getPublic();
        Transaction transaction = new Transaction(ll, new BigInteger("100"), new BigInteger("50"), senderPK, receiverPK, senderSK);
        byte[] prevHash = new byte[256];
        Block instance = new Block(prevHash);
        instance.addTransaction(transaction);
        if(instance.getTransactions().isEmpty())
            fail();
    }

    /**
     * Test of hash method, of class Block.
     */
    /*@Test
    public void testHash() throws Exception {
        System.out.println("hash");
        byte[] prevHash = new byte[256];
        Block instance = new Block(prevHash);
        byte[] expResult = null;
        byte[] result = instance.hash();
        //assertArrayEquals(expResult, result);
    }*/

    /**
     * Test of mine method, of class Block.
     */
    @Test
    public void testMine() throws Exception {
        System.out.println("mine");
        int zerosRule = 0;
        byte[] prevHash = new byte[256];
        Block instance = new Block(prevHash);
        Block result = instance.mine(zerosRule);
        int numOfZeroes = 0;
        byte[] hash = result.hash();
            for (int i = 0; i <= zerosRule / 8; i++) {
                for (int j = 0; j < (i == zerosRule / 8? zerosRule % 8: 8); j++) {
                    if ((hash[hash.length - i - 1] & (1 << j)) == 0) {
                        numOfZeroes++;
                    }
                }
            }
            if (numOfZeroes < zerosRule) {
                fail();
            }
    }

    /**
     * Test of getprevHash method, of class Block.
     */
    @Test
    public void testGetprevHash() {
        System.out.println("getprevHash");
        byte[] prevHash = new byte[256];
        Block instance = new Block(prevHash);
        byte[] expResult = new byte[256];
        byte[] result = instance.getprevHash();
        assertArrayEquals(expResult, result);
    }

    /**
     * Test of getTransactions method, of class Block.
     */
    @Test
    public void testGetTransactions() throws Exception {
        System.out.println("getTransactions");
        byte[] prevHash = new byte[256];
        Block instance = new Block(prevHash);
        LinkedList<BigInteger> ll = new LinkedList<BigInteger>();
        ll.add(new BigInteger("9"));
        ll.add(new BigInteger("10"));
        KeyPairGenerator senderKeyGen = KeyPairGenerator.getInstance("DSA");
        SecureRandom sr = new SecureRandom();
        senderKeyGen.initialize(1024, sr);
        KeyPair keypair = senderKeyGen.generateKeyPair();
        PrivateKey senderSK = keypair.getPrivate();
        PublicKey senderPK = keypair.getPublic();
        KeyPairGenerator receiverKeyGen = KeyPairGenerator.getInstance("DSA");
        receiverKeyGen.initialize(1024, sr);
        keypair = receiverKeyGen.generateKeyPair();
        PublicKey receiverPK = keypair.getPublic();
        Transaction transaction = new Transaction(ll, new BigInteger("100"), new BigInteger("50"), senderPK, receiverPK, senderSK);
        instance.addTransaction(transaction);
        HashMap<BigInteger, Transaction> expResult = new HashMap<BigInteger, Transaction>();
        expResult.put(BigInteger.ONE, transaction);
        HashMap<BigInteger, Transaction> result = instance.getTransactions();
        assertEquals(expResult, result);
    }

    /**
     * Test of checkPreviousHash method, of class Block.
     */
    @Test
    public void testCheckPreviousHash() throws Exception {
        System.out.println("checkPreviousHash");
        byte[] prevHash = new byte[256];
        Block prevBlock = new Block(prevHash);
        Block instance = new Block(prevHash);
        if(instance.checkPreviousHash(prevBlock)){
            fail();
        }
    }
    
}
