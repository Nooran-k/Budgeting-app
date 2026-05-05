
package model;

import java.io.Serializable;
import java.time.LocalDate;

public class Budget implements Serializable {

    private static final long serialVersionUID = 1L;

    private int budgetId;
    private int userId;
    private int categoryId;
    private String categoryName; 
    private double limitAmount;   
    private double spentAmount;  
    private LocalDate startDate;  
    private LocalDate endDate;    
    private int alertThreshold;   
    private BudgetStatus status; 

    public Budget(int budgetId, int userId, int categoryId,
                  String categoryName, double limitAmount,
                  LocalDate startDate, LocalDate endDate,
                  int alertThreshold) {
        this.budgetId       = budgetId;
        this.userId         = userId;
        this.categoryId     = categoryId;
        this.categoryName   = categoryName;
        this.limitAmount    = limitAmount;
        this.spentAmount    = 0.0;        
        this.startDate      = startDate;
        this.endDate        = endDate;
        this.alertThreshold = alertThreshold;
        this.status         = BudgetStatus.ON_TRACK; 
    }


 
    public void addExpense(double amount) {
        this.spentAmount += amount;
        updateStatus();
    }

 
    public void updateStatus() {
        double percentage = calculateUsagePercentage();
        if (percentage >= 100.0) {
            this.status = BudgetStatus.EXCEEDED;
        } else if (percentage >= alertThreshold) {
            this.status = BudgetStatus.NEAR_LIMIT;
        } else {
            this.status = BudgetStatus.ON_TRACK;
        }
    }

    public double calculateRemaining() {
        return limitAmount - spentAmount;
    }

    public double calculateUsagePercentage() {
        if (limitAmount == 0) return 0;
        return (spentAmount / limitAmount) * 100.0;
    }

    
    public boolean checkIfAlertNeeded() {
        return status == BudgetStatus.NEAR_LIMIT
            || status == BudgetStatus.EXCEEDED;
    }

    public int    getBudgetId()       { return budgetId; }
    public int    getUserId()         { return userId; }
    public int    getCategoryId()     { return categoryId; }
    public String getCategoryName()   { return categoryName; }
    public double getLimitAmount()    { return limitAmount; }
    public double getSpentAmount()    { return spentAmount; }
    public LocalDate getStartDate()   { return startDate; }
    public LocalDate getEndDate()     { return endDate; }
    public int    getAlertThreshold() { return alertThreshold; }
    public BudgetStatus getStatus()   { return status; }

    public void setLimitAmount(double amount)       { this.limitAmount = amount; updateStatus(); }
    public void setAlertThreshold(int threshold)    { this.alertThreshold = threshold; updateStatus(); }
    public void setSpentAmount(double amount)       { this.spentAmount = amount; updateStatus(); }
    public void setStatus(BudgetStatus status)      { this.status = status; }

    @Override
    public String toString() {
        return String.format("%s: %.2f / %.2f EGP (%.1f%%) [%s]",
                categoryName, spentAmount, limitAmount,
                calculateUsagePercentage(), status);
    }
}
