/**
 * Controller responsible for generating financial reports.
 */
package service;

import data.Database;
import model.Report;
import model.ReportType;
import model.Transaction;
import model.TransactionType;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportController {

    private static int nextReportId = 1;

    /**
     * Generates a monthly report.
     */
    public Report generateMonthlyReport(String userEmail, int month, int year) {

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        return generate(userEmail, start, end);
    }

    /**
     * Generates a custom report.
     */
    public Report generateCustomReport(String userEmail,
                                       LocalDate start, LocalDate end) {

        return generate(userEmail, start, end);
    }

    /**
     * Internal method to generate report.
     */
    private Report generate(String userEmail,
                             LocalDate start, LocalDate end) {

        List<Transaction> all = Database.loadTransactions(userEmail);

        List<Transaction> filtered = all.stream()
            .filter(t -> !t.getDate().toLocalDate().isBefore(start))
            .filter(t -> !t.getDate().toLocalDate().isAfter(end))
            .collect(Collectors.toList());

        Map<String, Double> cats = new HashMap<>();

        for (Transaction t : filtered) {
            if (t.getType() == TransactionType.EXPENSE) {
                cats.merge(t.getCategoryName(), t.getAmount(), Double::sum);
            }
        }

        double income = filtered.stream()
            .filter(t -> t.getType() == TransactionType.INCOME)
            .mapToDouble(Transaction::getAmount).sum();

        double expense = filtered.stream()
            .filter(t -> t.getType() == TransactionType.EXPENSE)
            .mapToDouble(Transaction::getAmount).sum();

        return new Report(nextReportId++, userEmail,
                start, end, ReportType.MONTHLY_SUMMARY,
                cats, income, expense);
    }

    /**
     * Checks if data exists in a date range.
     */
    public boolean hasData(String userEmail,
                           LocalDate start, LocalDate end) {

        return Database.hasTransactions(userEmail, start, end);
    }
}
