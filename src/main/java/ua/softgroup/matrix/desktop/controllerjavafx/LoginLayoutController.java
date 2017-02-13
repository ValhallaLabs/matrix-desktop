package ua.softgroup.matrix.desktop.controllerjavafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import ua.softgroup.matrix.desktop.sessionmanagers.AuthenticationSessionManager;

/**
 * Created by AndriiBei on 09.02.2017.
 */
/** TODO For Andrii Bei:
 * 1) Read user auth data from login window and send it to authenticationSessionManager
 * through the void sendUserAuthData(String userNameString, String userPasswordString) method.
 * 2) Create public method that will inform user that auth data is incorrect(method will be used
 * by authenticationSessionManager).
 * 3) Create public method that will open main window(method will be used by authenticationSessionManager)
 */
public class LoginLayoutController {
    private AuthenticationSessionManager authenticationSessionManager;

    @FXML
    public Button btnLogin;

    public void startMainWindow(ActionEvent actionEvent) {
       ((Node) actionEvent.getSource()).getScene().getWindow().hide();
        new MainLayoutController().startMainControllerLayout();
    }
}
