package ua.softgroup.matrix.desktop.controllerjavafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ua.softgroup.matrix.desktop.sessionmanagers.AuthenticationServerSessionManager;

/**
 * @author Andrii Bei <sg.andriy2@gmail.com>
 */

public class LoginLayoutController {
    private Stage stage;
    private AuthenticationServerSessionManager authenticationSessionManager;
    @FXML
    public TextField loginTextField;
    @FXML
    public PasswordField passwordTextField;
    @FXML
    public Button btnLogin;
    @FXML
    public Label labelErrorMessage;

    @FXML
    public void initialize(){
        authenticationSessionManager = new AuthenticationServerSessionManager(this);
    }

    public void setUpStage(Stage stage) {
        this.stage = stage;
        stage.setOnCloseRequest(event -> authenticationSessionManager.closeSession());
    }

    public void startMainWindow(ActionEvent actionEvent) {
       sendAuthDataToNotificationManager();
    }

    private void sendAuthDataToNotificationManager() {
        String login = loginTextField.getText();
        String password = passwordTextField.getText();
        authenticationSessionManager.sendUserAuthData(login, password);
    }

    public void errorLoginPassword() {
        labelErrorMessage.setVisible(true);
    }

    public void closeLoginLayoutAndStartMainLayout() {
        stage.close();
        new MainLayoutController().startMainControllerLayout();
    }

    public AuthenticationServerSessionManager getAuthenticationSessionManager() {
        return authenticationSessionManager;
    }
}
