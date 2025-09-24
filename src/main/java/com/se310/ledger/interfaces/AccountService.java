package com.se310.ledger.interfaces;

import com.se310.ledger.Account;
import com.se310.ledger.LedgerException;
import java.util.Map;

/**
 * Interface for account-related operations
 * Follows Interface Segregation Principle by focusing only on account management
 */
public interface AccountService {
    Account createAccount(String address) throws LedgerException;
    Account getAccount(String address);
    Integer getAccountBalance(String address) throws LedgerException;
    Map<String, Integer> getAllAccountBalances();
    boolean accountExists(String address);
}
