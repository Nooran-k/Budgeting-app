package controller;

import data.DataStore;
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

    public Report generateReport(int userId, ReportType type,
                                 LocalDate startDate, LocalDate endDate) {
       
        List<Transaction> all = DataStore.loadTransactions();
        List<Transaction> filtered = all.stream()
                .filter(t -> t.getUserId() == userId)
                .filter(t -> !t.getDate().toLocalDate().isBefore(startDate))
                .filter(t -> !t.getDate().toLocalDate().isAfter(endDate))
                .collect(Collectors.toList());

        
        Map<String, Double> categoryTotals = getExpensesByCategory(filtered);
        double totalIncome   = getTotalIncome(filtered);
        double totalExpenses = getTotalExpenses(filtered);

        return new Report(nextReportId++, userId, startDate, endDate,
                          type, categoryTotals, totalIncome, totalExpenses);
    }

    
    public Report generateMonthlyReport(int userId, int month, int year) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end   = start.withDayOfMonth(start.lengthOfMonth());
        return generateReport(userId, ReportType.MONTHLY_SUMMARY, start, end);
    }

    
    public Report generateCustomReport(int userId,
                                       LocalDate startDate, LocalDate endDate) {
        return generateReport(userId, ReportType.CUSTOM_RANGE, startDate, endDate);
    }

   
    public Map<String, Double> getExpensesByCategory(List<Transaction> transactions) {
        Map<String, Double> result = new HashMap<>();
        for (Transaction t : transactions) {
            if (t.getType() == TransactionType.EXPENSE) {
                // merge: if category already exists, add to its total
                result.merge(t.getCategoryName(), t.getAmount(), Double::sum);
            }
        }
        return result;
    }

    
    public double getTotalIncome(List<Transaction> transactions) {
        return transactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

   
    public double getTotalExpenses(List<Transaction> transactions) {
        return transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    
    public boolean hasData(int userId, LocalDate startDate, LocalDate endDate) {
        return DataStore.loadTransactions().stream()
                .anyMatch(t -> t.getUserId() == userId
                        && !t.getDate().toLocalDate().isBefore(startDate)
                        && !t.getDate().toLocalDate().isAfter(endDate));
    }
}