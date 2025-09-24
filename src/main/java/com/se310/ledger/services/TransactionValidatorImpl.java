package com.se310.ledger.services;

import com.se310.ledger.Transaction;
import com.se310.ledger.interfaces.TransactionValidator;

/**
 * Concrete implementation of TransactionValidator
 * Follows Single Responsibility Principle by handling only transaction validation
 */
public class TransactionValidatorImpl implements TransactionValidator {

    @Override
    public boolean isValid(Transaction transaction) {
        return getValidationError(transaction) == null;
    }

    @Override
    public String getValidationError(Transaction transaction) {
        if (transaction.getAmount() < 0 || transaction.getAmount() > Integer.MAX_VALUE) {
            return "Transaction Amount Is Out of Range";
        }
        
        if (transaction.getFee() < 10) {
            return "Transaction Fee Must Be Greater Than 10";
        }
        
        if (transaction.getNote().length() > 1024) {
            return "Note Length Must Be Less Than 1024 Chars";
        }
        
        if (transaction.getPayer().getBalance() < (transaction.getAmount() + transaction.getFee())) {
            return "Payer Does Not Have Required Funds";
        }
        
        return null; // No validation errors
    }
}
