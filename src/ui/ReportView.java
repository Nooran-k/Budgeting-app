package view;

import controller.ReportController;
import model.Report;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

public class ReportView extends JPanel {

    private final ReportController rc;
    private final int userId = 1;

    private JComboBox<String>  cbMonth;
    private JComboBox<Integer> cbYear;
    private JLabel             lblIncome;
    private JLabel             lblExpenses;
    private JLabel             lblSaved;
    private JLabel             lblInsight;
    private DefaultTableModel  tableModel;
    private ChartPanel         chartPanel;

    private final String[] MONTHS = {
        "January","February","March","April",
        "May","June","July","August",
        "September","October","November","December"
    };

   
    private static final Color[] COLORS = {
        Color.RED, Color.BLUE, Color.GREEN,
        Color.ORANGE, Color.MAGENTA, Color.CYAN,
        Color.PINK, Color.YELLOW
    };

    public ReportView(ReportController rc) {
        this.rc = rc;
        build();
        generate();
    }

    private void build() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buildTop(),    BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildBottom(), BorderLayout.SOUTH);
    }

   

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

    

    private JPanel buildCenter() {
        JPanel center = new JPanel(new GridLayout(1, 2, 10, 0));

        
        tableModel = new DefaultTableModel(
            new String[]{"Category", "Spent (EGP)", "%"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(tableModel);
        table.setRowHeight(22);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder("Category Breakdown"));
        center.add(scroll);

        
        chartPanel = new ChartPanel();
        chartPanel.setBorder(BorderFactory.createTitledBorder("Charts"));
        center.add(chartPanel);

        return center;
    }

   

    private JPanel buildBottom() {
        JPanel p = new JPanel(new GridLayout(2, 1, 0, 5));

        JPanel row = new JPanel(new GridLayout(1, 3, 5, 0));
        lblIncome   = makeCard("Income",   "0.00", new Color(220, 255, 220));
        lblExpenses = makeCard("Expenses", "0.00", new Color(255, 220, 220));
        lblSaved    = makeCard("Saved",    "0.00", new Color(220, 230, 255));
        row.add(lblIncome);
        row.add(lblExpenses);
        row.add(lblSaved);
        p.add(row);

        lblInsight = new JLabel(" ", SwingConstants.CENTER);
        lblInsight.setFont(new Font("Arial", Font.ITALIC, 12));
        p.add(lblInsight);

        return p;
    }

    private JLabel makeCard(String title, String val, Color bg) {
        JLabel l = new JLabel(
            "<html><center><b>" + title + "</b><br>"
            + val + " EGP</center></html>",
            SwingConstants.CENTER);
        l.setOpaque(true);
        l.setBackground(bg);
        l.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        return l;
    }

   
    private void generate() {
        int month = cbMonth.getSelectedIndex() + 1;
        int year  = (int) cbYear.getSelectedItem();

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end   = start.withDayOfMonth(start.lengthOfMonth());

        if (!rc.hasData(userId, start, end)) {
            tableModel.setRowCount(0);
            chartPanel.clear();
            lblIncome.setText(  "<html><center><b>Income</b><br>0.00 EGP</center></html>");
            lblExpenses.setText("<html><center><b>Expenses</b><br>0.00 EGP</center></html>");
            lblSaved.setText(   "<html><center><b>Saved</b><br>0.00 EGP</center></html>");
            lblInsight.setText("No data for this period.");
            lblInsight.setForeground(Color.GRAY);
            return;
        }

        Report r = rc.generateMonthlyReport(userId, month, year);

       
        tableModel.setRowCount(0);
        Map<String, Double> cats    = r.getCategoryTotals();
        double              totalEx = r.getTotalExpenses();
        for (Map.Entry<String, Double> e : cats.entrySet()) {
            double pct = totalEx > 0 ? (e.getValue() / totalEx) * 100 : 0;
            tableModel.addRow(new Object[]{
                e.getKey(),
                String.format("%.2f", e.getValue()),
                String.format("%.1f%%", pct)
            });
        }

        chartPanel.setData(cats, r.getTotalIncome(), r.getTotalExpenses());

       
        lblIncome.setText(String.format(
            "<html><center><b>Income</b><br>%.2f EGP</center></html>",
            r.getTotalIncome()));
        lblExpenses.setText(String.format(
            "<html><center><b>Expenses</b><br>%.2f EGP</center></html>",
            r.getTotalExpenses()));
        lblSaved.setText(String.format(
            "<html><center><b>Saved</b><br>%.2f EGP</center></html>",
            r.getNetSavings()));

        lblInsight.setText(r.getInsightMessage());
        lblInsight.setForeground(
            r.getNetSavings() >= 0 ? new Color(0, 140, 0) : Color.RED);
    }

    

    private class ChartPanel extends JPanel {

        private Map<String, Double> cats    = new LinkedHashMap<>();
        private double              income  = 0;
        private double              expense = 0;

        ChartPanel() {
            setBackground(Color.WHITE);
        }

        void setData(Map<String, Double> cats, double income, double expense) {
            this.cats    = cats;
            this.income  = income;
            this.expense = expense;
            repaint();
        }

        void clear() {
            this.cats    = new LinkedHashMap<>();
            this.income  = 0;
            this.expense = 0;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            int w = getWidth();
            int h = getHeight();

           
            int half = h / 2;

            drawPie(g, w, half);
            drawBar(g, w, half, h);
        }

       
        private void drawPie(Graphics g, int w, int half) {
            if (cats.isEmpty()) return;

           
            double total = cats.values().stream()
                               .mapToDouble(Double::doubleValue).sum();
            if (total == 0) return;

           
            int size = Math.min(w, half) - 40;
            int x    = (w - size) / 2;
            int y    = (half - size) / 2;

            List<String> keys   = new ArrayList<>(cats.keySet());
            List<Double>  vals  = new ArrayList<>(cats.values());

            int startAngle = 0;

            for (int i = 0; i < keys.size(); i++) {
                int arc = (int) ((vals.get(i) / total) * 360);

               
                g.setColor(COLORS[i % COLORS.length]);
                g.fillArc(x, y, size, size, startAngle, arc);

                
                g.setColor(Color.WHITE);
                g.drawArc(x, y, size, size, startAngle, arc);

                startAngle += arc;
            }

           
            int lx = 5;
            int ly = half - 18;
            g.setFont(new Font("Arial", Font.PLAIN, 10));
            for (int i = 0; i < keys.size(); i++) {
                if (lx + 80 > w) break; // stop if no more space
                g.setColor(COLORS[i % COLORS.length]);
                g.fillRect(lx, ly, 10, 10);
                g.setColor(Color.BLACK);
                g.drawString(keys.get(i), lx + 12, ly + 9);
                lx += g.getFontMetrics().stringWidth(keys.get(i)) + 22;
            }
        }

       

        private void drawBar(Graphics g, int w, int half, int h) {
            
            int chartX = 40;  
            int chartY = half + 10;
            int chartW = w - chartX - 10;
            int chartH = h - chartY - 30; 

            if (chartH <= 0 || chartW <= 0) return;

            double maxVal = Math.max(income, expense);
            if (maxVal == 0) return;

          
            g.setColor(Color.DARK_GRAY);
            g.drawLine(chartX, chartY, chartX, chartY + chartH);
            
            g.drawLine(chartX, chartY + chartH, chartX + chartW, chartY + chartH);

           
            g.setFont(new Font("Arial", Font.PLAIN, 9));
            g.setColor(Color.GRAY);
            g.drawString("0",       5, chartY + chartH);
            g.drawString(fmt(maxVal / 2), 2, chartY + chartH / 2);
            g.drawString(fmt(maxVal),     2, chartY + 10);

            int barW   = chartW / 5;   
            int gap    = chartW / 8;   

           
            int incomeH = (int) ((income  / maxVal) * chartH);
            int incomeX = chartX + gap;
            int incomeY = chartY + chartH - incomeH;

            g.setColor(new Color(60, 180, 60));
            g.fillRect(incomeX, incomeY, barW, incomeH);

           
            int expH = (int) ((expense / maxVal) * chartH);
            int expX  = incomeX + barW + gap;
            int expY  = chartY + chartH - expH;

            g.setColor(new Color(220, 60, 60));
            g.fillRect(expX, expY, barW, expH);

           
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 10));
            g.drawString("Income",   incomeX,     chartY + chartH + 14);
            g.drawString("Expenses", expX - 5,    chartY + chartH + 14);

            g.setFont(new Font("Arial", Font.PLAIN, 9));
            g.drawString(fmt(income),  incomeX + 2, incomeY - 3);
            g.drawString(fmt(expense), expX    + 2, expY    - 3);
        }

       
        private String fmt(double v) {
            return String.format("%.0f", v);
        }
    }
}