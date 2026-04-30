package model;

public class User {
    private String name;
    private String email;
    private String password;

    public User(String n, String e, String p) {
        name = n;
        email = e;
        password = p;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}