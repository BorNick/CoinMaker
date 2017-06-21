package model;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.LinkedList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class TransactionTest {
    
    static Transaction transaction;
    static PrivateKey senderSK;
    static PublicKey senderPK;
    static PrivateKey receiverSK;
    static PublicKey receiverPK;
    
    public TransactionTest() {
    }
    
    @BeforeClass
    public static void setUpClass() throws Exception {
        LinkedList<BigInteger> ll = new LinkedList<BigInteger>();
        ll.add(new BigInteger("9"));
        ll.add(new BigInteger("10"));
        KeyPairGenerator senderKeyGen = KeyPairGenerator.getInstance("DSA");
        SecureRandom sr = new SecureRandom();
        senderKeyGen.initialize(1024, sr);
        KeyPair keypair = senderKeyGen.generateKeyPair();
        senderSK = keypair.getPrivate();
        senderPK = keypair.getPublic();
        keypair = senderKeyGen.generateKeyPair();
        receiverSK = keypair.getPrivate();
        receiverPK = keypair.getPublic();
        transaction = new Transaction(ll, new BigInteger("10"), new BigInteger("15"), senderPK, receiverPK, senderSK);
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
     * Test of sign method, of class Transaction.
     */
    @Test
    public void testSign() throws Exception {
        System.out.println("sign");
        
        byte[] result = transaction.sign(senderSK);
        Signature sig = Signature.getInstance("SHA1withDSA", "SUN");
        sig.initVerify(senderPK);
        byte[] data = transaction.toBAForSign();
        sig.update(data, 0, data.length);
        if(!sig.verify(result)){
            fail();
        }
        result = transaction.sign(receiverSK);
        if(sig.verify(result)){
            fail();
        }
    }

    /**
     * Test of checkSign method, of class Transaction.
     */
    @Test
    public void testCheckSign() throws Exception {
        System.out.println("checkSign");
        if(!transaction.checkSign()){
            fail();
        }
    }

    /**
     * Test of toByteArray method, of class Transaction.
     */
    @Test
    public void testToByteArray() throws Exception {
        System.out.println("toByteArray");
        Transaction result = new Transaction(transaction.toByteArray());
        assertEquals(transaction, result);
    }
}
