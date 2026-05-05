package controller;

import data.DataStore;
import model.Notification;
import model.Notification.NotificationType;

import java.util.List;


public class NotificationController {

    private List<Notification> notifications;

    public NotificationController() {
        
        this.notifications = DataStore.loadNotifications();
    }

   
    public Notification sendNotification(int userId, NotificationType type, String message) {
        int id = DataStore.nextNotifId();
        Notification notif = new Notification(id, userId, type, message);
        notifications.add(notif);
        DataStore.saveNotifications(notifications); // persist immediately
        return notif;
    }

    
    public void pushBudgetAlert(int userId, String categoryName,
                                double percentage, boolean exceeded) {
        NotificationType type;
        String message;
        if (exceeded) {
            type    = NotificationType.BUDGET_EXCEEDED;
            message = "Budget Exceeded — " + categoryName +
                      "! You've exceeded your budget by " +
                      String.format("%.2f", percentage - 100) + "%.";
        } else {
            type    = NotificationType.BUDGET_ALERT;
            message = "Budget Alert — " + categoryName + ": You've used " +
                      String.format("%.1f", percentage) + "% of your budget.";
        }
        sendNotification(userId, type, message);
    }

    public void pushGoalCompleteAlert(int userId, String goalName) {
        String message = "Goal Complete! You've reached your goal: " + goalName;
        sendNotification(userId, NotificationType.GOAL_COMPLETE, message);
    }

    
    public List<Notification> getUserNotifications(int userId) {
        return notifications.stream()
                .filter(n -> n.getUserId() == userId)
                .collect(java.util.stream.Collectors.toList());
    }

    
    public Notification getLatestUnread(int userId) {
        return notifications.stream()
                .filter(n -> n.getUserId() == userId && !n.isRead())
                .reduce((first, second) -> second) // get last element
                .orElse(null);
    }
}