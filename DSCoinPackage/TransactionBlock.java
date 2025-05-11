package DSCoinPackage;

import HelperClasses.MerkleTree;
import HelperClasses.CRF;

public class TransactionBlock {

  public Transaction[] trarray;
  public TransactionBlock previous;
  public MerkleTree Tree;
  public String trsummary;
  public String nonce;
  public String dgst;


  public TransactionBlock(Transaction[] t) {
    trarray = new Transaction[t.length];
    for(int i=0;i<t.length;++i){

      trarray[i] = new Transaction();
      trarray[i].coinID = t[i].coinID;
      trarray[i].Source = t[i].Source;
      trarray[i].Destination = t[i].Destination;
      trarray[i].coinsrc_block = t[i].coinsrc_block;
      trarray[i].next = t[i].next;
    }

    previous = null;
    Tree = new MerkleTree();
    trsummary = Tree.Build(trarray);

    dgst = null;
    
  }

  public boolean checkTransaction (Transaction t) {
    TransactionBlock src  = t.coinsrc_block;

    boolean ret = false;
    if(src != null)
      for(int i = 0;i<src.trarray.length;++i){
        if(src.trarray[i].coinID.compareTo(t.coinID) ==0 && src.trarray[i].Destination.UID.compareTo(t.Source.UID) == 0){
          ret = true;
          break;
        }
      }
    else{
      ret = true;
    }
    if(!ret)
      return ret;
    TransactionBlock iter = this.previous;
    while((iter!=src)&&ret){
      for(int i = 0;i<iter.trarray.length;++i){
        if(iter.trarray[i].coinID.compareTo(t.coinID)==0){
          ret = false;
          break;
        }
      }
      iter = iter.previous;
    }
    return ret;
  }
}
