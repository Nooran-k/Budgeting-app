/**
 * Controller responsible for managing budgets.
 * 
 * Handles:
 * - Creating budgets
 * - Tracking expenses
 * - Retrieving monthly budgets
 */
package controller;

import data.Database;
import model.Budget;
import model.BudgetStatus;

import java.time.LocalDate;
import java.util.List;


public class BudgetController {

    private final NotificationController nc;

    /**
     * Constructor.
     *
     * @param nc notification controller
     */
    public BudgetController(NotificationController nc) {
        this.nc = nc;
    }

    /**
     * Creates a new budget.
     *
     * @param userEmail      user email
     * @param categoryId     category ID
     * @param categoryName   category name
     * @param limitAmount    budget limit
     * @param startDate      start date
     * @param endDate        end date
     * @param alertThreshold alert percentage threshold
     * @return Budget object or null if duplicate/failed
     */
    public Budget createBudget(String userEmail, int categoryId,
                               String categoryName, double limitAmount,
                               LocalDate startDate, LocalDate endDate,
                               int alertThreshold) {

        if (Database.budgetExists(userEmail, categoryId, startDate, endDate))
            return null;

        Budget b = new Budget(0, userEmail, categoryId, categoryName,
                              limitAmount, startDate, endDate, alertThreshold);

        if (!Database.saveBudget(b)) return null;

        return b;
    }

    /**
     * Tracks budget usage after adding an expense.
     *
     * @param userEmail user email
     * @param categoryId category ID
     * @param amount expense amount
     */
    public void trackBudgetUsage(String userEmail, int categoryId,
                                 double amount) {

        LocalDate today = LocalDate.now();

        Budget b = Database.findActiveBudget(userEmail, categoryId, today);
        if (b == null) return;

        BudgetStatus before = b.getStatus();

        b.addExpense(amount);
        Database.updateBudget(b);

        if (b.checkIfAlertNeeded() && b.getStatus() != before) {
            nc.pushBudgetAlert(userEmail, b.getCategoryName(),
                               b.calculateUsagePercentage(),
                               b.getStatus() == BudgetStatus.EXCEEDED);
        }
    }

    /**
     * Returns budgets for a specific month.
     *
     * @param userEmail user email
     * @param month     month number
     * @param year      year
     * @return list of budgets
     */
    public List<Budget> getBudgetForMonth(String userEmail,
                                          int month, int year) {
        return Database.loadBudgets(userEmail, month, year);
    }
}
