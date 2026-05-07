/**
 * Controller responsible for managing financial goals.
 */
package controller;

import data.Database;
import model.FinancialGoal;
import model.GoalStatus;

import java.time.LocalDate;
import java.util.List;
public class GoalController {

    private final NotificationController nc;

    /**
import model.FinancialGoal;
import model.GoalStatus;
     * Constructor.
     *
     * @param nc notification controller
     */
    public GoalController(NotificationController nc) {
        this.nc = nc;
    }

    /**
     * Creates a new financial goal.
     *
     * @param userEmail user email
     * @param name goal name
     * @param targetAmount target amount
     * @param initialAmount initial saved amount
     * @param deadline goal deadline
     * @return FinancialGoal object or null if invalid
     */
    public FinancialGoal createGoal(String userEmail, String name,
                                    double targetAmount, double initialAmount,
                                    LocalDate deadline) {

        if (name == null || name.isEmpty()) return null;
        if (targetAmount <= 0) return null;
        if (deadline.isBefore(LocalDate.now())) return null;

        FinancialGoal g = new FinancialGoal(0, userEmail, name,
                                            targetAmount, initialAmount,
                                            deadline);

        if (!Database.saveGoal(g)) return null;

        return g;
    }

    /**
     * Updates goal progress by adding contribution.
     *
     * @param goalId goal ID
     * @param userEmail user email
     * @param amount amount added
     */
    public void updateGoalProgress(int goalId, String userEmail, double amount) {

        List<FinancialGoal> goals = Database.loadGoals(userEmail);

        for (FinancialGoal g : goals) {
            if (g.getGoalId() == goalId) {

                g.addContribution(amount);
                Database.updateGoal(g);

                if (g.getStatus() == GoalStatus.COMPLETED) {
                    nc.pushGoalCompleteAlert(userEmail, g.getName());
                }

                return;
            }
        }
    }

    /**
     * Deletes a goal.
     *
     * @param goalId goal ID
     * @return true if deleted
     */
    public boolean deleteGoal(int goalId) {
        return Database.deleteGoal(goalId);
    }

    /**
     * Returns all goals for a user.
     *
     * @param userEmail user email
     * @return list of goals
     */
    public List<FinancialGoal> getAllGoals(String userEmail) {
        return Database.loadGoals(userEmail);
    }
}
