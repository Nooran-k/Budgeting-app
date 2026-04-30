package service;

import model.User;
import java.util.ArrayList;

public class AuthController {

    static ArrayList<User> users = new ArrayList<>();

    public static String register(String name, String email, String pass, String confirm) {

        if(name.isEmpty()) return "Name required";
        if(!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) return "Invalid email";

        for(User u : users)
            if(u.getEmail().equals(email)) return "Email exists";

        if(pass.length() < 6)
            return "Password must be at least 6 characters";

        if(!pass.equals(confirm)) return "Passwords mismatch";

        users.add(new User(name,email,pass));
        return "SUCCESS";
    }

    public static User login(String email, String pass) {
        for(User u : users)
            if(u.getEmail().equals(email) && u.getPassword().equals(pass))
                return u;
        return null;
    }
}