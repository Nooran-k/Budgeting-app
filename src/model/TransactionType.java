package model;

/**
 * Represents the type of a financial transaction.
 * 
 * Determines whether the transaction increases or decreases
 * the user's balance.
 */
public enum TransactionType {

    /** Money received (e.g., salary, gifts) */
    INCOME,

    /** Money spent (e.g., bills, food, transport) */
    EXPENSE
}
