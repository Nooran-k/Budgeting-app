package controller;

import data.DataStore;
import model.Budget;
import model.BudgetStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class BudgetController {

    private List<Budget>              budgets;
    private NotificationController    notifController;

    public BudgetController(NotificationController notifController) {
        this.notifController = notifController;
        this.budgets = DataStore.loadBudgets();
    }

    
    public Budget createBudget(int userId, int categoryId, String categoryName,
                               double limitAmount, LocalDate startDate,
                               LocalDate endDate, int alertThreshold) {
        
        boolean duplicate = budgets.stream().anyMatch(b ->
                b.getUserId()    == userId
             && b.getCategoryId() == categoryId
             && !b.getStartDate().isAfter(endDate)     
             && !b.getEndDate().isBefore(startDate));

        if (duplicate) return null; 

       
        int id = DataStore.nextBudgetId();
        Budget budget = new Budget(id, userId, categoryId, categoryName,
                                   limitAmount, startDate, endDate, alertThreshold);
        budgets.add(budget);
        DataStore.saveBudgets(budgets);
        return budget;
    }

   
    public boolean editBudget(int budgetId, double newLimit, int newThreshold) {
        for (Budget b : budgets) {
            if (b.getBudgetId() == budgetId) {
                b.setLimitAmount(newLimit);
                b.setAlertThreshold(newThreshold);
                DataStore.saveBudgets(budgets);
                return true;
            }
        }
        return false;
    }

   
    public boolean deleteBudget(int budgetId) {
        boolean removed = budgets.removeIf(b -> b.getBudgetId() == budgetId);
        if (removed) DataStore.saveBudgets(budgets);
        return removed;
    }

   
    public void trackBudgetUsage(int userId, int categoryId, double amount) {
       
        LocalDate today = LocalDate.now();
        Optional<Budget> match = budgets.stream().filter(b ->
                b.getUserId()    == userId
             && b.getCategoryId() == categoryId
             && !today.isBefore(b.getStartDate())
             && !today.isAfter(b.getEndDate()))
            .findFirst();

        if (match.isEmpty()) return; 

        Budget b = match.get();
        BudgetStatus before = b.getStatus();

        b.addExpense(amount);
        DataStore.saveBudgets(budgets); 

        if (b.checkIfAlertNeeded()) {
            boolean exceeded = b.getStatus() == BudgetStatus.EXCEEDED;
           
            if (b.getStatus() != before || before == BudgetStatus.ON_TRACK) {
                notifController.pushBudgetAlert(userId, b.getCategoryName(),
                     b.calculateUsagePercentage(), exceeded);
            }
        }
    }

   
    public List<Budget> getBudgetForMonth(int userId, int month, int year) {
        return budgets.stream().filter(b -> {
            if (b.getUserId() != userId) return false;
            LocalDate start = b.getStartDate();
            return start.getMonthValue() == month && start.getYear() == year;
        }).collect(Collectors.toList());
    }


    public List<Budget> getAllBudgets(int userId) {
        return budgets.stream()
                .filter(b -> b.getUserId() == userId)
                .collect(Collectors.toList());
    }
}
