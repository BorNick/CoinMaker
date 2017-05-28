package model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.*;
import java.util.LinkedList;
//import java.security.spec.X509EncodedKeySpec;

public class Transaction {

    private LinkedList<BigInteger> entryIds;
    private BigInteger left;
    private BigInteger pay;
    private PublicKey publicKey;
    private byte[] sign;

    public Transaction(LinkedList<BigInteger> sourceIds, BigInteger left, BigInteger pay, PublicKey publicKey, PrivateKey privateKey) throws Exception {
        this.entryIds = sourceIds;
        this.left = left;
        this.pay = pay;
        this.publicKey = publicKey;
        this.sign = sign(privateKey);
    }

    public byte[] sign(PrivateKey privateKey) throws Exception {
        //X509EncodedKeySpec privKeySpec = new X509EncodedKeySpec(SK);
        //KeyFactory keyFactory = KeyFactory.getInstance("DSA", "SUN");
        //PrivateKey privateKey = keyFactory.generatePrivate(privKeySpec);
        Signature sig = Signature.getInstance("SHA1withDSA", "SUN");
        sig.initSign(privateKey);
        byte[] data = toByteArray();
        sig.update(data, 0, data.length);
        return sig.sign();

    }

    public boolean checkSign(PublicKey pubKey) throws GeneralSecurityException, IOException {
        //X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(senderPK);
        //KeyFactory keyFactory = KeyFactory.getInstance("DSA", "SUN");
        //PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);
        Signature sig = Signature.getInstance("SHA1withDSA", "SUN");
        sig.initVerify(pubKey);
        byte[] data = toByteArray();
        sig.update(data, 0, data.length);
        return sig.verify(sign);
    }

    public LinkedList<BigInteger> getSourceIds() {
        return entryIds;
    }

    public BigInteger getLeft() {
        return left;
    }

    public BigInteger getPay() {
        return pay;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public byte[] getSign() {
        return sign;
    }
    
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(entryIds);
        byte[] arrEIds = byteOut.toByteArray();
        out.reset();
        byte[] arrLeft = left.toByteArray();
        byte[] arrPay = pay.toByteArray();
        out.writeObject(publicKey);
        byte[] arrPK = byteOut.toByteArray();
        byte[] data = new byte[arrEIds.length + arrLeft.length + arrPay.length + arrPK.length];
        System.arraycopy(arrEIds, 0, data, 0, arrEIds.length);
        System.arraycopy(arrLeft, 0, data, arrEIds.length, arrLeft.length);
        System.arraycopy(arrPay, 0, data, arrEIds.length + arrLeft.length, arrPay.length);
        System.arraycopy(publicKey, 0, data, arrEIds.length + arrLeft.length + arrPay.length, arrPK.length);
        return data;
    }
}
