package data;

import model.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Single class that handles ALL database operations.
 * Uses SQLite via the sqlite-jdbc driver.
 *
 * <p>This class represents the Data Access Layer in MVC architecture.</p>
 * <p>Each method corresponds to a single SQL operation.</p>
 */
public class Database {

    /**
     * SQLite database URL.
     */
    private static final String URL = "jdbc:sqlite:budgetapp.db";

    /**
     * Establishes connection to SQLite database.
     *
     * @return Connection object
     * @throws SQLException if database connection fails
     */
    private static Connection connect() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite driver not found");
        }
        return DriverManager.getConnection(URL);
    }

    // ─────────────────────────────────────────────────────────────
    // INIT DATABASE
    // ─────────────────────────────────────────────────────────────

    /**
     * Creates all required tables if they do not exist.
     */
    public static void init() {
        String[] tables = {

            /**
             * USERS TABLE
             */
            """
            CREATE TABLE IF NOT EXISTS users (
                name     TEXT NOT NULL,
                email    TEXT PRIMARY KEY,
                password TEXT NOT NULL
            )
            """,

            /**
             * TRANSACTIONS TABLE
             */
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

            /**
             * BUDGETS TABLE
             */
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

            /**
             * GOALS TABLE
             */
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

            /**
             * NOTIFICATIONS TABLE
             */
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

            for (String sql : tables) {
                stmt.execute(sql);
            }

        } catch (SQLException e) {
            System.err.println("DB init error: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────
    // USERS
    // ─────────────────────────────────────────────────────────────

    /**
     * Checks if email exists in database.
     *
     * @param email user email
     * @return true if exists
     */
    public static boolean emailExists(String email) {
        String sql = "SELECT 1 FROM users WHERE email = ?";

        try (Connection c = connect();
             PreparedStatement s = c.prepareStatement(sql)) {

            s.setString(1, email);
            return s.executeQuery().next();

        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Saves a new user.
     *
     * @param name user name
     * @param email user email
     * @param pass password
     * @return true if saved
     */
    public static boolean saveUser(String name, String email, String pass) {
        String sql = "INSERT INTO users (name, email, password) VALUES (?,?,?)";

        try (Connection c = connect();
             PreparedStatement s = c.prepareStatement(sql)) {

            s.setString(1, name);
            s.setString(2, email);
            s.setString(3, pass);
            s.executeUpdate();

            return true;

        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Authenticates user login.
     *
     * @param email user email
     * @param pass password
     * @return User object if valid else null
     */
    public static User findUser(String email, String pass) {
        String sql = "SELECT * FROM users WHERE email=? AND password=?";

        try (Connection c = connect();
             PreparedStatement s = c.prepareStatement(sql)) {

            s.setString(1, email);
            s.setString(2, pass);

            ResultSet rs = s.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password")
                );
            }

        } catch (SQLException e) {
            System.err.println("findUser error: " + e.getMessage());
        }

        return null;
    }

    // ─────────────────────────────────────────────────────────────
    // TRANSACTIONS
    // ─────────────────────────────────────────────────────────────

    /**
     * Inserts transaction into database.
     *
     * @param t transaction object
     * @return success status
     */
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
            s.setInt(4, t.getCategoryId());
            s.setString(5, t.getCategoryName());
            s.setString(6, t.getDescription());
            s.setString(7, t.getPaymentMethod());
            s.setString(8, t.getDate().toString());

            s.executeUpdate();
            return true;

        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Loads all transactions for a user.
     *
     * @param userEmail user email
     * @return list of transactions
     */
    public static List<Transaction> loadTransactions(String userEmail) {
        List<Transaction> list = new ArrayList<>();

        String sql = "SELECT * FROM transactions WHERE user_email=? ORDER BY id DESC";

        try (Connection c = connect();
             PreparedStatement s = c.prepareStatement(sql)) {

            s.setString(1, userEmail);
            ResultSet rs = s.executeQuery();

            while (rs.next()) {
                Transaction t = new Transaction(
                        rs.getInt("id"),
                        userEmail,
                        rs.getDouble("amount"),
                        TransactionType.valueOf(rs.getString("type")),
                        rs.getInt("category_id"),
                        rs.getString("category_name"),
                        rs.getString("description"),
                        rs.getString("payment_method")
                );

                t.setDate(LocalDateTime.parse(rs.getString("date")));
                list.add(t);
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return list;
    }

    // ─────────────────────────────────────────────────────────────
    // BUDGETS
    // ─────────────────────────────────────────────────────────────

    /**
     * Saves budget.
     */
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
            s.setInt(2, b.getCategoryId());
            s.setString(3, b.getCategoryName());
            s.setDouble(4, b.getLimitAmount());
            s.setDouble(5, b.getSpentAmount());
            s.setString(6, b.getStartDate().toString());
            s.setString(7, b.getEndDate().toString());
            s.setInt(8, b.getAlertThreshold());
            s.setString(9, b.getStatus().name());

            s.executeUpdate();
            return true;

        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Updates budget.
     */
    public static boolean updateBudget(Budget b) {
        String sql = "UPDATE budgets SET spent_amount=?, status=? WHERE id=?";

        try (Connection c = connect();
             PreparedStatement s = c.prepareStatement(sql)) {

            s.setDouble(1, b.getSpentAmount());
            s.setString(2, b.getStatus().name());
            s.setInt(3, b.getBudgetId());

            s.executeUpdate();
            return true;

        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Loads budgets for user.
     */
    public static List<Budget> loadBudgets(String userEmail, int month, int year) {
        List<Budget> list = new ArrayList<>();

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
                        rs.getInt("id"),
                        userEmail,
                        rs.getInt("category_id"),
                        rs.getString("category_name"),
                        rs.getDouble("limit_amount"),
                        LocalDate.parse(rs.getString("start_date")),
                        LocalDate.parse(rs.getString("end_date")),
                        rs.getInt("alert_threshold")
                );

                b.setSpentAmount(rs.getDouble("spent_amount"));
                b.setStatus(BudgetStatus.valueOf(rs.getString("status")));
                list.add(b);
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return list;
    }

    /**
     * Checks budget existence.
     */
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
            s.setInt(2, categoryId);
            s.setString(3, end.toString());
            s.setString(4, start.toString());

            return s.executeQuery().next();

        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Finds active budget.
     */
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
            s.setInt(2, categoryId);
            s.setString(3, today.toString());
            s.setString(4, today.toString());

            ResultSet rs = s.executeQuery();

            if (rs.next()) {
                Budget b = new Budget(
                        rs.getInt("id"),
                        userEmail,
                        rs.getInt("category_id"),
                        rs.getString("category_name"),
                        rs.getDouble("limit_amount"),
                        LocalDate.parse(rs.getString("start_date")),
                        LocalDate.parse(rs.getString("end_date")),
                        rs.getInt("alert_threshold")
                );

                b.setSpentAmount(rs.getDouble("spent_amount"));
                b.setStatus(BudgetStatus.valueOf(rs.getString("status")));
                return b;
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return null;
    }

    // ─────────────────────────────────────────────────────────────
    // GOALS
    // ─────────────────────────────────────────────────────────────

    /**
     * Saves financial goal.
     */
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
            return true;

        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Updates goal.
     */
    public static boolean updateGoal(FinancialGoal g) {

        String sql = "UPDATE goals SET current_amount=?, status=? WHERE id=?";

        try (Connection c = connect();
             PreparedStatement s = c.prepareStatement(sql)) {

            s.setDouble(1, g.getCurrentAmount());
            s.setString(2, g.getStatus().name());
            s.setInt(3, g.getGoalId());

            s.executeUpdate();
            return true;

        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Deletes goal.
     */
    public static boolean deleteGoal(int goalId) {

        String sql = "DELETE FROM goals WHERE id=?";

        try (Connection c = connect();
             PreparedStatement s = c.prepareStatement(sql)) {

            s.setInt(1, goalId);
            s.executeUpdate();
            return true;

        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Loads goals.
     */
    public static List<FinancialGoal> loadGoals(String userEmail) {

        List<FinancialGoal> list = new ArrayList<>();

        String sql = "SELECT * FROM goals WHERE user_email=? ORDER BY id DESC";

        try (Connection c = connect();
             PreparedStatement s = c.prepareStatement(sql)) {

            s.setString(1, userEmail);
            ResultSet rs = s.executeQuery();

            while (rs.next()) {

                FinancialGoal g = new FinancialGoal(
                        rs.getInt("id"),
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
            System.err.println(e.getMessage());
        }

        return list;
    }

    // ─────────────────────────────────────────────────────────────
    // NOTIFICATIONS
    // ─────────────────────────────────────────────────────────────

    /**
     * Saves notification.
     */
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
            s.setInt(4, n.isRead() ? 1 : 0);
            s.setString(5, n.getTimestamp().toString());

            s.executeUpdate();
            return true;

        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Loads latest unread notification.
     */
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
                return new Notification(
                        rs.getInt("id"),
                        userEmail,
                        Notification.NotificationType.valueOf(rs.getString("type")),
                        rs.getString("message")
                );
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return null;
    }

    /**
     * Marks notification as read.
     */
    public static boolean markNotificationRead(int id) {

        String sql = "UPDATE notifications SET is_read=1 WHERE id=?";

        try (Connection c = connect();
             PreparedStatement s = c.prepareStatement(sql)) {

            s.setInt(1, id);
            s.executeUpdate();
            return true;

        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Checks if user has transactions in date range.
     */
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

        } catch (SQLException e) {
            return false;
        }
    }
}
