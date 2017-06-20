package model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.*;
import java.util.LinkedList;
//import java.security.spec.X509EncodedKeySpec;

public class Transaction implements Serializable{

    private LinkedList<BigInteger> entryIds;
    private BigInteger left;
    private BigInteger pay;
    private PublicKey senderPK;
    private PublicKey receiverPK;
    private byte[] sign;

    public Transaction(LinkedList<BigInteger> sourceIds, BigInteger left, BigInteger pay, PublicKey senderPK, PublicKey receiverPK, PrivateKey privateKey) throws Exception {
        this.entryIds = sourceIds;
        this.left = left;
        this.pay = pay;
        this.senderPK = senderPK;
        this.receiverPK = receiverPK;
        this.sign = sign(privateKey);
    }
    
    public Transaction(byte[] data) throws Exception{
        Transaction buf = (Transaction)Serializer.deserialize(data);
        this.entryIds = buf.entryIds;
        this.left = buf.left;
        this.pay = buf.pay;
        this.senderPK = buf.senderPK;
        this.receiverPK = buf.receiverPK;
        this.sign = buf.sign;
    }

    public byte[] sign(PrivateKey privateKey) throws Exception {
        //X509EncodedKeySpec privKeySpec = new X509EncodedKeySpec(SK);
        //KeyFactory keyFactory = KeyFactory.getInstance("DSA", "SUN");
        //PrivateKey privateKey = keyFactory.generatePrivate(privKeySpec);
        Signature sig = Signature.getInstance("SHA1withDSA", "SUN");
        sig.initSign(privateKey);
        byte[] data = toBAForSign();
        sig.update(data, 0, data.length);
        return sig.sign();

    }

    public boolean checkSign() throws GeneralSecurityException, IOException {
        Signature sig = Signature.getInstance("SHA1withDSA", "SUN");
        sig.initVerify(senderPK);
        byte[] data = toBAForSign();
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
    
    public PublicKey getSenderPK() {
        return senderPK;
    }

    public PublicKey getReceiverPK() {
        return receiverPK;
    }

    public byte[] getSign() {
        return sign;
    }
    
    public byte[] toBAForSign() throws IOException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(entryIds);
        byte[] arrEIds = byteOut.toByteArray();
        out.reset();
        byte[] arrLeft = left.toByteArray();
        byte[] arrPay = pay.toByteArray();
        out.writeObject(senderPK);
        byte[] arrSPK = byteOut.toByteArray();
        out.reset();
        out.writeObject(receiverPK);
        byte[] arrRPK = byteOut.toByteArray();
        byte[] data = new byte[arrEIds.length + arrLeft.length + arrPay.length + arrSPK.length + arrRPK.length];
        System.arraycopy(arrEIds, 0, data, 0, arrEIds.length);
        System.arraycopy(arrLeft, 0, data, arrEIds.length, arrLeft.length);
        System.arraycopy(arrPay, 0, data, arrEIds.length + arrLeft.length, arrPay.length);
        System.arraycopy(arrSPK, 0, data, arrEIds.length + arrLeft.length + arrPay.length, arrSPK.length);
        System.arraycopy(arrRPK, 0, data, arrEIds.length + arrLeft.length + arrPay.length + arrSPK.length, arrRPK.length);
        return data;
    }
    
    public byte[] toByteArray() throws IOException{
        return Serializer.serialize(this);
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Transaction) {
            Transaction m = (Transaction) o;
            if (entryIds.equals(m.entryIds) && left.equals(m.left) && pay.equals(m.pay) && senderPK.equals(m.senderPK) && receiverPK.equals(m.receiverPK)) {
                if (sign.length != m.sign.length) {
                    return false;
                }
                for (int i = 0; i < sign.length; i++) {
                    if (sign[i] != m.sign[i]) {
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
}
