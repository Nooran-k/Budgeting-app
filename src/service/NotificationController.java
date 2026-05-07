/**
 * Controller responsible for managing notifications.
 */
package controller;

import data.Database;
import model.Notification;
import model.Notification.NotificationType;

public class NotificationController {

    /**
     * Sends a notification.
     *
     * @param userEmail user email
     * @param type notification type
     * @param message notification message
     * @return created Notification
     */
    public Notification sendNotification(String userEmail,
                                         NotificationType type,
                                         String message) {

        Notification n = new Notification(0, userEmail, type, message);
        Database.saveNotification(n);
        return n;
    }

    /**
     * Sends budget alert notification.
     */
    public void pushBudgetAlert(String userEmail, String categoryName,
                                double percentage, boolean exceeded) {

        NotificationType type;
        String message;

        if (exceeded) {
            type = NotificationType.BUDGET_EXCEEDED;
            message = "Budget Exceeded — " + categoryName
                    + "! You've used "
                    + String.format("%.1f", percentage) + "% of your budget.";
        } else {
            type = NotificationType.BUDGET_ALERT;
            message = "Budget Alert — " + categoryName
                    + ": You've used "
                    + String.format("%.1f", percentage) + "% of your budget.";
        }

        sendNotification(userEmail, type, message);
    }

    /**
     * Sends goal completion notification.
     */
    public void pushGoalCompleteAlert(String userEmail, String goalName) {

        sendNotification(userEmail, NotificationType.GOAL_COMPLETE,
                "Goal Complete! You reached: " + goalName);
    }

    /**
     * Gets latest unread notification.
     *
     * @param userEmail user email
     * @return latest unread notification
     */
    public Notification getLatestUnread(String userEmail) {
        return Database.loadLatestUnread(userEmail);
    }
}
