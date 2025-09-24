package com.se310.ledger;

/**
 * Account class implementation representing account in the Blockchain
 * Follows LSP by implementing proper cloning behavior
 *
 * @author  Sergey L. Sundukovskiy
 * @version 2.0
 */
public class Account implements Cloneable {

    private String address;
    private Integer balance;

    /**
     * Account Constructor
     * @param address
     * @param balance
     */
    public Account(String address, Integer balance) {
        this.address = address;
        this.balance = balance;
    }

    /**
     * Getter Method for account address
     * @return
     */
    public String getAddress() {
        return address;
    }

    /**
     * Setter Method for account address
     * @param address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Getter method for account balance
     * @return
     */
    public int getBalance() {
        return balance;
    }

    /**
     * Setter method for account balance
     * @param balance
     */
    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    /**
     * Method for creating an account copy (LSP - proper implementation of Cloneable)
     * @return Account clone
     */
    @Override
    public Account clone() {
        try {
            return (Account) super.clone();
        } catch (CloneNotSupportedException e) {
            // Fallback to manual cloning
            return new Account(this.getAddress(), this.balance);
        }
    }
}
