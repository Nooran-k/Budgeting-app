package controller;

import data.DataStore;
import model.FinancialGoal;
import model.GoalStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


public class GoalController {

    private List<FinancialGoal>    goals;
    private NotificationController notifController;

    public GoalController(NotificationController notifController) {
        this.notifController = notifController;
        this.goals = DataStore.loadGoals();
    }

    
    public FinancialGoal createGoal(int userId, String name,
                                    double targetAmount, double initialAmount,
                                    LocalDate deadline) {
        
        if (name == null || name.trim().isEmpty()) return null;
        if (targetAmount <= 0)                     return null;
        if (deadline.isBefore(LocalDate.now()))    return null; 

        int id = DataStore.nextGoalId();
        FinancialGoal goal = new FinancialGoal(id, userId, name,
                                               targetAmount, initialAmount,
                                               deadline);
        goals.add(goal);
        DataStore.saveGoals(goals);
        return goal;
    }


    public void updateGoalProgress(int goalId, int userId, double amount) {
        for (FinancialGoal g : goals) {
            if (g.getGoalId() == goalId) {
                g.addContribution(amount);
                DataStore.saveGoals(goals);

                if (g.getStatus() == GoalStatus.COMPLETED) {
                    notifController.pushGoalCompleteAlert(userId, g.getName());
                }
                return;
            }
        }
    }

   
    public boolean deleteGoal(int goalId) {
        boolean removed = goals.removeIf(g -> g.getGoalId() == goalId);
        if (removed) DataStore.saveGoals(goals);
        return removed;
    }

  
    public List<FinancialGoal> getAllGoals(int userId) {
        return goals.stream()
                .filter(g -> g.getUserId() == userId)
                .collect(Collectors.toList());
    }

   
    public double calculateProgressPercentage(int goalId) {
        return goals.stream()
                .filter(g -> g.getGoalId() == goalId)
                .findFirst()
                .map(FinancialGoal::calculateProgressPercentage)
                .orElse(0.0);
    }
}
