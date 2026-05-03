package controller;
public class GoalController {
    private List<FinancialGoal> goals;
    private GoalView view;

    public GoalController(GoalView view) {
        this.view = view;
        this.goals = new ArrayList<>();
        loadGoals();
    }

    public void createGoal(String name, double target, 
                            LocalDate deadline, double initial) {
        if (name == null || name.trim().isEmpty()) {
            view.showError("Goal name cannot be empty!");
            return;
        }
        if (target <= 0) {
            view.showError("Target must be greater than 0!");
            return;
        }
        if (deadline.isBefore(LocalDate.now())) {
            view.showError("Deadline must be a future date!");
            return;
        }

        FinancialGoal goal = new FinancialGoal(name, target, initial, deadline);
        goals.add(goal);
        saveGoals();

        double monthly = goal.calculateMonthlySavingsNeeded();
        view.showSuccessMessage(
            "Goal created! 💪 Save $" + 
            String.format("%.2f", monthly) + "/month to reach it.");
        
        view.displayGoals(goals); 
    }

    public void addContribution(String goalId, double amount) {
        FinancialGoal goal = findById(goalId);
        if (goal == null) return;

        goal.addContribution(amount);
        saveGoals();

        if (goal.getStatus() == GoalStatus.COMPLETED) {
            view.showGoalCompleted(goal.getGoalName());
        }

        view.displayGoals(goals); 
    }

    public void loadGoals() {
    }

    private void saveGoals() {
    }

    private FinancialGoal findById(String goalId) {
        for (FinancialGoal g : goals)
            if (g.getGoalId().equals(goalId)) return g;
        return null;
    }
}