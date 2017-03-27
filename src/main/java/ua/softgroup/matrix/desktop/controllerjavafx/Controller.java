package ua.softgroup.matrix.desktop.controllerjavafx;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputControl;
import javafx.stage.StageStyle;


/**
 * @author Andrii Bei <sg.andriy2@gmail.com>
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
abstract public class Controller {
    private static final String ALERT_ERROR_TITLE = "Supervisor";
    private static final String ALERT_SOMETHING_WENT_WRONG = "Something went wrong .Programs will be close";
    private static final String ALERT_ACCESS_DENIED = "Someone accessed through your name. Please, re-login";
    private static final String ALERT_XDOTOOL_NOT_FOUND = "Sorry, but xdotool not found. Please, install it.";
    private static final String ALERT_HEADER_TEXT = "Supervisor ERROR";

    /**
     * When something go wrong , create alert with message to user
     * and then click on button close programme
     */
    public void tellUserAboutCrash() {
        showUserAnAlert(ALERT_SOMETHING_WENT_WRONG);
    }

    /**
     * When something go wrong , create alert with message to user
     * and then click on button close programme
     */
    public void tellUserAboutAccessDenied() {
        showUserAnAlert(ALERT_ACCESS_DENIED);
    }

    public void tellUserAboutXdotoolNotFound() {
        showUserAnAlert(ALERT_XDOTOOL_NOT_FOUND);
    }

    private void showUserAnAlert(String alertText) {
        Alert mainAlert = new Alert(Alert.AlertType.INFORMATION);
        mainAlert.setTitle(ALERT_ERROR_TITLE);
        mainAlert.setHeaderText(ALERT_HEADER_TEXT);
        mainAlert.setContentText(alertText);
        mainAlert.initStyle(StageStyle.UTILITY);
        mainAlert.setOnCloseRequest(event -> Platform.exit());
        mainAlert.showAndWait();
        Platform.exit();
    }

    /**
     * Convert Seconds to Hours and Minutes format
     *
     * @param seconds current time what we want convert
     * @return String of Hours and Minutes
     */
    String convertFromSecondsToHoursAndMinutes(int seconds) {
        int todayTimeInHours = seconds / 3600;
        int todayTimeInMinutes = (seconds % 3600) / 60;
        return String.valueOf(todayTimeInHours + "h " + todayTimeInMinutes + 'm');
    }

    /**
     * Limit of amount on entry text
     *
     * @param ta        TextInputField in what input text
     * @param maxLength int number of max text amount
     */
      void addTextLimiter(final TextInputControl ta, final int maxLength) {
        ta.textProperty().addListener((ov, oldValue, newValue) -> {
            if (ta.getText().length() > maxLength) {
                String s = ta.getText().substring(0, maxLength);
                ta.setText(s);
            }
        });
    }
}
