package DSCoinPackage;

import HelperClasses.CRF;

public class BlockChain_Honest {

  public int tr_count;
  public static final String start_string = "DSCoin";
  public TransactionBlock lastBlock;

  static String computedgst(String dgst){
    long a = 1000000001;
    CRF obj = new CRF(64);

    while(true){
      if((obj.Fn(dgst + a)).substring(0,4).compareTo("0000")==0)
        break;
      a++;
    }
    return (dgst + a);
  }

  public BlockChain_Honest(){
    
    lastBlock = null;
    tr_count = 0;
  }

  public void InsertBlock_Honest (TransactionBlock newBlock) {

    if(lastBlock==null){
      lastBlock = newBlock;
      newBlock.dgst = (start_string + "#" + newBlock.trsummary + "#");

    }
    else{
      newBlock.previous = lastBlock;
      lastBlock = newBlock;
      newBlock.dgst = (newBlock.previous.dgst + "#" + newBlock.trsummary + "#");
    }

    newBlock.dgst = computedgst(newBlock.dgst);
    int n = newBlock.dgst.length();
    newBlock.nonce = newBlock.dgst.substring(n - 10 ,n);
    CRF obj = new CRF(64);
    newBlock.dgst = obj.Fn(newBlock.dgst);
  }
}
