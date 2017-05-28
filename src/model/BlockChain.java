package model;

import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map.Entry;

public class BlockChain {

    LinkedList<Block> blocks;
    int zerosRule;

    public BlockChain() {
        blocks = new LinkedList<Block>();
        this.zerosRule = zerosRule;
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
            ListIterator<BigInteger> trIter = checkTr.getSourceIds().listIterator();
            BigInteger hasMoney = BigInteger.ZERO;
            while(trIter.hasNext()){
                BigInteger trNum = trIter.next();
                //checking existance
                if(newBlock.getTransactions().containsKey(trNum)){
                    //checking sign and amount of money
                    Transaction t = newBlock.getTransactions().get(trNum);
                    if(checkTr.checkSign(t.getPublicKey())){
                        hasMoney = hasMoney.add(t.getPay());
                    }else{
                        //TODO case of referring to the same person's transaction
                    }
                }
                ListIterator blockIter = blocks.listIterator(blocks.size() - 1);
                while(blockIter.hasPrevious()){
                    //TODO same as in newBlock
                }
                //double spending check
                for(Entry<BigInteger, Transaction> e:newBlock.getTransactions().entrySet()){
                    Transaction t = e.getValue();
                    ListIterator<BigInteger> numIter = t.getSourceIds().listIterator();
                    while(numIter.hasNext()){
                        if(numIter.next().equals(trNum)){
                            //2 cases: referring to the same person's transaction and reffering to transaction for this person
                        }
                    }
                }
            }
        }
        return true;
    }
    
    public byte[] toByteArray(){
        //TODO
        return null;
    }
}
