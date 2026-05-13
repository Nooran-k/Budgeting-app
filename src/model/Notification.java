package model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Represents a notification sent to the user.
 * 
 * Notifications are used to alert users about important events
 * such as budget limits or goal progress.
 */
public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Types of notifications supported by the system.
     */
    public enum NotificationType {

        /** Alert when budget is near limit */
        BUDGET_ALERT,

        /** Alert when budget is exceeded */
        BUDGET_EXCEEDED,

        /** Notification when goal is completed */
        GOAL_COMPLETE,

        /** Alert when goal deadline is approaching */
        GOAL_DEADLINE_APPROACHING
    }

    private int              notificationId;
    private String           userEmail;
    private NotificationType type;
    private String           message;
    private boolean          isRead;
    private LocalDateTime    timestamp;

    /**
     * Constructs a new Notification.
     *
     * @param notificationId unique identifier
     * @param userEmail recipient email
     * @param type notification type
     * @param message notification content
     */
    public Notification(int notificationId, String userEmail,
                        NotificationType type, String message) {

        this.notificationId = notificationId;
        this.userEmail      = userEmail;
        this.type           = type;
        this.message        = message;
        this.isRead         = false;
        this.timestamp      = LocalDateTime.now();
    }

    /**
     * Marks the notification as read.
     */
    public void markAsRead() {
        this.isRead = true;
    }


    // Getters

    public int getNotificationId() { return notificationId; }

    public String getUserEmail() { return userEmail; }

    public NotificationType getType() { return type; }

    public String getMessage() { return message; }

    public boolean isRead() { return isRead; }

    public LocalDateTime getTimestamp() { return timestamp; }

    /**
     * Sets notification ID.
     * @param id new ID
     */
    public void setNotificationId(int id) {
        this.notificationId = id;
    }
}
