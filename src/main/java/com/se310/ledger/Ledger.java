package com.se310.ledger;

import com.se310.ledger.interfaces.*;
import com.se310.ledger.services.*;
import java.util.*;

/**
 * Refactored Ledger Class following SOLID principles
 * 
 * SRP: Delegates specific responsibilities to dedicated services
 * OCP: Open for extension through interfaces, closed for modification
 * LSP: Uses interfaces that can be substituted with different implementations
 * ISP: Depends only on interfaces it needs
 * DIP: Depends on abstractions, not concrete implementations
 *
 * @author  Sergey L. Sundukovskiy
 * @version 2.0
 */
public class Ledger {
    private String name;
    private String description;
    private String seed;
    
    // Dependencies injected through constructor (DIP)
    private final BlockchainRepository blockchainRepository;
    private final AccountService accountService;
    private final TransactionValidator transactionValidator;
    private final BlockchainValidator blockchainValidator;
    private final TransactionProcessor transactionProcessor;

    private static Ledger ledger;

    /**
     * Create singleton of the Ledger with dependency injection
     * @param name
     * @param description
     * @param seed
     * @return
     */
    public static synchronized Ledger getInstance(String name, String description, String seed) {
        if (ledger == null) {
            // Initialize dependencies (DIP - Dependency Injection)
            BlockchainRepository repository = new BlockchainRepositoryImpl();
            Block genesisBlock = new Block(1, "");
            genesisBlock.addAccount("master", new Account("master", Integer.MAX_VALUE));
            
            AccountService accountService = new AccountServiceImpl(repository, genesisBlock);
            TransactionValidator validator = new TransactionValidatorImpl();
            BlockchainValidator blockchainValidator = new BlockchainValidatorImpl(repository);
            HashGenerator hashGenerator = new MerkleHashGenerator();
            TransactionProcessor processor = new TransactionProcessor(validator, repository, hashGenerator, seed, genesisBlock);
            
            ledger = new Ledger(name, description, seed, repository, accountService, validator, blockchainValidator, processor);
        }
        return ledger;
    }

    /**
     * Private Ledger Constructor with dependency injection (DIP)
     * @param name
     * @param description
     * @param seed
     * @param blockchainRepository
     * @param accountService
     * @param transactionValidator
     * @param blockchainValidator
     * @param transactionProcessor
     */
    private Ledger(String name, String description, String seed,
                  BlockchainRepository blockchainRepository,
                  AccountService accountService,
                  TransactionValidator transactionValidator,
                  BlockchainValidator blockchainValidator,
                  TransactionProcessor transactionProcessor) {
        this.name = name;
        this.description = description;
        this.seed = seed;
        this.blockchainRepository = blockchainRepository;
        this.accountService = accountService;
        this.transactionValidator = transactionValidator;
        this.blockchainValidator = blockchainValidator;
        this.transactionProcessor = transactionProcessor;
    }

    /**
     * Getter method for the name of the Ledger
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Setter Method for the name of the Ledger
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter Method for Ledger description
     * @return String
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter Method for Description
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Getter Method for the seed
     * @return String
     */
    public String getSeed() {
        return seed;
    }

    /**
     * Setter Method for the seed
     * @param seed
     */
    public void setSeed(String seed) {
        this.seed = seed;
    }

    /**
     * Method for creating accounts in the blockchain (SRP - delegates to AccountService)
     * @param address
     * @return Account representing account in the Blockchain
     */
    public Account createAccount(String address) throws LedgerException {
        return accountService.createAccount(address);
    }

    /**
     * Method implementing core functionality of the Blockchain by handling given transaction (SRP - delegates to TransactionProcessor)
     * @param transaction
     * @return String representing transaction id
     * @throws LedgerException
     */
    public synchronized String processTransaction(Transaction transaction) throws LedgerException {
        return transactionProcessor.processTransaction(transaction);
    }

    /**
     * Get Account balance by address (SRP - delegates to AccountService)
     * @param address
     * @return Integer representing balance of the Account
     * @throws LedgerException
     */
    public int getAccountBalance(String address) throws LedgerException {
        return accountService.getAccountBalance(address);
    }

    /**
     * Get all Account balances that are part of the Blockchain (SRP - delegates to AccountService)
     * @return Map representing Accounts and balances
     */
    public Map<String,Integer> getAccountBalances(){
        return accountService.getAllAccountBalances();
    }

    /**
     * Get Block by id (SRP - delegates to BlockchainRepository)
     * @param blockNumber
     * @return Block or Null
     */
    public Block getBlock (int blockNumber) throws LedgerException {
        Block block = blockchainRepository.getBlock(blockNumber);
        if(block == null){
            throw new LedgerException("Get Block", "Block Does Not Exist");
        }
        return block;
    }

    /**
     * Get Transaction by id (SRP - delegates to BlockchainRepository)
     * @param transactionId
     * @return Transaction or Null
     */
    public Transaction getTransaction (String transactionId){
        return blockchainRepository.getTransaction(transactionId);
    }

    /**
     * Get number of Blocks in the Blockchain (SRP - delegates to BlockchainRepository)
     * @return int representing number of blocks committed to Blockchain
     */
    public int getNumberOfBlocks(){
        return blockchainRepository.getBlockCount();
    }

    /**
     * Method for validating Blockchain (SRP - delegates to BlockchainValidator)
     * Check each block for Hash consistency
     * Check each block for Transaction count
     * Check account balances against the total
     */
    public void validate() throws LedgerException {
        blockchainValidator.validate();
    }

    /**
     * Helper method for CommandProcessor (SRP - delegates to TransactionProcessor)
     * @return current block we are working with
     */
    public Block getUncommittedBlock(){
        return transactionProcessor.getUncommittedBlock();
    }

    /**
     * Helper method allowing reset the state of the Ledger
     */
    public synchronized void reset(){
        // Reset all services
        BlockchainRepository repository = new BlockchainRepositoryImpl();
        Block genesisBlock = new Block(1, "");
        genesisBlock.addAccount("master", new Account("master", Integer.MAX_VALUE));
        
        AccountService accountService = new AccountServiceImpl(repository, genesisBlock);
        TransactionValidator validator = new TransactionValidatorImpl();
        BlockchainValidator blockchainValidator = new BlockchainValidatorImpl(repository);
        HashGenerator hashGenerator = new MerkleHashGenerator();
        TransactionProcessor processor = new TransactionProcessor(validator, repository, hashGenerator, this.seed, genesisBlock);
        
        // Reinitialize ledger with new services
        ledger = new Ledger(this.name, this.description, this.seed, repository, accountService, validator, blockchainValidator, processor);
    }
}
