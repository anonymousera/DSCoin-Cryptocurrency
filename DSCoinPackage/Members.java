package DSCoinPackage;

import java.util.*;
import HelperClasses.*;

public class Members
 {

  public String UID;
  public List<Pair<String, TransactionBlock>> mycoins;
  public Transaction[] in_process_trans;

  public Members(){

    in_process_trans = new Transaction[100];
    mycoins = new ArrayList<Pair<String, TransactionBlock>>();
    UID = null;

  }

  public void initiateCoinsend(String destUID, DSCoin_Honest DSobj) {
    try{
      Pair<String, TransactionBlock> coin = mycoins.get(0);
      mycoins.remove(0);
      Transaction obj = new Transaction();
      obj.coinID = coin.first;
      obj.Source = this;
      int i;
      for(i = 0 ; i<DSobj.memberlist.length ; ++i){
        if(DSobj.memberlist[i].UID.compareTo(destUID)==0)
          break;
      }
      obj.Destination = DSobj.memberlist[i];
      obj.coinsrc_block = coin.second;
      DSobj.pendingTransactions.AddTransactions(obj);


      for( i = 0;i<in_process_trans.length ; ++i)
        if(in_process_trans[i]==null)
          break;

      in_process_trans[i] = obj; 

    }
    catch(Exception e){
      System.out.println(e);
    }
  }

  public Pair<List<Pair<String, String>>, List<Pair<String, String>>> finalizeCoinsend (Transaction tobj, DSCoin_Honest DSObj) throws MissingTransactionException {
    
    boolean flag = false;
    TransactionBlock iter = DSObj.bChain.lastBlock;
    int i = 0;
    while(iter!=null && !flag){
      for(i = 0;i< iter.trarray.length ; ++i){
        if(iter.trarray[i].compare(tobj)){
          flag = true;
          break;
        }
      }
      if(flag)
        break;
      iter = iter.previous;
    }
    if(!flag){
      throw new MissingTransactionException();
    }
    MerkleTree curr = iter.Tree;
    int l = 0, r = iter.trarray.length;r--;
    int mid;
    TreeNode node = curr.rootnode;

    
    while(true){
      mid = (l+r)/2;
      if(node.left!=null){
        if(mid>=i){
          node = node.left;
          r = mid;
        }
        else{
          node = node.right;
          l = mid+1;
        }
      }
      else
        break;
    }

    List<Pair<String,String>> sibpath = new ArrayList<>(),chain = new ArrayList<>(),reversechain = new ArrayList<>();

    while(node!=null){
      if(node.parent==null){

        sibpath.add(new Pair<String,String>(node.val,null));
        node = node.parent;
      }
      else{

        node = node.parent;
        sibpath.add(new Pair<String,String>(node.left.val,node.right.val));
      }
    }
    
    TransactionBlock biter = DSObj.bChain.lastBlock;
    
    while(biter!=iter){
      chain.add(new Pair<String,String>(biter.dgst,biter.previous.dgst + "#" + biter.trsummary + "#" + biter.nonce));
      biter = biter.previous;
    }
    if(iter.previous!=null){
      chain.add(new Pair<String,String>(iter.dgst,iter.previous.dgst + "#" + iter.trsummary + "#" + iter.nonce));
      chain.add(new Pair<String,String>(iter.previous.dgst,null));
    }
    else{
      chain.add(new Pair<String,String>(iter.dgst,null));
      chain.add(new Pair<String,String>(null,null));
    }

    
    Pair<String,String> temp;
    while(chain.size()>0)
    {
      i = chain.size();
      temp = chain.get(i-1);
      chain.remove(i-1);
      reversechain.add(temp);
      
    }

    
    int j = 0;
    for(i=0;i<tobj.Source.in_process_trans.length;i++){
      if(tobj.Source.in_process_trans[i]==null)
        break;
      if(tobj.Source.in_process_trans[i].compare(tobj))
        continue;
      tobj.Source.in_process_trans[j++] = tobj.Source.in_process_trans[i];
    }
    tobj.Source.in_process_trans[j]=null;

    List<Pair<String, TransactionBlock>> dest_coins = tobj.Destination.mycoins, updatedlist = new ArrayList<>();
    boolean done = false;

    for(i = 0;i<dest_coins.size();){
      if(!done && dest_coins.get(i).first.compareTo(tobj.coinID)>0){
        updatedlist.add(new Pair<String, TransactionBlock>(tobj.coinID,iter));
        done = true;
      }
      else{
        updatedlist.add(dest_coins.get(i++));
      }
    }
    if(!done)
      updatedlist.add(new Pair<String, TransactionBlock>(tobj.coinID,iter));
    tobj.Destination.mycoins = updatedlist;


    return new Pair<List<Pair<String,String>>, List<Pair<String,String>>> (sibpath,reversechain);
  }

  public static boolean checkvalid (Transaction t, TransactionBlock last) {
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
    TransactionBlock iter = last;
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

  public void MineCoin(DSCoin_Honest DSObj) {

    Transaction[] newtrblockcont = new Transaction[DSObj.bChain.tr_count];
    Transaction iter = null;
    int j = 0;
    HashMap<String,Integer> doublecheck = new HashMap<String,Integer>();
    while(j<DSObj.bChain.tr_count-1){
      boolean valid = true,reward = false;
      try {
        iter = DSObj.pendingTransactions.RemoveTransaction(); 
      } catch (Exception e) {
          System.out.println(e);
      }
      
      if(iter.coinsrc_block ==null && iter.Source ==null)
        reward = true;
      else if(iter.coinsrc_block == null && iter.Source.UID.compareTo("Moderator")!=0)
        valid = false;
      if(!reward)
        if(!checkvalid(iter,DSObj.bChain.lastBlock))
          valid = false;
      
      if(doublecheck.get(iter.coinID)!=null)
          valid = false;
      if(valid || reward){
        newtrblockcont[j++] = iter;

        doublecheck.put(iter.coinID,1);
      }
    }
    Transaction minerrewardtransaction = new Transaction();
    DSObj.latestCoinIDint++;
    DSObj.latestCoinID = "" + DSObj.latestCoinIDint;

    minerrewardtransaction.coinID = DSObj.latestCoinID;
    minerrewardtransaction.Source = null;
    minerrewardtransaction.Destination = this;
    minerrewardtransaction.coinsrc_block = null;
    newtrblockcont[j] = minerrewardtransaction;
    TransactionBlock newtrblock = new TransactionBlock(newtrblockcont);
    DSObj.bChain.InsertBlock_Honest(newtrblock);
    mycoins.add(new Pair<String,TransactionBlock>(DSObj.latestCoinID,newtrblock));

  }  

  public void MineCoin(DSCoin_Malicious DSObj) {
    Transaction[] newtrblockcont = new Transaction[DSObj.bChain.tr_count];
    TransactionBlock lastBlock =DSObj.bChain.FindLongestValidChain();
    Transaction iter = null;
    int j = 0;
    HashMap<String,Integer> doublecheck = new HashMap<String,Integer>();

    while(j<DSObj.bChain.tr_count-1){
      boolean valid = true,reward = false;
      try {
        iter = DSObj.pendingTransactions.RemoveTransaction(); 
      } catch (Exception e) {
          System.out.println(e);
      }
      
      if(iter.coinsrc_block ==null && iter.Source ==null)
        reward = true;
      else if(iter.coinsrc_block == null && iter.Source.UID.compareTo("Moderator")!=0)
        valid = false;
      if(!reward)
        if(!checkvalid(iter,lastBlock))
          valid = false;
      
      if(doublecheck.get(iter.coinID)!=null)
          valid = false;
      if(valid || reward){
        newtrblockcont[j++] = iter;
        doublecheck.put(iter.coinID,1);
      }
    }
    Transaction minerrewardtransaction = new Transaction();
    DSObj.latestCoinIDint++;
    DSObj.latestCoinID = "" + DSObj.latestCoinIDint;

    minerrewardtransaction.coinID = DSObj.latestCoinID;
    minerrewardtransaction.Source = null;
    minerrewardtransaction.Destination = this;
    minerrewardtransaction.coinsrc_block = null;
    newtrblockcont[j] = minerrewardtransaction;
    TransactionBlock newtrblock = new TransactionBlock(newtrblockcont);
    DSObj.bChain.InsertBlock_Malicious(newtrblock);
    mycoins.add(new Pair<String,TransactionBlock>(DSObj.latestCoinID,newtrblock));

  }  
}
