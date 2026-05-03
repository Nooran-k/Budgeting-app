package model;

import java.io.Serializable;
import java.time.LocalDate;


public class Budget  implements Serializable {
   private static final long serialVersionUID = 1L;
    private String categoryName; 
    private double budgetamount;   
    private double spentAmount;   
    private int alertThreshold; 
    private BudgetStatus status;  
  
    
 
    public Budget(String categoryname , double budgetamount, int alertThreshold ){
          this.categoryName = categoryname;
          this.budgetamount = budgetamount;
          this.alertThreshold = alertThreshold;
          this.spentAmount = 0;
          this.status = BudgetStatus.ON_TRACK;
    }
    public double calc_percentage (){
        return (spentAmount/budgetamount) * 100;
    }
    public BudgetStatus calc_status(){
          double percent = calc_percentage();
          if (percent >= 100) { 
              return BudgetStatus.EXCEEDED; }
          if (percent >= alertThreshold) { 
              return BudgetStatus.NEAR_LIMIT; }
          return BudgetStatus.ON_TRACK; 
    }
    public void add_expense(double amount){
        this.spentAmount += amount;
        this.status = calc_status();
    }
    public double calculateRemaining() { 
    return budgetamount - spentAmount; 
}
    public void setCategory(String category) {
        this.categoryName = category; }
public void setBudgetAmount(double budgetamount) { 
    this.budgetamount = budgetamount; }
public void setSpentAmount(double spentAmount) {
    this.spentAmount = spentAmount; }
public void setAlertThreshold(int alertThreshold) { 
    this.alertThreshold = alertThreshold; }
    
public String getCategory() { 
    return categoryName; }
  public int getAlertThreshold() { 
      return alertThreshold; }
    public double getBudgetAmount() { 
        return budgetamount; }
    public double getSpentAmount(){
        return spentAmount; }

}
