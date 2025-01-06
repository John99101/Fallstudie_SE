package model;

public class User {
    private int id;
    private String email;
    private String name;
    private String password;
    private boolean isEmployee;

    // Default Constructor
    public User() {
    }

    // Parameterized Constructor
    public User(int id, String email, String name, String password, boolean isEmployee) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.password = password;
        this.isEmployee = isEmployee;
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEmployee() {
        return isEmployee;
    }

    public void setEmployee(boolean employee) {
        isEmployee = employee;
    }

    // Overriding toString() for debugging or logging purposes
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", isEmployee=" + isEmployee +
                '}';
    }
}
