package model;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class Block {
    private byte[] prevHash;
    public byte[] nonce;
    private HashMap<BigInteger, Transaction> transactions;
    private BigInteger lastId;
    
    public Block(){
        transactions = new HashMap<BigInteger, Transaction>();
        lastId = BigInteger.ZERO;
    }
    
    public Block(BigInteger lastId){
        transactions = new HashMap<BigInteger, Transaction>();
        this.lastId = lastId;
    }
    
    public void addTransaction(Transaction transaction){
        lastId = lastId.add(BigInteger.ONE);
        transactions.put(lastId, transaction);
    }
    
    public byte[] hash() throws NoSuchAlgorithmException, IOException{
        MessageDigest digest = MessageDigest.getInstance("SHA1");
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(transactions);
        byte[] arrTr = byteOut.toByteArray();
        byte[] data = new byte[prevHash.length + nonce.length + arrTr.length];
        System.arraycopy(prevHash, 0, data, 0, prevHash.length);
        System.arraycopy(nonce, 0, data, prevHash.length, nonce.length);
        System.arraycopy(arrTr, 0, data, prevHash.length + nonce.length, arrTr.length);
        return digest.digest(data);
    }
    
    public Block mine(int zerosRule) throws Exception {
        byte[] hash;
        Random rnd = new Random();
        while (true) {
            int numOfZeroes = 0;
            hash = this.hash();
            for (int i = 0; i <= zerosRule / 8; i++) {
                for (int j = 0; j < (i == zerosRule / 8? zerosRule % 8: 8); j++) {
                    if ((hash[hash.length - i] & (1 << j)) == 0) {
                        numOfZeroes++;
                    }
                }
            }
            if (numOfZeroes >= zerosRule) {
                return this;
            } else {
                rnd.nextBytes(nonce);
            }
        }
    }
    
    public byte[] getprevHash()
    {
        return prevHash;
    }
    
    public HashMap<BigInteger, Transaction> getTransactions(){
        return transactions;
    }
    
    public void setLastId(BigInteger lastId){
        this.lastId = lastId;
    }
    
    public boolean checkPreviousHash(Block prevBlock)
    {
        return Arrays.equals(prevBlock.prevHash, this.prevHash);
    }
}
