package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Represents a financial goal set by a user.
 * 
 * A financial goal defines a target amount that the user aims to reach
 * by a specific deadline. The class tracks progress and determines
 * the goal status based on contributions and remaining time.
 */
public class FinancialGoal implements Serializable {

    private static final long serialVersionUID = 1L;

    private int        goalId;
    private String     userEmail;
    private String     name;
    private double     targetAmount;
    private double     currentAmount;
    private LocalDate  deadline;
    private GoalStatus status;

    /**
     * Constructs a new FinancialGoal.
     *
     * @param goalId unique identifier for the goal
     * @param userEmail email of the user who owns the goal
     * @param name name of the goal
     * @param targetAmount amount the user wants to achieve
     * @param currentAmount current saved amount
     * @param deadline target completion date
     */
    public FinancialGoal(int goalId, String userEmail, String name,
                         double targetAmount, double currentAmount,
                         LocalDate deadline) {

        this.goalId        = goalId;
        this.userEmail     = userEmail;
        this.name          = name;
        this.targetAmount  = targetAmount;
        this.currentAmount = currentAmount;
        this.deadline      = deadline;
        this.status        = GoalStatus.IN_PROGRESS;
        updateStatus();
    }

    /**
     * Adds a contribution toward the goal.
     * Ensures the current amount does not exceed the target amount.
     *
     * @param amount amount to add
     */
    public void addContribution(double amount) {
        this.currentAmount += amount;
        if (this.currentAmount > this.targetAmount)
            this.currentAmount = this.targetAmount;
        updateStatus();
    }

    /**
     * Calculates the progress toward the goal as a percentage.
     *
     * @return progress percentage (0–100)
     */
    public double calculateProgressPercentage() {
        return targetAmount == 0 ? 0 : (currentAmount / targetAmount) * 100.0;
    }

    /**
     * Calculates how much needs to be saved monthly to reach the goal.
     *
     * @return required monthly savings
     */
    public double calculateMonthlySavingsNeeded() {
        double remaining = targetAmount - currentAmount;
        long months = ChronoUnit.MONTHS.between(LocalDate.now(), deadline);

        if (months <= 0) return remaining;

        return remaining / months;
    }

    /**
     * Updates the status of the goal based on progress and time remaining.
     */
    public void updateStatus() {

        if (currentAmount >= targetAmount) {
            status = GoalStatus.COMPLETED;
            return;
        }

        long days = ChronoUnit.DAYS.between(LocalDate.now(), deadline);

        status = (days <= 30 && calculateProgressPercentage() < 50)
                 ? GoalStatus.BEHIND_SCHEDULE
                 : GoalStatus.IN_PROGRESS;
    }


    // Getters

    public int getGoalId() { return goalId; }

    public String getUserEmail() { return userEmail; }

    public String getName() { return name; }

    public double getTargetAmount() { return targetAmount; }

    public double getCurrentAmount() { return currentAmount; }

    public LocalDate getDeadline() { return deadline; }

    public GoalStatus getStatus() { return status; }

    // Setters

    /**
     * Sets the goal ID.
     * @param id new goal ID
     */
    public void setGoalId(int id) { this.goalId = id; }

    /**
     * Sets the goal name.
     * @param name new name
     */
    public void setName(String name) { this.name = name; }

    /**
     * Sets target amount and updates status.
     * @param a new target amount
     */
    public void setTargetAmount(double a) {
        this.targetAmount = a;
        updateStatus();
    }

    /**
     * Sets current amount and updates status.
     * @param a new current amount
     */
    public void setCurrentAmount(double a) {
        this.currentAmount = a;
        updateStatus();
    }

    /**
     * Sets deadline and updates status.
     * @param d new deadline
     */
    public void setDeadline(LocalDate d) {
        this.deadline = d;
        updateStatus();
    }

    /**
     * Sets goal status manually.
     * @param s new status
     */
    public void setStatus(GoalStatus s) {
        this.status = s;
    }
}
