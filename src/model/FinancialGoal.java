package model;

public class FinancialGoal {
    private String goalId;
    private String goalName;
    private double targetAmount;   
    private double currentAmount;  
    private LocalDate deadline;

    public FinancialGoal(String goalName, double targetAmount, 
                          double initialAmount, LocalDate deadline) {
        this.goalId = UUID.randomUUID().toString();
        this.goalName = goalName;
        this.targetAmount = targetAmount;
        this.currentAmount = initialAmount;
        this.deadline = deadline;
    }

    public double getProgressPercent() {
        return (currentAmount / targetAmount) * 100;
    }

    public double calculateMonthlySavingsNeeded() {
        long monthsLeft = ChronoUnit.MONTHS.between(LocalDate.now(), deadline);
        if (monthsLeft <= 0) return targetAmount - currentAmount;
        return (targetAmount - currentAmount) / monthsLeft;
    }

    public void addContribution(double amount) {
        this.currentAmount += amount;
    }

    public GoalStatus getStatus() {
        if (currentAmount >= targetAmount) return GoalStatus.COMPLETED;
        return GoalStatus.IN_PROGRESS;
    }

    
    public String getGoalId() { return goalId; }
    public String getGoalName() { return goalName; }
    public double getTargetAmount() { return targetAmount; }
    public double getCurrentAmount() { return currentAmount; }
    public LocalDate getDeadline() { return deadline; }
}