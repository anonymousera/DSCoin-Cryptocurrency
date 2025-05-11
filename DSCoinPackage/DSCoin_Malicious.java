package DSCoinPackage;

public class DSCoin_Malicious {

  public TransactionQueue pendingTransactions;
  public BlockChain_Malicious bChain;
  public Members[] memberlist;
  public String latestCoinID;
  public int latestCoinIDint;

  public DSCoin_Malicious(){
    memberlist = new Members[0];
    latestCoinIDint =  100000;
    latestCoinID = "" + latestCoinID;
  }
}
