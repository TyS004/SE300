package com.se310.ledger.services;

import com.se310.ledger.Block;
import com.se310.ledger.LedgerException;
import com.se310.ledger.interfaces.BlockchainValidator;
import com.se310.ledger.interfaces.BlockchainRepository;
import java.util.Map;

/**
 * Concrete implementation of BlockchainValidator
 * Follows Single Responsibility Principle by handling only blockchain validation
 */
public class BlockchainValidatorImpl implements BlockchainValidator {
    private final BlockchainRepository blockchainRepository;

    public BlockchainValidatorImpl(BlockchainRepository blockchainRepository) {
        this.blockchainRepository = blockchainRepository;
    }

    @Override
    public void validate() throws LedgerException {
        Map<Integer, Block> blockMap = blockchainRepository.getAllBlocks();
        
        if (blockMap.isEmpty()) {
            throw new LedgerException("Validate", "No Block Has Been Committed");
        }

        Block lastBlock = blockchainRepository.getLastBlock();
        Map<String, com.se310.ledger.Account> accountMap = lastBlock.getAccountBalanceMap();
        
        int totalBalance = accountMap.values().stream()
                .mapToInt(com.se310.ledger.Account::getBalance)
                .sum();

        int fees = 0;
        
        for (Block block : blockMap.values()) {
            // Check for Hash Consistency
            if (block.getBlockNumber() != 1) {
                if (!block.getPreviousHash().equals(block.getPreviousBlock().getHash())) {
                    throw new LedgerException("Validate", "Hash Is Inconsistent: " + block.getBlockNumber());
                }
            }

            // Check for Transaction Count
            if (block.getTransactionList().size() != 10) {
                throw new LedgerException("Validate", "Transaction Count Is Not 10 In Block: " + block.getBlockNumber());
            }

            for (com.se310.ledger.Transaction transaction : block.getTransactionList()) {
                fees += transaction.getFee();
            }
        }

        int adjustedBalance = totalBalance + fees;

        // Check for account balances against the total
        if (adjustedBalance != Integer.MAX_VALUE) {
            throw new LedgerException("Validate", "Balance Does Not Add Up");
        }
    }
}
