package com.se310.ledger.interfaces;

import com.se310.ledger.Block;
import com.se310.ledger.Transaction;
import java.util.Map;

/**
 * Interface for blockchain data repository operations
 * Follows Interface Segregation Principle by focusing only on data access
 */
public interface BlockchainRepository {
    void addBlock(Block block);
    Block getBlock(Integer blockNumber);
    Transaction getTransaction(String transactionId);
    int getBlockCount();
    Map<Integer, Block> getAllBlocks();
    Block getLastBlock();
}
