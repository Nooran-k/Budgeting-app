package model;

/**
 * Represents the possible states of a Budget.
 * 
 * This enum is used to determine the current condition of a budget
 * based on spending compared to the defined limit.
 * 
 * It is also used in the UI to control visual indicators such as colors:
 * <ul>
 *   <li>ON_TRACK   → Budget usage is within safe limits (green)</li>
 *   <li>NEAR_LIMIT → Spending has reached the alert threshold (orange)</li>
 *   <li>EXCEEDED   → Spending has exceeded the budget limit (red)</li>
 * </ul>
 */
public enum BudgetStatus {

    /** Budget usage is within acceptable range */
    ON_TRACK,

    /** Budget usage has reached the alert threshold */
    NEAR_LIMIT,

    /** Budget limit has been exceeded */
    EXCEEDED
}
