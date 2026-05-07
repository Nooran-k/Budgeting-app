package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Map;

/**
 * Represents a financial report generated for a user.
 * 
 * A report summarizes income, expenses, and spending distribution
 * over a specific period. It also generates an insight message
 * to help the user understand their financial behavior.
 */
public class Report implements Serializable {

    private static final long serialVersionUID = 1L;

    private int                 reportId;
    private String              userEmail;
    private LocalDate           startDate;
    private LocalDate           endDate;
    private ReportType          type;
    private Map<String, Double> categoryTotals;
    private double              totalIncome;
    private double              totalExpenses;
    private String              insightMessage;

    /**
     * Constructs a new Report.
     *
     * @param reportId unique identifier for the report
     * @param userEmail email of the user
     * @param startDate start date of the report period
     * @param endDate end date of the report period
     * @param type type of the report
     * @param categoryTotals total spending per category
     * @param totalIncome total income during the period
     * @param totalExpenses total expenses during the period
     */
    public Report(int reportId, String userEmail, LocalDate startDate,
                  LocalDate endDate, ReportType type,
                  Map<String, Double> categoryTotals,
                  double totalIncome, double totalExpenses) {

        this.reportId       = reportId;
        this.userEmail      = userEmail;
        this.startDate      = startDate;
        this.endDate        = endDate;
        this.type           = type;
        this.categoryTotals = categoryTotals;
        this.totalIncome    = totalIncome;
        this.totalExpenses  = totalExpenses;
        this.insightMessage = buildInsight();
    }

    /**
     * Generates a financial insight message based on report data.
     *
     * @return a descriptive insight message for the user
     */
    private String buildInsight() {

        if (categoryTotals == null || categoryTotals.isEmpty())
            return "No spending data for this period.";

        String top = categoryTotals.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Unknown");

        double saved = totalIncome - totalExpenses;

        if (saved < 0)
            return String.format(
                "You spent %.2f EGP more than you earned. Biggest: %s",
                -saved, top
            );

        return String.format(
            "You saved %.2f EGP. Biggest expense: %s",
            saved, top
        );
    }

    /** 
     * @param getNetSavings(
     * @return int
     */
    // Getters

    public int getReportId() { return reportId; }

    public String getUserEmail() { return userEmail; }

    public LocalDate getStartDate() { return startDate; }

    public LocalDate getEndDate() { return endDate; }

    public ReportType getType() { return type; }

    public Map<String, Double> getCategoryTotals() { return categoryTotals; }

    public double getTotalIncome() { return totalIncome; }

    public double getTotalExpenses() { return totalExpenses; }

    public String getInsightMessage() { return insightMessage; }

    /**
     * Calculates net savings (income - expenses).
     *
     * @return net savings value
     */
    public double getNetSavings() {
        return totalIncome - totalExpenses;
    }
}
