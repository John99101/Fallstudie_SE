package model;

public class User {
    private int id;
    private String username;
    private String password;
    private String email;
    private String name;
    private boolean isEmployee;
    private boolean isCompany;

    // Default Constructor
    public User() {
    }

    // Parameterized Constructor
    public User(int id, String username, String password, String email, String name, boolean isEmployee) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.name = name;
        this.isEmployee = isEmployee;
        this.isCompany = false;
    }

    // Getters
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public boolean isEmployee() { return isEmployee; }
    public boolean isCompany() { return isCompany; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setEmail(String email) { this.email = email; }
    public void setName(String name) { this.name = name; }
    public void setEmployee(boolean employee) { isEmployee = employee; }
    public void setCompany(boolean company) { isCompany = company; }

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
