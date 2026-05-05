package model;

import java.io.Serializable;
import java.time.LocalDateTime;


public class Transaction implements Serializable {

    
    private static final long serialVersionUID = 1L;

    private int transactionId;
    private int userId;           
    private double amount;       
    private TransactionType type; 
    private int categoryId;       
    private String categoryName;  
    private LocalDateTime date;
    private String description;   
    private String paymentMethod; 

    
    public Transaction(int transactionId, int userId, double amount, TransactionType type, int categoryId,
        String categoryName, String description,String paymentMethod) {
        this.transactionId  = transactionId;
        this.userId         = userId;
        this.amount         = amount;
        this.type           = type;
        this.categoryId     = categoryId;
        this.categoryName   = categoryName;
        this.date           = LocalDateTime.now(); 
        this.description    = description;
        this.paymentMethod  = paymentMethod;
    }

   
    public int    getTransactionId()  { return transactionId; }
    public int    getUserId()         { return userId; }
    public double getAmount()         { return amount; }
    public TransactionType getType()  { return type; }
    public int    getCategoryId()     { return categoryId; }
    public String getCategoryName()   { return categoryName; }
    public LocalDateTime getDate()    { return date; }
    public String getDescription()    { return description; }
    public String getPaymentMethod()  { return paymentMethod; }

   
    public void setAmount(double amount)               { this.amount = amount; }
    public void setType(TransactionType type)          { this.type = type; }
    public void setCategoryId(int id)                  { this.categoryId = id; }
    public void setCategoryName(String name)           { this.categoryName = name; }
    public void setDate(LocalDateTime date)            { this.date = date; }
    public void setDescription(String desc)            { this.description = desc; }
    public void setPaymentMethod(String method)        { this.paymentMethod = method; }

    @Override
    public String toString() {
        
        return String.format("[%s] %s %.2f EGP - %s",
                type, categoryName, amount,
                date.toLocalDate().toString());
    }
}