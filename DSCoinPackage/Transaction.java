package DSCoinPackage;

public class Transaction {

  public String coinID;
  public Members Source;
  public Members Destination;
  public TransactionBlock coinsrc_block;
  public Transaction previous, next;

  public boolean compare(Transaction b){
    Transaction a = this;
    if(a.coinID.compareTo(b.coinID)!=0)
      return false;
    if(a.Source!=b.Source)
      return false;
    if(a.Destination!=b.Destination)
      return false;
    if(a.coinsrc_block!=b.coinsrc_block)
      return false;

    return true;
  }
}
