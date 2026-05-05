package controller;

import data.DataStore;
import model.Transaction;
import model.TransactionType;

import java.util.List;
import java.util.stream.Collectors;


public class TransactionController {

    private List<Transaction> transactions;
    private BudgetController  budgetController; // needed for US#5 integration

    public TransactionController(BudgetController budgetController) {
        this.budgetController = budgetController;
        // Load persisted data at startup
        this.transactions = DataStore.loadTransactions();
    }

    
    public Transaction addTransaction(int userId, double amount,
                                      TransactionType type, int categoryId,
                                      String categoryName, String description,
                                      String paymentMethod) {
       
        if (amount <= 0) {
            
            return null;
        }
        if (categoryName == null || categoryName.trim().isEmpty()) {
            return null;
        }

       
        int id = DataStore.nextTransactionId();
        Transaction t = new Transaction(id, userId, amount, type,
                                        categoryId, categoryName,
                                        description, paymentMethod);
        transactions.add(t);
        DataStore.saveTransactions(transactions); // persist to file

        
        if (type == TransactionType.EXPENSE) {
            budgetController.trackBudgetUsage(userId, categoryId, amount);
        }

        return t;
    }

    
    public boolean updateTransaction(int transactionId, double newAmount,
                                     String newCategory, String newDescription) {
        for (Transaction t : transactions) {
            if (t.getTransactionId() == transactionId) {
                t.setAmount(newAmount);
                t.setCategoryName(newCategory);
                t.setDescription(newDescription);
                DataStore.saveTransactions(transactions);
                return true;
            }
        }
        return false;
    }

    
    public boolean deleteTransaction(int transactionId) {
        boolean removed = transactions.removeIf(
            t -> t.getTransactionId() == transactionId);
        if (removed) DataStore.saveTransactions(transactions);
        return removed;
    }

    
    public List<Transaction> getTransactionsByUser(int userId) {
        return transactions.stream()
                .filter(t -> t.getUserId() == userId)
                .collect(Collectors.toList());
    }

   
    public List<Transaction> filterByCategory(int userId, int categoryId) {
        return transactions.stream()
                .filter(t -> t.getUserId() == userId
                          && t.getCategoryId() == categoryId)
                .collect(Collectors.toList());
    }
}