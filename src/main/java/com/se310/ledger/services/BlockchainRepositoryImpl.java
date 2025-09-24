package com.se310.ledger.services;

import com.se310.ledger.Block;
import com.se310.ledger.Transaction;
import com.se310.ledger.interfaces.BlockchainRepository;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Concrete implementation of BlockchainRepository
 * Follows Single Responsibility Principle by handling only blockchain data operations
 */
public class BlockchainRepositoryImpl implements BlockchainRepository {
    private final NavigableMap<Integer, Block> blockMap;

    public BlockchainRepositoryImpl() {
        this.blockMap = new TreeMap<>();
    }

    @Override
    public void addBlock(Block block) {
        blockMap.put(block.getBlockNumber(), block);
    }

    @Override
    public Block getBlock(Integer blockNumber) {
        return blockMap.get(blockNumber);
    }

    @Override
    public Transaction getTransaction(String transactionId) {
        // Search in committed blocks
        for (Map.Entry<Integer, Block> entry : blockMap.entrySet()) {
            Block block = entry.getValue();
            for (Transaction transaction : block.getTransactionList()) {
                if (transaction.getTransactionId().equals(transactionId)) {
                    return transaction;
                }
            }
        }
        return null;
    }

    @Override
    public int getBlockCount() {
        return blockMap.size();
    }

    @Override
    public Map<Integer, Block> getAllBlocks() {
        return blockMap;
    }

    public Block getLastBlock() {
        return blockMap.isEmpty() ? null : blockMap.lastEntry().getValue();
    }
}
