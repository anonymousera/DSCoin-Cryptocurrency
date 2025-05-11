package DSCoinPackage;

import HelperClasses.*;

public class BlockChain_Malicious {

  public int tr_count;
  public static final String start_string = "DSCoin";
  public TransactionBlock[] lastBlocksList;

  public BlockChain_Malicious(){
    lastBlocksList = new TransactionBlock[100];
    tr_count = 0;
  }

  public static boolean checkTransactionBlock (TransactionBlock tB) {
    
    MerkleTree temp = new MerkleTree();
    if(tB.dgst.substring(0,4).compareTo("0000")!=0)
      return false;
    CRF obj = new CRF(64);
    if(tB.previous == null){
      if(tB.dgst.compareTo(obj.Fn(start_string + "#" + tB.trsummary +"#" + tB.nonce))!=0)
        return false;
    }
    else
      if(tB.dgst.compareTo(obj.Fn(tB.previous.dgst + "#" + tB.trsummary +"#" + tB.nonce))!=0){
        return false;
      }
    
    if(tB.trsummary.compareTo(temp.Build(tB.trarray)) != 0)
      return false;
    
    for(int i = 0 ; i < tB.trarray.length ; ++i)
      if(!tB.checkTransaction(tB.trarray[i]))
        return false;

    return true;
  }

  public TransactionBlock FindLongestValidChain () {
    TransactionBlock last = null,iter,tempmax;
    int maxlen = 0,j;
    for(int i = 0 ; i<lastBlocksList.length; ++i){
      tempmax = iter = lastBlocksList[i];
      if(iter==null)
        break;
      j = 0;
      while(iter!=null){
        if(checkTransactionBlock(iter)){
          j++;
        }
        else{
          j = 0;
        }
        if(j == 1)
          tempmax = iter;
        iter = iter.previous;
      }
      if(j>maxlen){
        maxlen = j;
        last = tempmax;
      }

    }
    return last;
  }

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

  public void InsertBlock_Malicious (TransactionBlock newBlock) {

    TransactionBlock lastBlock = FindLongestValidChain();

    newBlock.previous = lastBlock;
    if(lastBlock==null){

      newBlock.dgst = (start_string + "#" + newBlock.trsummary + "#");
      newBlock.dgst = computedgst(newBlock.dgst);
      int n = newBlock.dgst.length();
      newBlock.nonce = newBlock.dgst.substring(n - 10 ,n);
      CRF obj = new CRF(64);
      newBlock.dgst = obj.Fn(newBlock.dgst);
      lastBlocksList[0] = newBlock;
      return;
    }

    newBlock.dgst = (newBlock.previous.dgst + "#" + newBlock.trsummary + "#");
    newBlock.dgst = computedgst(newBlock.dgst);
    int n = newBlock.dgst.length();
    newBlock.nonce = newBlock.dgst.substring(n - 10 ,n);
    CRF obj = new CRF(64);
    newBlock.dgst = obj.Fn(newBlock.dgst);
    int i;
    for(i = 0 ; i < lastBlocksList.length;++i){
      if(lastBlocksList[i]==null)
        break;
      if(lastBlocksList[i]==lastBlock){
        lastBlocksList[i] = newBlock;
        break;
      }
    }
    if(lastBlocksList[i] == null){
      lastBlocksList[i] = newBlock;
    }
  }
}
