package model;

/**
 * Represents the possible states of a FinancialGoal.
 * 
 * The status is determined based on the user's progress
 * and the remaining time before the deadline.
 */
public enum GoalStatus {

    /** Goal is still in progress */
    IN_PROGRESS,

    /** Goal has been fully achieved */
    COMPLETED,

    /** Deadline is near and progress is insufficient */
    BEHIND_SCHEDULE
}
