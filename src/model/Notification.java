package model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Notification implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum NotificationType {
        BUDGET_ALERT, BUDGET_EXCEEDED,
        GOAL_COMPLETE, GOAL_DEADLINE_APPROACHING
    }

    private int notificationId;
    private String userEmail;       
    private NotificationType type;
    private String message;
    private boolean isRead;
    private LocalDateTime timestamp;

    public Notification(int notificationId, String userEmail, NotificationType type, String message) {
        this.notificationId = notificationId;
        this.userEmail = userEmail;
        this.type = type;
        this.message = message;
        this.isRead = false;
        this.timestamp = LocalDateTime.now();
    }

    public void markAsRead(){ this.isRead = true; }

    public int getNotificationId(){ 
        return notificationId; }
    public String getUserEmail(){ 
        return userEmail; }
    public NotificationType getType(){ 
        return type; }
    public String getMessage(){ 
        return message; }
    public boolean isRead(){ 
        return isRead; }
    public LocalDateTime getTimestamp(){ 
        return timestamp; }

    public void setNotificationId(int id){
        this.notificationId = id; }
}