/**
 * Controller responsible for user authentication.
 *
 * Handles:
 * - User registration
 * - User login
 */
package controller;

import data.Database;
import model.User;
public class AuthController {

    /**
     * Registers a new user after validating input data.
     *
     * @param name    user's name
     * @param email   user's email
     * @param pass    password
     * @param confirm confirmation password
     * @return status message ("SUCCESS" or error message)
     */
    public static String register(String name, String email,
                                  String pass, String confirm) {

        name  = name.trim();
        email = email.trim();

        if (name.isEmpty())                           return "Name required";
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) return "Invalid email";
        if (pass.length() < 6)                        return "Password must be at least 6 characters";
        if (!pass.equals(confirm))                    return "Passwords do not match";
        if (Database.emailExists(email))              return "Email already exists";

        return Database.saveUser(name, email, pass) ? "SUCCESS" : "Registration failed";
    }

    /**
     * Authenticates a user.
     *
     * @param email user's email
     * @param pass  user's password
     * @return User object if login successful, otherwise null
     */
    public static User login(String email, String pass) {
        return Database.findUser(email.trim(), pass);
    }
}