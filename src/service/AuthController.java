package service;

import data.Database;
import model.User;

public class AuthController {

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

    public static User login(String email, String pass) {
        return Database.findUser(email.trim(), pass);
    }
}