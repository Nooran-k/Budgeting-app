
package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

﻿
package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


public class FinancialGoal implements Serializable {

    private static final long serialVersionUID = 1L;

    private int    goalId;
    private int    userId;
    private String name;           
    private double targetAmount; 
    private double currentAmount;  
    private LocalDate deadline;    
    private GoalStatus status;

    
    public FinancialGoal(int goalId, int userId, String name,
                         double targetAmount, double currentAmount,
                         LocalDate deadline) {
        this.goalId        = goalId;
        this.userId        = userId;
        this.name          = name;
        this.targetAmount  = targetAmount;
        this.currentAmount = currentAmount;
        this.deadline      = deadline;
        this.status        = GoalStatus.IN_PROGRESS;
        updateStatus(); 
    }

   
    public void addContribution(double amount) {
        this.currentAmount += amount;
        
        if (this.currentAmount > this.targetAmount) {
            this.currentAmount = this.targetAmount;
        }
        updateStatus();
    }

   
    public double calculateProgressPercentage() {
        if (targetAmount == 0) return 0;
        return (currentAmount / targetAmount) * 100.0;
    }

    public double calculateMonthlySavingsNeeded() {
        double remaining = targetAmount - currentAmount;
        long months = ChronoUnit.MONTHS.between(LocalDate.now(), deadline);
        if (months <= 0) return remaining; 
        return remaining / months;
    }

    
    public void updateStatus() {
        if (currentAmount >= targetAmount) {
            this.status = GoalStatus.COMPLETED;
            return;
        }
       
        long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), deadline);
        if (daysLeft <= 30 && calculateProgressPercentage() < 50.0) {
            this.status = GoalStatus.BEHIND_SCHEDULE;
        } else {
            this.status = GoalStatus.IN_PROGRESS;
        }
    }

   
    public int       getGoalId()         { return goalId; }
    public int       getUserId()         { return userId; }
    public String    getName()           { return name; }
    public double    getTargetAmount()   { return targetAmount; }
    public double    getCurrentAmount()  { return currentAmount; }
    public LocalDate getDeadline()       { return deadline; }
    public GoalStatus getStatus()        { return status; }

    public void setName(String name)               { this.name = name; }
    public void setTargetAmount(double amount)     { this.targetAmount = amount; updateStatus(); }
    public void setCurrentAmount(double amount)    { this.currentAmount = amount; updateStatus(); }
    public void setDeadline(LocalDate date)        { this.deadline = date; updateStatus(); }

    @Override
    public String toString() {
        return String.format("%s: %.2f / %.2f EGP (%.1f%%) – %s",
                name, currentAmount, targetAmount,
                calculateProgressPercentage(), status);
    }
}
