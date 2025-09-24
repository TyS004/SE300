package com.se310.ledger.interfaces;

/**
 * Interface for blockchain validation
 * Follows Interface Segregation Principle by focusing only on blockchain validation
 */
public interface BlockchainValidator {
    void validate() throws Exception;
}
