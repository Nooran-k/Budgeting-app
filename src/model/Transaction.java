package model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Represents a financial transaction made by a user.
 * 
 * A transaction can be either income or expense and is associated
 * with a category, date, and payment method.
 */
public class Transaction implements Serializable {

    private static final long serialVersionUID = 1L;

    private int             transactionId;
    private String          userEmail;
    private double          amount;
    private TransactionType type;
    private int             categoryId;
    private String          categoryName;
    private LocalDateTime   date;
    private String          description;
    private String          paymentMethod;

    /**
     * Constructs a new Transaction.
     *
     * @param transactionId unique identifier
     * @param userEmail user email
     * @param amount transaction amount
     * @param type transaction type (income or expense)
     * @param categoryId category ID
     * @param categoryName category name
     * @param description description of transaction
     * @param paymentMethod method used for payment
     */
    public Transaction(int transactionId, String userEmail, double amount,
                       TransactionType type, int categoryId,
                       String categoryName, String description,
                       String paymentMethod) {

        this.transactionId = transactionId;
        this.userEmail     = userEmail;
        this.amount        = amount;
        this.type          = type;
        this.categoryId    = categoryId;
        this.categoryName  = categoryName;
        this.date          = LocalDateTime.now();
        this.description   = description;
        this.paymentMethod = paymentMethod;
    }


    // Getters

    public int getTransactionId() { return transactionId; }

    public String getUserEmail() { return userEmail; }

    public double getAmount() { return amount; }

    public TransactionType getType() { return type; }

    public int getCategoryId() { return categoryId; }

    public String getCategoryName() { return categoryName; }

    public LocalDateTime getDate() { return date; }

    public String getDescription() { return description; }

    public String getPaymentMethod() { return paymentMethod; }


    // Setters

    public void setTransactionId(int id) { this.transactionId = id; }

    public void setAmount(double amount) { this.amount = amount; }

    public void setType(TransactionType type) { this.type = type; }

    public void setCategoryId(int id) { this.categoryId = id; }

    public void setCategoryName(String name) { this.categoryName = name; }

    public void setDate(LocalDateTime date) { this.date = date; }

    public void setDescription(String desc) { this.description = desc; }

    public void setPaymentMethod(String method) { this.paymentMethod = method; }

    /**
     * Returns a formatted string representation of the transaction.
     *
     * @return formatted transaction string
     */
    @Override
    public String toString() {
        return String.format("[%s] %s %.2f EGP - %s",
                type, categoryName, amount,
                date.toLocalDate().toString());
    }
}
