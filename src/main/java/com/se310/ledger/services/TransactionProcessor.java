package com.se310.ledger.services;

import com.se310.ledger.Account;
import com.se310.ledger.Block;
import com.se310.ledger.LedgerException;
import com.se310.ledger.Transaction;
import com.se310.ledger.interfaces.BlockchainRepository;
import com.se310.ledger.interfaces.HashGenerator;
import com.se310.ledger.interfaces.TransactionValidator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service for processing transactions
 * Follows Single Responsibility Principle by handling only transaction processing
 */
public class TransactionProcessor {
    private final TransactionValidator transactionValidator;
    private final BlockchainRepository blockchainRepository;
    private final HashGenerator hashGenerator;
    private final String seed;
    private Block uncommittedBlock;

    public TransactionProcessor(TransactionValidator transactionValidator,
                               BlockchainRepository blockchainRepository,
                               HashGenerator hashGenerator,
                               String seed,
                               Block uncommittedBlock) {
        this.transactionValidator = transactionValidator;
        this.blockchainRepository = blockchainRepository;
        this.hashGenerator = hashGenerator;
        this.seed = seed;
        this.uncommittedBlock = uncommittedBlock;
    }

    public synchronized String processTransaction(Transaction transaction) throws LedgerException {
        // Validate transaction
        String validationError = transactionValidator.getValidationError(transaction);
        if (validationError != null) {
            throw new LedgerException("Process Transaction", validationError);
        }

        // Check for duplicate transaction ID
        if (blockchainRepository.getTransaction(transaction.getTransactionId()) != null) {
            throw new LedgerException("Process Transaction", "Transaction Id Must Be Unique");
        }

        // Process the transaction
        executeTransaction(transaction);
        uncommittedBlock.getTransactionList().add(transaction);

        // Check if block is full and needs to be committed
        if (uncommittedBlock.getTransactionList().size() == 10) {
            commitBlock();
        }

        return transaction.getTransactionId();
    }

    private void executeTransaction(Transaction transaction) {
        Account payerAccount = transaction.getPayer();
        Account receiverAccount = transaction.getReceiver();

        // Deduct balance from payer
        payerAccount.setBalance(payerAccount.getBalance() - transaction.getAmount() - transaction.getFee());
        
        // Increase balance of receiver
        receiverAccount.setBalance(receiverAccount.getBalance() + transaction.getAmount());
    }

    private void commitBlock() throws LedgerException {
        List<String> tempTxList = new ArrayList<>();
        tempTxList.add(seed);

        // Add transaction strings for hashing
        for (Transaction tempTx : uncommittedBlock.getTransactionList()) {
            tempTxList.add(tempTx.toString());
        }

        // Generate hash
        String hash = hashGenerator.generateHash(tempTxList);
        uncommittedBlock.setHash(hash);

        // Commit block
        blockchainRepository.addBlock(uncommittedBlock);

        // Create next block
        Block committedBlock = blockchainRepository.getLastBlock();
        Map<String, Account> accountMap = committedBlock.getAccountBalanceMap();
        List<Account> accountList = new ArrayList<>(accountMap.values());

        uncommittedBlock = new Block(uncommittedBlock.getBlockNumber() + 1, committedBlock.getHash());

        // Replicate accounts
        for (Account account : accountList) {
            Account tempAccount = account.clone();
            uncommittedBlock.addAccount(tempAccount.getAddress(), tempAccount);
        }

        // Link to previous block
        uncommittedBlock.setPreviousBlock(committedBlock);
    }

    public Block getUncommittedBlock() {
        return uncommittedBlock;
    }

    public void setUncommittedBlock(Block uncommittedBlock) {
        this.uncommittedBlock = uncommittedBlock;
    }
}
