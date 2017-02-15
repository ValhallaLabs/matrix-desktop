package ua.softgroup.matrix.desktop.controllerjavafx;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ua.softgroup.matrix.desktop.sessionmanagers.AuthenticationSessionManager;


/**
 * @author Andrii Bei <sg.andriy2@gmail.com>
 */

public class LoginLayoutController {
    private static String EMPTY_FIElD = "Error: Please Fill All Field";
    private static String INVALID_LOGIN_PASSWORD = "Error: Wrong Login or Password";
    private Stage stage;
    private AuthenticationSessionManager authenticationSessionManager;
    @FXML
    public TextField loginTextField;
    @FXML
    public PasswordField passwordTextField;
    @FXML
    public Button btnLogin;
    @FXML
    public Label labelErrorMessage;

    @FXML
    public void initialize() {
        authenticationSessionManager = new AuthenticationSessionManager(this);
        addTextLimiter(loginTextField, 20);
        addTextLimiter(passwordTextField, 20);
    }

    public void setUpStage(Stage stage) {
        this.stage = stage;
        stage.setOnCloseRequest(event -> authenticationSessionManager.closeSession());
    }

    public void startMainWindow(ActionEvent actionEvent) {
        if (!textFieldNotEmpty(loginTextField) || !textFieldNotEmpty(passwordTextField)) {
            labelErrorMessage.setText(EMPTY_FIElD);
            return;
        }
        sendAuthDataToNotificationManager();
    }

    private void sendAuthDataToNotificationManager() {
        String login = loginTextField.getText();
        String password = passwordTextField.getText();
        authenticationSessionManager.sendUserAuthData(login, password);
    }

    public void errorLoginPassword() {
        labelErrorMessage.setText(INVALID_LOGIN_PASSWORD);
    }

    public void closeLoginLayoutAndStartMainLayout() {
        stage.close();
        new MainLayoutController().startMainControllerLayout();
    }

    public AuthenticationSessionManager getAuthenticationSessionManager() {
        return authenticationSessionManager;
    }

    public static void addTextLimiter(final TextField tf, final int maxLength) {
        tf.textProperty().addListener((ov, oldValue, newValue) -> {
            if (tf.getText().length() > maxLength) {
                String s = tf.getText().substring(0, maxLength);
                tf.setText(s);
            }
        });
    }

    public static boolean textFieldNotEmpty(TextField tf) {
        if (tf.getText() != null && !tf.getText().isEmpty()) {
            return true;
        }
        return false;
    }

}
