package data;

import model.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class Database {

    private static final String URL = "jdbc:sqlite:budgetapp.db";

    

    private static Connection connect() throws SQLException {
        try { Class.forName("org.sqlite.JDBC"); }
        catch (ClassNotFoundException e) {
            System.err.println("SQLite driver not found");
        }
        return DriverManager.getConnection(URL);
    }

    
    public static void init() {
        String[] tables = {

           
            """
            CREATE TABLE IF NOT EXISTS users (
                name     TEXT NOT NULL,
                email    TEXT PRIMARY KEY,
                password TEXT NOT NULL
            )
            """,

            
            """
            CREATE TABLE IF NOT EXISTS transactions (
                id            INTEGER PRIMARY KEY AUTOINCREMENT,
                user_email    TEXT    NOT NULL,
                amount        REAL    NOT NULL,
                type          TEXT    NOT NULL,
                category_id   INTEGER NOT NULL,
                category_name TEXT    NOT NULL,
                description   TEXT,
                payment_method TEXT,
                date          TEXT    NOT NULL
            )
            """,

           
            """
            CREATE TABLE IF NOT EXISTS budgets (
                id              INTEGER PRIMARY KEY AUTOINCREMENT,
                user_email      TEXT    NOT NULL,
                category_id     INTEGER NOT NULL,
                category_name   TEXT    NOT NULL,
                limit_amount    REAL    NOT NULL,
                spent_amount    REAL    NOT NULL DEFAULT 0,
                start_date      TEXT    NOT NULL,
                end_date        TEXT    NOT NULL,
                alert_threshold INTEGER NOT NULL DEFAULT 80,
                status          TEXT    NOT NULL DEFAULT 'ON_TRACK'
            )
            """,

           
            """
            CREATE TABLE IF NOT EXISTS goals (
                id             INTEGER PRIMARY KEY AUTOINCREMENT,
                user_email     TEXT    NOT NULL,
                name           TEXT    NOT NULL,
                target_amount  REAL    NOT NULL,
                current_amount REAL    NOT NULL DEFAULT 0,
                deadline       TEXT    NOT NULL,
                status         TEXT    NOT NULL DEFAULT 'IN_PROGRESS'
            )
            """,

            // US#5 — notifications
            """
            CREATE TABLE IF NOT EXISTS notifications (
                id        INTEGER PRIMARY KEY AUTOINCREMENT,
                user_email TEXT   NOT NULL,
                type      TEXT    NOT NULL,
                message   TEXT    NOT NULL,
                is_read   INTEGER NOT NULL DEFAULT 0,
                timestamp TEXT    NOT NULL
            )
            """
        };

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            for (String sql : tables) stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("DB init error: " + e.getMessage());
        }
    }

    
    // USERS
   
    public static boolean emailExists(String email) {
        String sql = "SELECT 1 FROM users WHERE email = ?";
        try (Connection c = connect();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, email);
            return s.executeQuery().next();
        } catch (SQLException e) { return false; }
    }

    public static boolean saveUser(String name, String email, String pass) {
        String sql = "INSERT INTO users (name, email, password) VALUES (?,?,?)";
        try (Connection c = connect();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, name);
            s.setString(2, email);
            s.setString(3, pass);
            s.executeUpdate();
            return true;
        } catch (SQLException e) { return false; }
    }

    public static model.User findUser(String email, String pass) {
        String sql = "SELECT * FROM users WHERE email=? AND password=?";
        try (Connection c = connect();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, email);
            s.setString(2, pass);
            ResultSet rs = s.executeQuery();
            if (rs.next())
                return new model.User(
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("password"));
        } catch (SQLException e) {
            System.err.println("findUser error: " + e.getMessage());
        }
        return null;
    }

   
    // TRANSACTIONS
    
    public static boolean saveTransaction(Transaction t) {
        String sql = """
            INSERT INTO transactions
            (user_email, amount, type, category_id,
             category_name, description, payment_method, date)
            VALUES (?,?,?,?,?,?,?,?)
            """;
        try (Connection c = connect();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, t.getUserEmail());
            s.setDouble(2, t.getAmount());
            s.setString(3, t.getType().name());
            s.setInt   (4, t.getCategoryId());
            s.setString(5, t.getCategoryName());
            s.setString(6, t.getDescription());
            s.setString(7, t.getPaymentMethod());
            s.setString(8, t.getDate().toString());
            s.executeUpdate();

            // Get the generated ID back
            ResultSet keys = s.getGeneratedKeys();
            if (keys.next()) t.setTransactionId(keys.getInt(1));
            return true;
        } catch (SQLException e) {
            System.err.println("saveTransaction: " + e.getMessage());
            return false;
        }
    }

    public static List<Transaction> loadTransactions(String userEmail) {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE user_email=? ORDER BY id DESC";
        try (Connection c = connect();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, userEmail);
            ResultSet rs = s.executeQuery();
            while (rs.next()) {
                Transaction t = new Transaction(
                    rs.getInt   ("id"),
                    userEmail,
                    rs.getDouble("amount"),
                    TransactionType.valueOf(rs.getString("type")),
                    rs.getInt   ("category_id"),
                    rs.getString("category_name"),
                    rs.getString("description"),
                    rs.getString("payment_method")
                );
                t.setDate(LocalDateTime.parse(rs.getString("date")));
                list.add(t);
            }
        } catch (SQLException e) {
            System.err.println("loadTransactions: " + e.getMessage());
        }
        return list;
    }

    
    // BUDGETS
  
    public static boolean saveBudget(Budget b) {
        String sql = """
            INSERT INTO budgets
            (user_email, category_id, category_name,
             limit_amount, spent_amount, start_date, end_date,
             alert_threshold, status)
            VALUES (?,?,?,?,?,?,?,?,?)
            """;
        try (Connection c = connect();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, b.getUserEmail());
            s.setInt   (2, b.getCategoryId());
            s.setString(3, b.getCategoryName());
            s.setDouble(4, b.getLimitAmount());
            s.setDouble(5, b.getSpentAmount());
            s.setString(6, b.getStartDate().toString());
            s.setString(7, b.getEndDate().toString());
            s.setInt   (8, b.getAlertThreshold());
            s.setString(9, b.getStatus().name());
            s.executeUpdate();

            ResultSet keys = s.getGeneratedKeys();
            if (keys.next()) b.setBudgetId(keys.getInt(1));
            return true;
        } catch (SQLException e) {
            System.err.println("saveBudget: " + e.getMessage());
            return false;
        }
    }

    public static boolean updateBudget(Budget b) {
        String sql = """
            UPDATE budgets
            SET spent_amount=?, status=?
            WHERE id=?
            """;
        try (Connection c = connect();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setDouble(1, b.getSpentAmount());
            s.setString(2, b.getStatus().name());
            s.setInt   (3, b.getBudgetId());
            s.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("updateBudget: " + e.getMessage());
            return false;
        }
    }

    public static List<Budget> loadBudgets(String userEmail, int month, int year) {
        List<Budget> list = new ArrayList<>();
        // Filter by start_date year and month
        String sql = """
            SELECT * FROM budgets
            WHERE user_email=?
            AND strftime('%Y', start_date)=?
            AND strftime('%m', start_date)=?
            """;
        try (Connection c = connect();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, userEmail);
            s.setString(2, String.valueOf(year));
            s.setString(3, String.format("%02d", month));
            ResultSet rs = s.executeQuery();
            while (rs.next()) {
                Budget b = new Budget(
                    rs.getInt   ("id"),
                    userEmail,
                    rs.getInt   ("category_id"),
                    rs.getString("category_name"),
                    rs.getDouble("limit_amount"),
                    LocalDate.parse(rs.getString("start_date")),
                    LocalDate.parse(rs.getString("end_date")),
                    rs.getInt   ("alert_threshold")
                );
                b.setSpentAmount(rs.getDouble("spent_amount"));
                b.setStatus(BudgetStatus.valueOf(rs.getString("status")));
                list.add(b);
            }
        } catch (SQLException e) {
            System.err.println("loadBudgets: " + e.getMessage());
        }
        return list;
    }

    public static boolean budgetExists(String userEmail, int categoryId,
                                       LocalDate start, LocalDate end) {
        String sql = """
            SELECT 1 FROM budgets
            WHERE user_email=? AND category_id=?
            AND start_date <= ? AND end_date >= ?
            """;
        try (Connection c = connect();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, userEmail);
            s.setInt   (2, categoryId);
            s.setString(3, end.toString());
            s.setString(4, start.toString());
            return s.executeQuery().next();
        } catch (SQLException e) { return false; }
    }

    public static Budget findActiveBudget(String userEmail,
                                          int categoryId, LocalDate today) {
        String sql = """
            SELECT * FROM budgets
            WHERE user_email=? AND category_id=?
            AND start_date <= ? AND end_date >= ?
            """;
        try (Connection c = connect();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, userEmail);
            s.setInt   (2, categoryId);
            s.setString(3, today.toString());
            s.setString(4, today.toString());
            ResultSet rs = s.executeQuery();
            if (rs.next()) {
                Budget b = new Budget(
                    rs.getInt   ("id"),
                    userEmail,
                    rs.getInt   ("category_id"),
                    rs.getString("category_name"),
                    rs.getDouble("limit_amount"),
                    LocalDate.parse(rs.getString("start_date")),
                    LocalDate.parse(rs.getString("end_date")),
                    rs.getInt   ("alert_threshold")
                );
                b.setSpentAmount(rs.getDouble("spent_amount"));
                b.setStatus(BudgetStatus.valueOf(rs.getString("status")));
                return b;
            }
        } catch (SQLException e) {
            System.err.println("findActiveBudget: " + e.getMessage());
        }
        return null;
    }

    
    // GOALS
    
    public static boolean saveGoal(FinancialGoal g) {
        String sql = """
            INSERT INTO goals
            (user_email, name, target_amount, current_amount, deadline, status)
            VALUES (?,?,?,?,?,?)
            """;
        try (Connection c = connect();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, g.getUserEmail());
            s.setString(2, g.getName());
            s.setDouble(3, g.getTargetAmount());
            s.setDouble(4, g.getCurrentAmount());
            s.setString(5, g.getDeadline().toString());
            s.setString(6, g.getStatus().name());
            s.executeUpdate();

            ResultSet keys = s.getGeneratedKeys();
            if (keys.next()) g.setGoalId(keys.getInt(1));
            return true;
        } catch (SQLException e) {
            System.err.println("saveGoal: " + e.getMessage());
            return false;
        }
    }

    public static boolean updateGoal(FinancialGoal g) {
        String sql = """
            UPDATE goals
            SET current_amount=?, status=?
            WHERE id=?
            """;
        try (Connection c = connect();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setDouble(1, g.getCurrentAmount());
            s.setString(2, g.getStatus().name());
            s.setInt   (3, g.getGoalId());
            s.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("updateGoal: " + e.getMessage());
            return false;
        }
    }

    public static boolean deleteGoal(int goalId) {
        String sql = "DELETE FROM goals WHERE id=?";
        try (Connection c = connect();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, goalId);
            s.executeUpdate();
            return true;
        } catch (SQLException e) { return false; }
    }

    public static List<FinancialGoal> loadGoals(String userEmail) {
        List<FinancialGoal> list = new ArrayList<>();
        String sql = "SELECT * FROM goals WHERE user_email=? ORDER BY id DESC";
        try (Connection c = connect();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, userEmail);
            ResultSet rs = s.executeQuery();
            while (rs.next()) {
                FinancialGoal g = new FinancialGoal(
                    rs.getInt   ("id"),
                    userEmail,
                    rs.getString("name"),
                    rs.getDouble("target_amount"),
                    rs.getDouble("current_amount"),
                    LocalDate.parse(rs.getString("deadline"))
                );
                g.setStatus(GoalStatus.valueOf(rs.getString("status")));
                list.add(g);
            }
        } catch (SQLException e) {
            System.err.println("loadGoals: " + e.getMessage());
        }
        return list;
    }

   
    // NOTIFICATIONS
   

    public static boolean saveNotification(Notification n) {
        String sql = """
            INSERT INTO notifications
            (user_email, type, message, is_read, timestamp)
            VALUES (?,?,?,?,?)
            """;
        try (Connection c = connect();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, n.getUserEmail());
            s.setString(2, n.getType().name());
            s.setString(3, n.getMessage());
            s.setInt   (4, n.isRead() ? 1 : 0);
            s.setString(5, n.getTimestamp().toString());
            s.executeUpdate();

            ResultSet keys = s.getGeneratedKeys();
            if (keys.next()) n.setNotificationId(keys.getInt(1));
            return true;
        } catch (SQLException e) {
            System.err.println("saveNotification: " + e.getMessage());
            return false;
        }
    }

    public static Notification loadLatestUnread(String userEmail) {
        String sql = """
            SELECT * FROM notifications
            WHERE user_email=? AND is_read=0
            ORDER BY id DESC LIMIT 1
            """;
        try (Connection c = connect();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, userEmail);
            ResultSet rs = s.executeQuery();
            if (rs.next()) {
                Notification n = new Notification(
                    rs.getInt   ("id"),
                    userEmail,
                    Notification.NotificationType.valueOf(rs.getString("type")),
                    rs.getString("message")
                );
                return n;
            }
        } catch (SQLException e) {
            System.err.println("loadLatestUnread: " + e.getMessage());
        }
        return null;
    }

    public static boolean markNotificationRead(int id) {
        String sql = "UPDATE notifications SET is_read=1 WHERE id=?";
        try (Connection c = connect();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, id);
            s.executeUpdate();
            return true;
        } catch (SQLException e) { return false; }
    }

    public static boolean hasTransactions(String userEmail,
                                          LocalDate start, LocalDate end) {
        String sql = """
            SELECT 1 FROM transactions
            WHERE user_email=?
            AND date >= ? AND date <= ?
            """;
        try (Connection c = connect();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, userEmail);
            s.setString(2, start.atStartOfDay().toString());
            s.setString(3, end.atTime(23,59,59).toString());
            return s.executeQuery().next();
        } catch (SQLException e) { return false; }
    }
}
