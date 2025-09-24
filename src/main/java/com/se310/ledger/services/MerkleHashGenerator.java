package com.se310.ledger.services;

import com.se310.ledger.MerkleTrees;
import com.se310.ledger.interfaces.HashGenerator;
import java.util.List;

/**
 * Concrete implementation of HashGenerator using MerkleTrees
 * Follows Single Responsibility Principle by handling only hash generation
 */
public class MerkleHashGenerator implements HashGenerator {

    @Override
    public String generateHash(List<String> data) {
        MerkleTrees merkleTrees = new MerkleTrees(data);
        merkleTrees.merkle_tree();
        return merkleTrees.getRoot();
    }
}
