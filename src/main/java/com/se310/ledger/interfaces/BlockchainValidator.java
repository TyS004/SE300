package com.se310.ledger.interfaces;

import com.se310.ledger.LedgerException;

/**
 * Interface for blockchain validation
 * Follows Interface Segregation Principle by focusing only on blockchain validation
 */
public interface BlockchainValidator {
    void validate() throws LedgerException;
}
