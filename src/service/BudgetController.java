package controller;

import model.Budget;
import model.BudgetStatus;
import view.BudgetView;
import java.util.ArrayList;
import java.util.List;

public class BudgetController {

    private List<Budget> budgets;
    private BudgetView view;


    public BudgetController(BudgetView view) {
        this.view = view;
        this.budgets = new ArrayList<>();
    }

    public void createBudget(String category, double amount, int threshold) {

        for (Budget b : budgets) {
            if (b.getCategory().equals(category)) {
                view.showError("Budget for " + category + " already exists!");
                return;
            }
        }

        if (amount <= 0) {
            view.showError("Amount must be greater than 0!");
            return;
        }

        Budget newBudget = new Budget(category, amount, threshold);
        budgets.add(newBudget);

        view.displayBudgets(budgets);
        view.showSuccess("Budget created successfully!");
    }


    public void onExpenseAdded(String category, double amount) {

        Budget budget = findByCategory(category);

        if (budget == null) return;

        budget.add_expense(amount);

        BudgetStatus status = budget.calc_status();

        if (status == BudgetStatus.EXCEEDED) {
            double overage = budget.getSpentAmount() - budget.getBudgetAmount();
            view.showExceededAlert(budget.getCategory(), overage);

        } else if (status == BudgetStatus.NEAR_LIMIT) {
            view.showNearLimitAlert(budget.getCategory(), budget.calc_percentage());
        }

        view.displayBudgets(budgets);
    }


    public void loadBudgets() {

        view.displayBudgets(budgets);
    }


    private Budget findByCategory(String category) {
        for (Budget b : budgets) {
            if (b.getCategory().equals(category)) {
                return b;
            }
        }
        return null;
    }


    public List<Budget> getBudgets() {
        return budgets;
    }
}