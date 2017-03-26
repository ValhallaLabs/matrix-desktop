package ua.softgroup.matrix.desktop.controllerjavafx;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.StageStyle;

import java.util.Optional;

/**
 * Created by vb on 25/03/17.
 */
abstract public class Controller {
    private static final String ALERT_ERROR_TITLE = "Supervisor";
    private static final String ALERT_CONTENT_TEXT = "Something go wrong .Programs will be close";
    private static final String ALERT_ACCESS_DENIED_TEXT = "Someone accessed through your name. Please, re-login";
    private static final String ALERT_HEADER_TEXT = "Supervisor ERROR";

    /**
     * When something go wrong , create alert with message to user
     * and then click on button close programme
     */
    public void tellUserAboutCrash() {
        Alert mainAlert = new Alert(Alert.AlertType.INFORMATION);
        mainAlert.setTitle(ALERT_ERROR_TITLE);
        mainAlert.setHeaderText(ALERT_HEADER_TEXT);
        mainAlert.setContentText(ALERT_CONTENT_TEXT);
        mainAlert.initStyle(StageStyle.UTILITY);
        mainAlert.setOnCloseRequest(event -> Platform.exit());
        Optional<ButtonType> result = mainAlert.showAndWait();
//        if (result.isPresent() && result.get() == ButtonType.OK) {
            Platform.exit();
//        }
    }

    /**
     * When something go wrong , create alert with message to user
     * and then click on button close programme
     */
    public void tellUserAboutAccessDenied() {
        Alert mainAlert = new Alert(Alert.AlertType.INFORMATION);
        mainAlert.setTitle(ALERT_ERROR_TITLE);
        mainAlert.setHeaderText(ALERT_HEADER_TEXT);
        mainAlert.setContentText(ALERT_ACCESS_DENIED_TEXT);
        mainAlert.initStyle(StageStyle.UTILITY);
        mainAlert.setOnCloseRequest(event -> Platform.exit());
        Optional<ButtonType> result = mainAlert.showAndWait();
//        if (result.isPresent() && result.get() == ButtonType.OK) {
            Platform.exit();
//        }
    }


}
