package ua.softgroup.matrix.desktop.view.controllers;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputControl;
import javafx.stage.StageStyle;

import static ua.softgroup.matrix.desktop.Main.resourceBundle;


/**
 * @author Andrii Bei <sg.andriy2@gmail.com>
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
abstract public class Controller {
    private static final String HOURS_SYMBOL = "h ";
    private static final char MINUTES_SYMBOL = 'm';
    private static final int SECONDS_IN_HOURS = 3600;
    private static final int MINUTES_IN_HOURS = 60;
    static final String APP_TITLE = "SG Tracker";

    /**
     * When something go wrong , create alert with message to user
     * and then click on button close programme
     */
    public void tellUserAboutCrash() {
        showUserAnAlert(resourceBundle.getString("key.AlertSomethingWrong"));
//        Something went wrong .Programs will be close
    }

    /**
     * When something go wrong , create alert with message to user
     * and then click on button close programme
     */
    public void tellUserAboutAccessDenied() {
        showUserAnAlert(resourceBundle.getString("key.AlertAccessDenied"));
    }

    public void tellUserAboutXdotoolNotFound() {
        showUserAnAlert(resourceBundle.getString("key.AlertXDoTool"));
    }

    /**
     * Convert Seconds to Hours and Minutes format
     *
     * @param seconds current time what we want convert
     * @return String of Hours and Minutes
     */
    String convertFromSecondsToHoursAndMinutes(int seconds) {
        int todayTimeInHours = seconds / SECONDS_IN_HOURS;
        int todayTimeInMinutes = (seconds % SECONDS_IN_HOURS) / MINUTES_IN_HOURS;
        return String.valueOf(todayTimeInHours + HOURS_SYMBOL + todayTimeInMinutes + MINUTES_SYMBOL);
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

    private void showUserAnAlert(String alertText) {
        Alert mainAlert = new Alert(Alert.AlertType.INFORMATION);
        mainAlert.setTitle(APP_TITLE);
        mainAlert.setHeaderText(resourceBundle.getString("key.TrackerError"));
        mainAlert.setContentText(alertText);
        mainAlert.initStyle(StageStyle.UTILITY);
        mainAlert.setOnCloseRequest(event -> Platform.exit());
        mainAlert.showAndWait();
        Platform.exit();
        System.exit(0);
    }
}
