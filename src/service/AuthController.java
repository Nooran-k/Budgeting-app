package service;

import model.User;

import java.sql.*;

public class AuthController {

    private static final String DB_URL = "jdbc:sqlite:budgetapp.db";

    // Runs once when the app starts — creates the table if it doesn't exist yet
    static {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    name     TEXT NOT NULL,
                    email    TEXT PRIMARY KEY,
                    password TEXT NOT NULL
                )
            """);

        } catch (SQLException e) {
            System.err.println("DB init error: " + e.getMessage());
        }
    }


    // ── REGISTER ─────────────────────────────────────────────────────────────

    public static String register(String name, String email,
                                  String pass, String confirm) {
        name  = name.trim();
        email = email.trim();

        if (name.isEmpty()) return "Name required";
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) return "Invalid email";
        if (pass.length() < 6) return "Password must be at least 6 characters";
        if (!pass.equals(confirm)) return "Passwords do not match";

        if (emailExists(email)) return "Email already exists";

        String sql = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, pass);
            stmt.executeUpdate();
            return "SUCCESS";

        } catch (SQLException e) {
            System.err.println("Register error: " + e.getMessage());
            return "Registration failed, try again";
        }
    }


    // ── LOGIN ─────────────────────────────────────────────────────────────────

    public static User login(String email, String pass) {
        email = email.trim();

        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, pass);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password")
                );
            }

        } catch (SQLException e) {
            System.err.println("Login error: " + e.getMessage());
        }

        return null;
    }


    // ── HELPERS ───────────────────────────────────────────────────────────────

    private static Connection connect() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite driver not found: " + e.getMessage());
        }
        return DriverManager.getConnection(DB_URL);
    }

    private static boolean emailExists(String email) {
        String sql = "SELECT 1 FROM users WHERE email = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            return stmt.executeQuery().next();

        } catch (SQLException e) {
            return false;
        }
    }
}