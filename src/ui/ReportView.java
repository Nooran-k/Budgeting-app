package ui;

import service.ReportController;
import model.Report;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Panel responsible for generating and displaying financial reports.
 *
 * Features:
 * - Generate monthly reports
 * - Display category-wise expense breakdown
 * - Show total income, expenses, and savings
 * - Visualize data using pie chart and bar chart
 *
 * Controller:
 * - ReportController
 */
public class ReportView extends JPanel {

    /** Controller used to generate reports */
    private final ReportController rc;

    /** Logged-in user email */
    private final String userEmail;

    private JComboBox<String>  cbMonth;
    private JComboBox<Integer> cbYear;
    private JLabel             lblIncome, lblExpenses, lblSaved, lblInsight;
    private DefaultTableModel  tableModel;
    private ChartPanel         chartPanel;

    /** Month names */
    private final String[] MONTHS = {
        "January","February","March","April",
        "May","June","July","August",
        "September","October","November","December"
    };

    /** Colors used in charts */
    private static final Color[] COLORS = {
        Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE,
        Color.MAGENTA, Color.CYAN, Color.PINK, Color.YELLOW
    };

    /**
     * Initializes the report panel.
     *
     * @param rc Report controller
     * @param userEmail logged-in user email
     */
    public ReportView(ReportController rc, String userEmail) {
        this.rc = rc;
        this.userEmail = userEmail;
        build();
        generate();
    }

    /**
     * Builds UI layout.
     */
    private void build() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buildTop(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildBottom(), BorderLayout.SOUTH);
    }

    /**
     * Builds top controls (month/year selection).
     */
    private JPanel buildTop() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));

        p.add(new JLabel("Month:"));
        cbMonth = new JComboBox<>(MONTHS);
        cbMonth.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
        p.add(cbMonth);

        p.add(new JLabel("Year:"));
        cbYear = new JComboBox<>();
        int yr = LocalDate.now().getYear();
        for (int y = yr - 2; y <= yr; y++) cbYear.addItem(y);
        cbYear.setSelectedItem(yr);
        p.add(cbYear);

        JButton btn = new JButton("Generate");
        btn.addActionListener(e -> generate());
        p.add(btn);

        return p;
    }

    /**
     * Builds center (table + chart).
     */
    private JPanel buildCenter() {
        JPanel center = new JPanel(new GridLayout(1, 2, 10, 0));

        tableModel = new DefaultTableModel(
                new String[]{"Category", "Spent (EGP)", "%"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(tableModel);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder("Category Breakdown"));

        center.add(scroll);

        chartPanel = new ChartPanel();
        chartPanel.setBorder(BorderFactory.createTitledBorder("Charts"));
        center.add(chartPanel);

        return center;
    }

    /**
     * Builds bottom (summary cards).
     */
    private JPanel buildBottom() {
        JPanel p = new JPanel(new GridLayout(2, 1, 0, 5));

        JPanel row = new JPanel(new GridLayout(1, 3, 5, 0));

        lblIncome   = makeCard("Income", "0.00", new Color(220,255,220));
        lblExpenses = makeCard("Expenses", "0.00", new Color(255,220,220));
        lblSaved    = makeCard("Saved", "0.00", new Color(220,230,255));

        row.add(lblIncome);
        row.add(lblExpenses);
        row.add(lblSaved);

        p.add(row);

        lblInsight = new JLabel(" ", SwingConstants.CENTER);
        p.add(lblInsight);

        return p;
    }

    /**
     * Creates a summary card.
     */
    private JLabel makeCard(String title, String val, Color bg) {
        JLabel l = new JLabel(
                "<html><center><b>" + title + "</b><br>" + val
                        + " EGP</center></html>", SwingConstants.CENTER);
        l.setOpaque(true);
        l.setBackground(bg);
        return l;
    }

    /**
     * Generates report based on selected period.
     */
    private void generate() {
        int month = cbMonth.getSelectedIndex() + 1;
        int year  = (int) cbYear.getSelectedItem();

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end   = start.withDayOfMonth(start.lengthOfMonth());

        if (!rc.hasData(userEmail, start, end)) {
            tableModel.setRowCount(0);
            chartPanel.clear();
            lblInsight.setText("No data.");
            return;
        }

        Report r = rc.generateMonthlyReport(userEmail, month, year);

        tableModel.setRowCount(0);

        Map<String, Double> cats = r.getCategoryTotals();
        double total = r.getTotalExpenses();

        for (Map.Entry<String, Double> e : cats.entrySet()) {
            double pct = total > 0 ? (e.getValue() / total) * 100 : 0;

            tableModel.addRow(new Object[]{
                    e.getKey(),
                    String.format("%.2f", e.getValue()),
                    String.format("%.1f%%", pct)
            });
        }

        chartPanel.setData(cats, r.getTotalIncome(), r.getTotalExpenses());

        lblIncome.setText("Income: " + r.getTotalIncome());
        lblExpenses.setText("Expenses: " + r.getTotalExpenses());
        lblSaved.setText("Saved: " + r.getNetSavings());
        lblInsight.setText(r.getInsightMessage());
    }

    /**
     * Inner class responsible for drawing charts.
     */
    private class ChartPanel extends JPanel {

        private Map<String, Double> cats = new LinkedHashMap<>();
        private double income = 0;
        private double expense = 0;

        void setData(Map<String, Double> cats, double income, double expense) {
            this.cats = cats;
            this.income = income;
            this.expense = expense;
            repaint();
        }

        void clear() {
            cats.clear();
            income = 0;
            expense = 0;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawBar(g);
        }

        /**
         * Draws income vs expenses bar chart.
         */
        private void drawBar(Graphics g) {
            int w = getWidth();
            int h = getHeight();

            int barWidth = 50;
            int maxHeight = h - 50;

            double maxVal = Math.max(income, expense);
            if (maxVal == 0) return;

            int incHeight = (int) ((income / maxVal) * maxHeight);
            int expHeight = (int) ((expense / maxVal) * maxHeight);

            g.setColor(Color.GREEN);
            g.fillRect(100, h - incHeight - 20, barWidth, incHeight);

            g.setColor(Color.RED);
            g.fillRect(200, h - expHeight - 20, barWidth, expHeight);

            g.setColor(Color.BLACK);
            g.drawString("Income", 100, h - 5);
            g.drawString("Expenses", 200, h - 5);
        }
    }
}
