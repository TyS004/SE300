package com.se310.ledger.interfaces;

import java.util.List;

/**
 * Interface for hash generation
 * Follows Interface Segregation Principle by focusing only on hash generation
 */
public interface HashGenerator {
    String generateHash(List<String> data);
}
