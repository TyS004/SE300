package com.se310.ledger.services;

import com.se310.ledger.Account;
import com.se310.ledger.Block;
import com.se310.ledger.LedgerException;
import com.se310.ledger.interfaces.AccountService;
import com.se310.ledger.interfaces.BlockchainRepository;
import java.util.HashMap;
import java.util.Map;

/**
 * Concrete implementation of AccountService
 * Follows Single Responsibility Principle by handling only account-related operations
 */
public class AccountServiceImpl implements AccountService {
    private final BlockchainRepository blockchainRepository;
    private Block uncommittedBlock;

    public AccountServiceImpl(BlockchainRepository blockchainRepository, Block uncommittedBlock) {
        this.blockchainRepository = blockchainRepository;
        this.uncommittedBlock = uncommittedBlock;
    }

    @Override
    public Account createAccount(String address) throws LedgerException {
        if (uncommittedBlock.getAccount(address) != null) {
            throw new LedgerException("Create Account", "Account Already Exists");
        }

        Account account = new Account(address, 0);
        uncommittedBlock.addAccount(address, account);
        return account;
    }

    @Override
    public Account getAccount(String address) {
        return uncommittedBlock.getAccount(address);
    }

    @Override
    public Integer getAccountBalance(String address) throws LedgerException {
        Block lastBlock = blockchainRepository.getLastBlock();
        if (lastBlock == null) {
            throw new LedgerException("Get Account Balance", "Account Is Not Committed to a Block");
        }

        Account account = lastBlock.getAccount(address);
        if (account == null) {
            throw new LedgerException("Get Account Balance", "Account Does Not Exist");
        }
        return account.getBalance();
    }

    @Override
    public Map<String, Integer> getAllAccountBalances() {
        Block lastBlock = blockchainRepository.getLastBlock();
        if (lastBlock == null) {
            return null;
        }

        Map<String, Account> accountMap = lastBlock.getAccountBalanceMap();
        Map<String, Integer> balances = new HashMap<>();

        for (Account account : accountMap.values()) {
            balances.put(account.getAddress(), account.getBalance());
        }

        return balances;
    }

    @Override
    public boolean accountExists(String address) {
        return uncommittedBlock.getAccount(address) != null;
    }

    public void setUncommittedBlock(Block uncommittedBlock) {
        this.uncommittedBlock = uncommittedBlock;
    }
}
