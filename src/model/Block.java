package model;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

public class Block implements Serializable {

    private byte[] prevHash;
    public byte[] nonce = new byte[30];
    private HashMap<BigInteger, Transaction> transactions;
    private BigInteger lastId;

    public Block(byte[] prevHash) {
        transactions = new HashMap<BigInteger, Transaction>();
        lastId = BigInteger.ZERO;
        this.prevHash = prevHash;

    }

    public Block(byte[] prevHash, BigInteger lastId) {
        transactions = new HashMap<BigInteger, Transaction>();
        this.lastId = lastId;
        this.prevHash = prevHash;
    }

    public void addTransaction(Transaction transaction) {
        lastId = lastId.add(BigInteger.ONE);
        transactions.put(lastId, transaction);
    }

    public byte[] hash() throws Exception {
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
                for (int j = 0; j < (i == zerosRule / 8 ? zerosRule % 8 : 8); j++) {
                    if ((hash[hash.length - i - 1] & (1 << j)) == 0) {
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

    public byte[] getprevHash() {
        return prevHash;
    }

    public HashMap<BigInteger, Transaction> getTransactions() {
        return transactions;
    }

    public void setLastId(BigInteger lastId) {
        this.lastId = lastId;
    }

    public BigInteger getLastId() {
        return lastId;
    }

    public boolean checkPreviousHash(Block prevBlock) throws Exception {
        return Arrays.equals(prevBlock.hash(), this.prevHash);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Block) {
            Block m = (Block) o;
            if (transactions.equals(m.transactions) && lastId.equals(m.lastId)) {
                if (prevHash.length != m.prevHash.length) {
                    return false;
                }
                if (nonce.length != m.nonce.length) {
                    return false;
                }
                for (int i = 0; i < prevHash.length; i++) {
                    if (prevHash[i] != m.prevHash[i]) {
                        return false;
                    }
                }
                for (int i = 0; i < nonce.length; i++) {
                    if (nonce[i] != m.nonce[i]) {
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public void addMinerTransaction(BigInteger pay, PublicKey receiverPK, PrivateKey receiverSK) throws Exception {
        Transaction t = new Transaction(new LinkedList(), BigInteger.ZERO, pay, receiverPK, receiverPK, receiverSK);
        addTransaction(t);
    }

    public byte[] toByteArray() throws IOException {
        return Serializer.serialize(this);
    }
}
