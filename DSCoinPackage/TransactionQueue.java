package DSCoinPackage;

public class TransactionQueue {

  public Transaction firstTransaction;
  public Transaction lastTransaction;
  public int numTransactions;

  public TransactionQueue(){
    
    numTransactions = 0;
    firstTransaction = lastTransaction = null;
  }

  public void AddTransactions (Transaction transaction) {

    if(numTransactions==0){
      firstTransaction = lastTransaction = transaction;

    }
    else{
      lastTransaction.next = transaction;
      transaction.previous = lastTransaction;
      lastTransaction = transaction;

    }
    numTransactions++;
  }
  
  public Transaction RemoveTransaction () throws EmptyQueueException {
    
    if(numTransactions==0){
      throw new EmptyQueueException();

    }
    else{
      Transaction temp = firstTransaction;
      firstTransaction = firstTransaction.next;
      
      numTransactions--;
      if(numTransactions==0)
        lastTransaction = null;
      else
        firstTransaction.previous = null;
      return temp;
    }
  }

  public int size() {
    return numTransactions;
  }
}
