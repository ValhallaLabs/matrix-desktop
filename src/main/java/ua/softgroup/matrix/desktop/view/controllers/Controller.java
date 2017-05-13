package ua.softgroup.matrix.desktop.view.controllers;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputControl;
import javafx.stage.StageStyle;
import ua.softgroup.matrix.desktop.view.UTF8Control;

import java.util.Locale;
import java.util.ResourceBundle;


/**
 * @author Andrii Bei <sg.andriy2@gmail.com>
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
abstract public class Controller {
    private static final String ALERT_ERROR_TITLE = "SG Tracker";
    private static final String ALERT_SOMETHING_WENT_WRONG = "Something went wrong .Programs will be close";
    private static final String ALERT_ACCESS_DENIED = "Someone accessed through your name.\n" + "Please, re-login";
    private static final String ALERT_XDOTOOL_NOT_FOUND = "Sorry, xdotool not found. Please, install it just in one step:\n" +
            "apt-get install xdotool\n" +
            "Read more: http://www.semicomplete.com/projects/xdotool";
    private static final String ALERT_HEADER_TEXT = "Supervisor ERROR";
    private static final String HOURS_SYMBOL = "h ";
    private static final char MINUTES_SYMBOL = 'm';
    private static final int SECONDS_IN_HOURS = 3600;
    private static final int MINUTES_IN_HOURS = 60;

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

    ResourceBundle setResourceBundle(ClassLoader classLoader){
        ResourceBundle bundle = new UTF8Control().newBundle(new Locale("uk"),classLoader);
        return  bundle;
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
        System.exit(0);
    }
}
