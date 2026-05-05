package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Map;


public class Report implements Serializable {

    private static final long serialVersionUID = 1L;

    private int        reportId;
    private int        userId;
    private LocalDate  startDate;
    private LocalDate  endDate;
    private ReportType type;

    
    private Map<String, Double> categoryTotals;


    private double totalIncome;
    private double totalExpenses;

    private String insightMessage;

   
    public Report(int reportId, int userId, LocalDate startDate,
                  LocalDate endDate, ReportType type,
                  Map<String, Double> categoryTotals,
                  double totalIncome, double totalExpenses) {
        this.reportId       = reportId;
        this.userId         = userId;
        this.startDate      = startDate;
        this.endDate        = endDate;
        this.type           = type;
        this.categoryTotals = categoryTotals;
        this.totalIncome    = totalIncome;
        this.totalExpenses  = totalExpenses;
        this.insightMessage = generateInsight();
    }

   
    private String generateInsight() {
        if (categoryTotals == null || categoryTotals.isEmpty()) {
            return "No spending data for this period.";
        }

        
        String topCategory = categoryTotals.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Unknown");

        double topAmount = categoryTotals.getOrDefault(topCategory, 0.0);

        if (totalExpenses > totalIncome) {
            return String.format(
                "⚠ You spent %.2f EGP more than you earned. "
                + "Your biggest expense is %s (%.2f EGP).",
                totalExpenses - totalIncome, topCategory, topAmount);
        } else {
            return String.format(
                "✓ Great! You saved %.2f EGP this period. "
                + "Your biggest expense is %s (%.2f EGP).",
                totalIncome - totalExpenses, topCategory, topAmount);
        }
    }

   
    public int                  getReportId()       { return reportId; }
    public int                  getUserId()         { return userId; }
    public LocalDate            getStartDate()      { return startDate; }
    public LocalDate            getEndDate()        { return endDate; }
    public ReportType           getType()           { return type; }
    public Map<String, Double>  getCategoryTotals() { return categoryTotals; }
    public double               getTotalIncome()    { return totalIncome; }
    public double               getTotalExpenses()  { return totalExpenses; }
    public String               getInsightMessage() { return insightMessage; }
    public double               getNetSavings()     { return totalIncome - totalExpenses; }
}