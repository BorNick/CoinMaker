package model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map.Entry;

public class BlockChain {

    LinkedList<Block> blocks;
    int zerosRule;

    public BlockChain(int zerosRule) {
        blocks = new LinkedList<Block>();
        this.zerosRule = zerosRule;
    }
    
    public BlockChain(byte[] data) throws Exception {
        BlockChain buf = (BlockChain)Serializer.deserialize(data);
        this.zerosRule = buf.zerosRule;
        this.blocks = buf.blocks;
        
    }

    public void addBlock(Block newBlock) {
        blocks.add(newBlock);
    }

    public boolean checkBlock(Block newBlock) throws NoSuchAlgorithmException, IOException, GeneralSecurityException {
        //checking number of nulls
        int numOfZeros = 0;
        byte[] hash = newBlock.hash();
        for (int i = 0; i <= zerosRule / 8; i++) {
            for (int j = 0; j < (i == zerosRule / 8 ? zerosRule % 8 : 8); j++) {
                if ((hash[hash.length - i] & (1 << j)) == 0) {
                    numOfZeros++;
                }
            }
        }
        if(numOfZeros < zerosRule)
            return false;
        //checking source transactions correctness
        for(Entry<BigInteger, Transaction> entry:newBlock.getTransactions().entrySet()){
            Transaction checkTr = entry.getValue();
            //checking sign
            if(!checkTr.checkSign()){
                return false;
            }
            ListIterator<BigInteger> sIdsIter = checkTr.getSourceIds().listIterator();
            BigInteger hasMoney = BigInteger.ZERO;
            while(sIdsIter.hasNext()){
                BigInteger trNum = sIdsIter.next();
                //checking existance
                boolean found = false;
                if(newBlock.getTransactions().containsKey(trNum)){
                    found  = true;
                    //checking amount of money
                    Transaction t = newBlock.getTransactions().get(trNum);
                    if(checkTr.getSenderPK().equals(t.getReceiverPK())){
                        hasMoney = hasMoney.add(t.getPay());
                    }else if(checkTr.getSenderPK().equals(t.getSenderPK())){
                        hasMoney = hasMoney.add(t.getLeft());
                    }
                } else {
                    ListIterator<Block> blockIter = blocks.listIterator(blocks.size() - 1);
                    while (blockIter.hasPrevious()) {
                        //same as in newBlock
                        Block curBlock = blockIter.previous();
                        if (curBlock.getTransactions().containsKey(trNum)) {
                            found = true;
                            Transaction t = curBlock.getTransactions().get(trNum);
                            if (checkTr.getSenderPK().equals(t.getReceiverPK())) {
                                hasMoney = hasMoney.add(t.getPay());
                            } else if (checkTr.getSenderPK().equals(t.getSenderPK())) {
                                hasMoney = hasMoney.add(t.getLeft());
                            }
                        }
                    }
                }
                if(!found){
                    return false;
                }
                //double spending check
                for(Entry<BigInteger, Transaction> e:newBlock.getTransactions().entrySet()){
                    Transaction t = e.getValue();
                    ListIterator<BigInteger> numIter = t.getSourceIds().listIterator();
                    while(numIter.hasNext()){
                        if(numIter.next().equals(trNum)){
                            if(checkTr.getSenderPK().equals(t.getSenderPK())){
                                return false;
                            }
                        }
                    }
                }
            }
            if(hasMoney.compareTo(checkTr.getLeft().add(checkTr.getPay())) < 0){
                return false;
            }
        }
        return true;
    }
    
    public BigInteger getLastId(){
        Block lastBlock = blocks.getLast();
        BigInteger max = BigInteger.ZERO;
        for(BigInteger id:lastBlock.getTransactions().keySet()){
            if(id.compareTo(max) > 0){
                max = id;
            }
        }
        return max;
    }
    
    public byte[] toByteArray() throws IOException{
        return Serializer.serialize(this);
    }
}
