package model;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map.Entry;

public class BlockChain implements Serializable{

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

    public boolean checkBlock(Block newBlock, BigInteger maxReward) throws Exception{
        //checking number of nulls
        int numOfZeros = 0;
        byte[] hash = newBlock.hash();
        for (int i = 0; i <= zerosRule / 8; i++) {
            for (int j = 0; j < (i == zerosRule / 8 ? zerosRule % 8 : 8); j++) {
                if ((hash[hash.length - i - 1] & (1 << j)) == 0) {
                    numOfZeros++;
                }
            }
        }
        if(numOfZeros < zerosRule)
            return false;
        //checking source transactions correctness
        for(Entry<BigInteger, Transaction> entry:newBlock.getTransactions().entrySet()){
            //for miner transaction
            if(entry.getKey().equals(newBlock.getLastId())){
                if(entry.getValue().getPay().compareTo(maxReward) > 0 || !entry.getValue().getLeft().equals(BigInteger.ZERO)){
                    return false;
                }
                continue;
            }
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
                    ListIterator<Block> blockIter = blocks.listIterator(blocks.size());
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
                for (Entry<BigInteger, Transaction> e : newBlock.getTransactions().entrySet()) {
                    Transaction t = e.getValue();
                    ListIterator<BigInteger> numIter = t.getSourceIds().listIterator();
                    if (!e.getKey().equals(entry.getKey())) {
                        while (numIter.hasNext()) {
                            if (numIter.next().equals(trNum)) {
                                if (checkTr.getSenderPK().equals(t.getSenderPK())) {
                                    return false;
                                }
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
    
    public BigInteger getBalance(PublicKey pk, LinkedList<BigInteger> activeIds){
        HashMap<BigInteger, Transaction> getTr = new HashMap<BigInteger, Transaction>();
        HashMap<BigInteger, Transaction> giveTr = new HashMap<BigInteger, Transaction>();
        ListIterator<Block> li = blocks.listIterator();
        while(li.hasNext()){
            for(Entry<BigInteger, Transaction> e:li.next().getTransactions().entrySet()){
                if(e.getValue().getSenderPK().equals(pk)){
                    if(!e.getValue().getLeft().equals(BigInteger.ZERO)){
                        giveTr.put(e.getKey(), e.getValue());
                    }
                    ListIterator<BigInteger> sourceIdsIter = e.getValue().getSourceIds().listIterator();
                    while(sourceIdsIter.hasNext()){
                        BigInteger id = sourceIdsIter.next();
                        if(getTr.containsKey(id)){
                            getTr.remove(id);
                        }
                        if(giveTr.containsKey(id)){
                            giveTr.remove(id);
                        }
                    }
                }
                if(e.getValue().getReceiverPK().equals(pk)){
                    getTr.put(e.getKey(), e.getValue());
                    ListIterator<BigInteger> sourceIdsIter = e.getValue().getSourceIds().listIterator();
                    while(sourceIdsIter.hasNext()){
                        BigInteger id = sourceIdsIter.next();
                        if(getTr.containsKey(id)){
                            getTr.remove(id);
                        }
                        if(giveTr.containsKey(id)){
                            giveTr.remove(id);
                        }
                    }
                }
            }
        }
        BigInteger sum = BigInteger.ZERO;
        for(Entry<BigInteger, Transaction> e: giveTr.entrySet()){
            sum = sum.add(e.getValue().getLeft());
            activeIds.add(e.getKey());
        }
        for(Entry<BigInteger, Transaction> e: getTr.entrySet()){
            sum = sum.add(e.getValue().getPay());
            activeIds.add(e.getKey());
        }
        return sum;
    }
    
    public LinkedList<Block> getBlocks(){
        return blocks;
    }
}
