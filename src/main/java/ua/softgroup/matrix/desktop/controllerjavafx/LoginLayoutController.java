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

import java.io.IOException;

/**
 * @author Andrii Bei <sg.andriy2@gmail.com>
 */

public class LoginLayoutController {
    @FXML
    public TextField loginTextField;
    @FXML
    public PasswordField passwordTextField;
    @FXML
    public Button btnLogin;
    @FXML
    public Label labelErrorMessage;

    private AuthenticationSessionManager authenticationSessionManager;

    public void startLoginLayout(Stage loginStage) {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(classLoader.getResource("fxml/loginLayout.fxml"));
            Pane loginLayout = loader.load();
            Scene scene = new Scene(loginLayout);
            loginStage.setScene(scene);
            loginStage.initStyle(StageStyle.UTILITY);
            loginStage.setResizable(false);
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startMainWindow(ActionEvent actionEvent) {
        sendAuthDataToNotificationManager();
    }

    private void sendAuthDataToNotificationManager() {
        String login = loginTextField.getText().toString();
        String password = passwordTextField.getText().toString();
        authenticationSessionManager = new AuthenticationSessionManager(LoginLayoutController.this);
        authenticationSessionManager.sendUserAuthData(login, password);
    }

    public void errorLoginPassword() {
        labelErrorMessage.setVisible(true);
    }

    public void closeLoginLayoutAndStartMainLayout() {
        closeLoginlayout();
        new MainLayoutController().startMainControllerLayout();
    }

    private void closeLoginlayout() {
        Stage stage = (Stage) btnLogin.getScene().getWindow();
        stage.close();
    }
}
