package src.main.java.com.bakery.controller;
public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label messageLabel;

    private UserService userService = new UserService();

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        User user = userService.loginUser(username, password);
        if (user != null) {
            if (user.getRole().equals("customer")) {
                // Lade Kundenansicht
            } else if (user.getRole().equals("employee")) {
                // Lade Mitarbeiteransicht
            }
        } else {
            messageLabel.setText("Ungültige Anmeldedaten");
        }
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        // Registrierungshandling
    }
}