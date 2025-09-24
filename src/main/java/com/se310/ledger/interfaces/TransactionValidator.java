package com.se310.ledger.interfaces;

import com.se310.ledger.Transaction;

/**
 * Interface for transaction validation
 * Follows Interface Segregation Principle by focusing only on validation logic
 */
public interface TransactionValidator {
    boolean isValid(Transaction transaction);
    String getValidationError(Transaction transaction);
}
