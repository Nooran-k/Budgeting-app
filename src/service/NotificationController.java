package controller;

import data.Database;
import model.Notification;
import model.Notification.NotificationType;

public class NotificationController {

    public Notification sendNotification(String userEmail,NotificationType type, String message) {
        Notification n = new Notification(0, userEmail, type, message);
        Database.saveNotification(n);
        return n;
    }

    public void pushBudgetAlert(String userEmail, String categoryName, double percentage, boolean exceeded) {
        NotificationType type;
        String message;
        if (exceeded) {
            type    = NotificationType.BUDGET_EXCEEDED;
            message = "Budget Exceeded — " + categoryName
                    + "! You've used "
                    + String.format("%.1f", percentage) + "% of your budget.";
        } else {
            type    = NotificationType.BUDGET_ALERT;
            message = "Budget Alert — " + categoryName
                    + ": You've used "
                    + String.format("%.1f", percentage) + "% of your budget.";
        }
        sendNotification(userEmail, type, message);
    }

    public void pushGoalCompleteAlert(String userEmail, String goalName) {
        sendNotification(userEmail, NotificationType.GOAL_COMPLETE,
                "Goal Complete! You reached: " + goalName);
    }

    public Notification getLatestUnread(String userEmail) {
        return Database.loadLatestUnread(userEmail);
    }
}
