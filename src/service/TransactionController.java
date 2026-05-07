/**
 * Controller responsible for managing transactions.
 */
package controller;

import data.Database;
import model.Transaction;
import model.TransactionType;

import java.util.List;
public class TransactionController {

    private final BudgetController budgetController;

    /**
     * Constructor.
     *
     * @param budgetController budget controller
     */
    public TransactionController(BudgetController budgetController) {
        this.budgetController = budgetController;
    }

    /**
     * Adds a new transaction.
     *
     * @param userEmail user email
     * @param amount amount
     * @param type transaction type
     * @param categoryId category ID
     * @param categoryName category name
     * @param description description
     * @param paymentMethod payment method
     * @return Transaction object or null if invalid
     */
    public Transaction addTransaction(String userEmail, double amount,
                                      TransactionType type, int categoryId,
                                      String categoryName, String description,
                                      String paymentMethod) {

        if (amount <= 0) return null;
        if (categoryName == null || categoryName.isEmpty()) return null;

        Transaction t = new Transaction(0, userEmail, amount, type,
                categoryId, categoryName, description, paymentMethod);

        if (!Database.saveTransaction(t)) return null;

        if (type == TransactionType.EXPENSE) {
            budgetController.trackBudgetUsage(userEmail, categoryId, amount);
        }

        return t;
    }

    /**
     * Returns all transactions for a user.
     *
     * @param userEmail user email
     * @return list of transactions
     */
    public List<Transaction> getTransactionsByUser(String userEmail) {
        return Database.loadTransactions(userEmail);
    }
}
