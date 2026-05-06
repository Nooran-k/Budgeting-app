package controller;

import data.Database;
import model.Transaction;
import model.TransactionType;

import java.util.List;

public class TransactionController {

    private final BudgetController budgetController;

    public TransactionController(BudgetController budgetController) {
        this.budgetController = budgetController;
    }

    public Transaction addTransaction(String userEmail, double amount,TransactionType type, int categoryId,
     String categoryName, String description,String paymentMethod) {
        if (amount <= 0)  return null;
        if (categoryName == null || categoryName.isEmpty()) return null;

        Transaction t = new Transaction(0, userEmail, amount, type, categoryId, categoryName,
        description, paymentMethod);
        if (!Database.saveTransaction(t)) return null;

        if (type == TransactionType.EXPENSE)
            budgetController.trackBudgetUsage(userEmail, categoryId, amount);

        return t;
    }

    public List<Transaction> getTransactionsByUser(String userEmail) {
        return Database.loadTransactions(userEmail);
    }
}
