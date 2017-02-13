package ua.softgroup.matrix.desktop.controllerjavafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;

/**
 * Created by AndriiBei on 09.02.2017.
 */
public class LoginLayoutController {

    @FXML
    public Button btnLogin;

    public void startMainWindow(ActionEvent actionEvent) {
       ((Node) actionEvent.getSource()).getScene().getWindow().hide();
        new MainLayoutController().startMainControllerLayout();
    }
}
