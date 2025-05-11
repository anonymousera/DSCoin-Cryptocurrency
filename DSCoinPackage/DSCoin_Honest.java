package DSCoinPackage;

public class DSCoin_Honest {

  public TransactionQueue pendingTransactions;
  public BlockChain_Honest bChain;
  public Members[] memberlist;
  public String latestCoinID;
  public int latestCoinIDint;

  public DSCoin_Honest(){
    memberlist = new Members[0];
    latestCoinIDint =  100000;
    latestCoinID = "" + latestCoinID;
  }
}
