package DSCoinPackage;

import HelperClasses.*;

public class Moderator
 {

  public void initializeDSCoin(DSCoin_Honest DSObj, int coinCount) {

    int i=0,j=0,k=DSObj.bChain.tr_count;

    Transaction[] Transactionlist = new Transaction[k];
    Members mod = new Members();
    mod.UID = "Moderator";
    int coin = 100000;

    while(j<coinCount){
        if(i==DSObj.memberlist.length)
            i = 0;

        Transaction t = new Transaction();
        
        t.coinID = ""+coin;
        coin++;
        
        t.Source = mod;
        t.Destination = DSObj.memberlist[i];
        t.coinsrc_block = null;
        Transactionlist[j%k] = t;
        
        j++;
        i++;

        if(j%k==0){
          TransactionBlock tB  = new TransactionBlock(Transactionlist);
          for(k = 0 ; k< DSObj.bChain.tr_count; ++k ){

            Transactionlist[k].Destination.mycoins.add(new Pair<String,TransactionBlock>(Transactionlist[k].coinID,tB));

          }
          DSObj.bChain.InsertBlock_Honest(tB);
          Transactionlist = new Transaction[k];
        }

    }

    coin--;
    DSObj.latestCoinIDint = coin;
    DSObj.latestCoinID = "" + DSObj.latestCoinIDint;

  }
    
  public void initializeDSCoin(DSCoin_Malicious DSObj, int coinCount) {

    int i=0,j=0,k=DSObj.bChain.tr_count;

    Transaction[] Transactionlist = new Transaction[k]; 
    Members mod = new Members();
    mod.UID = "Moderator";
    int coin = 100000;
    while(j<coinCount){
        if(i==DSObj.memberlist.length)
            i = 0;
        Transaction t = new Transaction();
        
        t.coinID = ""+coin;
        coin++;
        t.Source = mod;
        t.Destination = DSObj.memberlist[i];
        t.coinsrc_block = null;
        Transactionlist[j%k] = t;
        
        j++;
        i++;

        if(j%k==0){
          TransactionBlock tB  = new TransactionBlock(Transactionlist);
          for(k = 0 ; k< DSObj.bChain.tr_count; ++k ){
            Transactionlist[k].Destination.mycoins.add(new Pair<String,TransactionBlock>(Transactionlist[k].coinID,tB));

          }
          DSObj.bChain.InsertBlock_Malicious(tB);
          Transactionlist = new Transaction[k];

        }
    }
    coin--;
    DSObj.latestCoinIDint = coin;
    DSObj.latestCoinID = "" + DSObj.latestCoinIDint;
 
  }
}
