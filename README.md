# Cryptocurrency Building

## Our Cryptocurrencey : DSCoin
Every coin is a six digit unique number.

Every **[transaction](DSCoinPackage/Transaction.java)** has the following information:
 The coin being transferred
 The source (that is, the person spending this coin)
 The destination (that is, the person receiving this coin)
 Some information to indicate when the source received this coin from someone (this will be described in more detail later).

For simplicity, we assume every transaction consists of exactly one coin.

A **[transaction-block](DSCoinPackage/TransactionBlock.java)** consists of a set of transactions. Let tr-count denote the number of transactions per block. 1 The transaction-block will also have additional attributes, which will be discussed below.

A *blockchain* is an authenticated linked list of transaction-blocks.

*Pending transactions* and **[transaction-queue](DSCoinPackage/TransactionQueue.java)**: All the transactions in the transaction-block are processed transactions. Additionally, we have a transaction-queue which contains pending transactions. Every new transaction is first added to the transaction-queue, and later moved to a transaction-block (and thus added to the blockchain).

## [DSCoin_Honest](DSCoinPackage/DSCoin_Honest.java): A cryptocoin for honest users

### Intial Setup
Initially, all participants receive a certain number of coins from the **[moderator](DSCoinPackage/Moderator.java)**. All these are added in (possibly separate) **transaction-blocks** to the blockchain and there are no pending transactions. After the initial setup, transactions can be processed on demand. 

### Initializing a Coin-Send
The coin-sender creates a **[transaction](DSCoinPackage/Transaction.java)** object consisting of the *coin ID* he/she wishes to spend, the sender's identification, and the reciever's identification and a pointer *coin_src* that points to the **transaction-block** in the blockchain where the sender recieved this coin. The sender adds this transaction to the pending transactions queue and also maintains his/her own list of pending transactions in **in_process_trans**. Once there are sufficiently many transactions in the transaction queue, a *miner* removes them from this queue and *mines* a transaction block and adds it to the **[blockchain](DSCoinPackage/BlockChain_Honest.java)**. At this point, the sender verifies the presence of initiated transaction in the blockchain and then sends a **proof of membership** to the reciever before the transaction is finalized by adding the coin ID to the sender's collection.

### Mining
A transaction block in the blockchain contains *tr-count* number of transactions stored in an array and on a **[Merkle Tree](HelperClasses/MerkleTree.java)** built on it. The job of the miner is to create a **transaction-block** consisting of **tr-count** *valid* transactions (that is,none of the transaction should be a *double spending*).
The miner collects **tr-count-1** number of valid transactions from the Transaction Queue. The miner also recieves a *reward* for mining this block, which is also processed as a valid transaction in the same transaction-block, hence making it up to **tr-count** transactions. After completing the transaction-block, the miner simply adds this to the blockchain.

### Finalizing a Coin-Send
After a transaction has been included in a transaction-block in the blockchain, the respective sender can send a proof of membership/transaction to the reciever, which can be verified by later. The sender also removes this tranaction from his/her **in_process_trans** queue and the respective coin is added to the reciever's collection.

## [DSCoin_Malicious](DSCoinPackage/DSCoin_Malicious.java): Handling Malicious Miners
The solution described in the previous section works in the setting where all miners are honest. However, one of the prime advantages of a cryptocurrency is the decentralized aspect, and in this setting, we cannot assume that the miners are honest.  Indeed, it is possible that a buyer (source) adds an invalid transaction to the **pendingTransactions** queue, and then the same buyer mines a block that includes the invalid transaction.
The validity of a transaction-block can be checked by verifying the **Merkle Tree** and other relavent attributes stored in it. In order to handle malicious miners, who add invalid blocks to the blockchain, the structure of the block chain is changed from a simple list into a tree-like structure, where the honest miners add valid transaction blocks at the end of the longest chain of valid blocks. The first few blocks are always valid in our setup, which stores the transactions the processed the initial distribution of coins for the members.
