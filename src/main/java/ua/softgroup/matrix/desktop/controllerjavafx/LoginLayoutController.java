package ua.softgroup.matrix.desktop.controllerjavafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ua.softgroup.matrix.desktop.sessionmanagers.AuthenticationSessionManager;
import ua.softgroup.matrix.desktop.start.Main;

import java.io.IOException;

/**
 * @author Andrii Bei <sg.andriy2@gmail.com>
 */

public class LoginLayoutController {
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
    public void initialize(){
        authenticationSessionManager = new AuthenticationSessionManager(this);
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

    public AuthenticationSessionManager getAuthenticationSessionManager() {
        return authenticationSessionManager;
    }
}
