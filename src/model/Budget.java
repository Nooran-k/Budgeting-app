package model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Represents a budget assigned to a specific user and category.
 * 
 * This class is responsible for tracking spending against a predefined limit
 * within a specific time period. It also monitors budget usage and updates
 * its status based on defined thresholds.
 */
public class Budget implements Serializable {

    private static final long serialVersionUID = 1L;

    private int          budgetId;
    private String       userEmail;       
    private int          categoryId;
    private String       categoryName;
    private double       limitAmount;
    private double       spentAmount;
    private LocalDate    startDate;
    private LocalDate    endDate;
    private int          alertThreshold;
    private BudgetStatus status;

    /**
     * Constructs a new Budget object with the specified details.
     *
     * @param budgetId unique identifier for the budget
     * @param userEmail email of the user associated with the budget
     * @param categoryId identifier of the category
     * @param categoryName name of the category
     * @param limitAmount maximum allowed spending
     * @param startDate start date of the budget period
     * @param endDate end date of the budget period
     * @param alertThreshold percentage threshold to trigger alerts
     */
    public Budget(int budgetId, String userEmail, int categoryId,
                  String categoryName, double limitAmount,
                  LocalDate startDate, LocalDate endDate,
                  int alertThreshold) {

        this.budgetId       = budgetId;
        this.userEmail      = userEmail;
        this.categoryId     = categoryId;
        this.categoryName   = categoryName;
        this.limitAmount    = limitAmount;
        this.spentAmount    = 0.0;
        this.startDate      = startDate;
        this.endDate        = endDate;
        this.alertThreshold = alertThreshold;
        this.status         = BudgetStatus.ON_TRACK;
    }

    /**
     * Adds an expense to the current budget and updates its status.
     *
     * @param amount the expense amount to be added
     */
    public void addExpense(double amount) {
        this.spentAmount += amount;
        updateStatus();
    }

    /**
     * Updates the budget status based on current usage percentage.
     * 
     * Status can be:
     * ON_TRACK, NEAR_LIMIT, or EXCEEDED.
     */
    public void updateStatus() {
        double pct = calculateUsagePercentage();
        if      (pct >= 100)            status = BudgetStatus.EXCEEDED;
        else if (pct >= alertThreshold) status = BudgetStatus.NEAR_LIMIT;
        else                            status = BudgetStatus.ON_TRACK;
    }

    /**
     * Calculates the remaining budget amount.
     *
     * @return remaining amount (limit - spent)
     */
    public double calculateRemaining() {
        return limitAmount - spentAmount;
    }

    /**
     * Calculates how much of the budget has been used as a percentage.
     *
     * @return usage percentage (0–100+)
     */
    public double calculateUsagePercentage() {
        return limitAmount == 0 ? 0 : (spentAmount / limitAmount) * 100.0;
    }

    /**
     * Checks whether an alert should be triggered.
     *
     * @return true if budget is near limit or exceeded, false otherwise
     */
    public boolean checkIfAlertNeeded() {
        return status == BudgetStatus.NEAR_LIMIT
            || status == BudgetStatus.EXCEEDED;
    }


    // Getters

    public int getBudgetId() { return budgetId; }

    public String getUserEmail() { return userEmail; }

    public int getCategoryId() { return categoryId; }

    public String getCategoryName() { return categoryName; }

    public double getLimitAmount() { return limitAmount; }

    public double getSpentAmount() { return spentAmount; }

    public LocalDate getStartDate() { return startDate; }

    public LocalDate getEndDate() { return endDate; }

    public int getAlertThreshold() { return alertThreshold; }

    public BudgetStatus getStatus() { return status; }

    // Setters

    /**
     * Sets the budget ID.
     *
     * @param id new budget ID
     */
    public void setBudgetId(int id) {
        this.budgetId = id;
    }

    /**
     * Sets the spent amount and updates status.
     *
     * @param a new spent amount
     */
    public void setSpentAmount(double a) {
        this.spentAmount = a;
        updateStatus();
    }

    /**
     * Sets the budget status manually.
     *
     * @param s new status
     */
    public void setStatus(BudgetStatus s) {
        this.status = s;
    }

    /**
     * Sets the budget limit and updates status.
     *
     * @param a new limit amount
     */
    public void setLimitAmount(double a) {
        this.limitAmount = a;
        updateStatus();
    }

    /**
     * Sets the alert threshold and updates status.
     *
     * @param t new alert threshold percentage
     */
    public void setAlertThreshold(int t) {
        this.alertThreshold = t;
        updateStatus();
    }
}
