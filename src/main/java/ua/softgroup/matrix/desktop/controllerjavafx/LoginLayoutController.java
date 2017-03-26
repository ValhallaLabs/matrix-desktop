package ua.softgroup.matrix.desktop.controllerjavafx;


import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.softgroup.matrix.desktop.sessionmanagers.AuthenticationServerSessionManager;

import java.io.IOException;
import java.util.Optional;
import java.util.prefs.Preferences;


/**
 * @author Andrii Bei <sg.andriy2@gmail.com>
 */

public class LoginLayoutController {
    private static final Logger logger = LoggerFactory.getLogger(LoginLayoutController.class);
    private static final String EMPTY_FIElD = "Error: Please Fill All Field";
    private static final String INVALID_LOGIN_PASSWORD = "Error: Wrong Login or Password";
    private static final String LOGO = "/images/logoIcon.png";
    private static final String ALERT_TITLE_TEXT = "Supervisor";
    private static final String ALERT_CONTENT_TEXT = "Target ip:port is Unreachable";
    private static final String ALERT_HEADER_TEXT = "NETWORK ERROR";
    private static final int MAIN_LAYOUT_MIN_WIDTH = 1200;
    private static final int MAIN_LAYOUT_MIN_HEIGHT = 800;
    private static final String SETTING_LAYOUT = "fxml/settingLayout.fxml";
    private static final String SETTING_LAYOUT_TITLE ="Settings";
    private static final String PROJECT_LAYOUT = "fxml/projectsLayout.fxml";
    private static final String PROJECT_LAYOUT_TITLE = "SuperVisor";
    private static final int SETTING_LAYOUT_MIN_WIDTH = 500;
    private static final int SETTING_LAYOUT_MIN_HEIGHT = 250;
    private Stage stage;
    private AuthenticationServerSessionManager authenticationSessionManager;
    private Preferences preferences;
    private final static String USER_NAME = "userName";
    private final static String USER_PASSWORD = "password";
    private final static String USER_SWITCH_SETTINGS = "false";
    @FXML
    public TextField loginTextField;
    @FXML
    public PasswordField passwordTextField;
    @FXML
    public Button btnLogin;
    @FXML
    public Label labelErrorMessage;
    @FXML
    public CheckBox cbRememberMe;
    @FXML
    public VBox vboxLoginWindow;
    @FXML
    public ProgressIndicator progIndWaitConnection;

    /**
     * After Load/Parsing fxml call this method
     */
    @FXML
    public void initialize() {
        preferences = Preferences.userRoot().node(this.getClass().getName());
        getPreferencesAndSetLoginPassword();
        initializeAuthenticationManager();
        maxInputTextLimiter(loginTextField, 20);
        maxInputTextLimiter(passwordTextField, 20);
        loginTextField.requestFocus();

    }

    /**
     * Set possibility click on VBox panel and dismiss ProgressIndicator
     */
    public void unlockLoginWindowAfterConnect(){
        vboxLoginWindow.setDisable(false);
        progIndWaitConnection.setVisible(false);
        progIndWaitConnection.setDisable(true);
        cbRememberMe.setDisable(false);
    }

    /**
     * Set impossibility click on VBox panel and show ProgressIndicator
     */
    private void showProgressIndicator(){
        vboxLoginWindow.setDisable(true);
        progIndWaitConnection.setVisible(true);
        progIndWaitConnection.setDisable(false);
        cbRememberMe.setDisable(true);
    }

    /**
     * If preferences saved, set automatically user login and password in special field
     */
    private void getPreferencesAndSetLoginPassword() {
        if (preferences != null) {
            loginTextField.setText(preferences.get(USER_NAME, ""));
            passwordTextField.setText(preferences.get(USER_PASSWORD, ""));
            cbRememberMe.setSelected(preferences.getBoolean(USER_SWITCH_SETTINGS, true));
        }
    }

    /**
     * Check state checkbox, and if he is Selected put user login and password to preferences,
     * else set empty field
     */
    private void saveLoginAndPasswordToPreferencesManager() {
        if (cbRememberMe.isSelected()) {
            preferences.put(USER_NAME, loginTextField.getText());
            preferences.put(USER_PASSWORD, passwordTextField.getText());
            preferences.putBoolean(USER_SWITCH_SETTINGS, true);
        } else {
            preferences.put(USER_NAME, "");
            preferences.put(USER_PASSWORD, "");
            preferences.putBoolean(USER_SWITCH_SETTINGS, false);
        }

    }

    /**
     * If when start programme bad connection, create alert with message to user
     * and then click on button close programme
     */
    public void tellUserAboutBadConnection() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(ALERT_TITLE_TEXT);
        alert.setHeaderText(ALERT_HEADER_TEXT);
        alert.setContentText(ALERT_CONTENT_TEXT);
        alert.initStyle(StageStyle.UTILITY);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            openSettingsWindow();
        }
    }

    /**
     * Hears when login window close and close current authentication session manager
     *
     * @param stage for close stage
     */
    public void setUpStage(Stage stage) {
        this.stage = stage;
        stage.setOnCloseRequest(event -> authenticationSessionManager.closeSession());
    }

    /**
     * Hears when user click on button and check input field if something go wrong
     * displays message
     *
     * @param actionEvent callback click on button
     */
    public void startMainWindow(ActionEvent actionEvent) {
        if (!checkTextFieldOnEmpty(loginTextField) || !checkTextFieldOnEmpty(passwordTextField)) {
            labelErrorMessage.setText(EMPTY_FIElD);
            return;
        }
        sendAuthDataToNotificationManager();
    }

    /**
     * Get String field from login and password and set this value in
     * method  of {@link AuthenticationServerSessionManager }
     */
    private void sendAuthDataToNotificationManager() {
        String login = loginTextField.getText();
        String password = passwordTextField.getText();
        authenticationSessionManager.sendUserAuthData(login, password);
    }

    /**
     * If user input invalid login or password
     * call this method in {@link AuthenticationServerSessionManager}
     * and set information on login window
     */
    public void errorLoginPassword() {
        labelErrorMessage.setText(INVALID_LOGIN_PASSWORD);
    }

    /**
     * Close current stage and prepare for start main window
     */
    public void closeLoginLayoutAndStartMainLayout() {
        stage.close();
        startProjectsControllerLayout();
        saveLoginAndPasswordToPreferencesManager();
    }

    /**
     * Tells {@link LoginLayoutController} to open project window
     */
    private void startProjectsControllerLayout() {
        try {
            Stage projectsStage = new Stage();
            Image icon = new Image(getClass().getResourceAsStream(LOGO));
            projectsStage.getIcons().add(icon);
            ClassLoader classLoader = getClass().getClassLoader();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(classLoader.getResource(PROJECT_LAYOUT));
            AnchorPane projectsLayout = loader.load();
            Scene scene = new Scene(projectsLayout);
            projectsStage.setScene(scene);
            ProjectsLayoutController projectsLayoutController=loader.getController();
            projectsLayoutController.setUpStage(projectsStage);
            projectsStage.setMinWidth(MAIN_LAYOUT_MIN_WIDTH);
            projectsStage.setMinHeight(MAIN_LAYOUT_MIN_HEIGHT);
            projectsStage.setResizable(false);
            projectsStage.setTitle(PROJECT_LAYOUT_TITLE);
            projectsStage.show();
        } catch (IOException e) {
            logger.error("Error when start Main Layout ", e);
        }
    }

    /**
     * Limit of amount on entry text
     * @param tf        TextField in what input text
     * @param maxLength int number of max text amount
     */
    private static void maxInputTextLimiter(final TextField tf, final int maxLength) {
        tf.textProperty().addListener((ov, oldValue, newValue) -> {
            if (tf.getText().length() > maxLength) {
                String s = tf.getText().substring(0, maxLength);
                tf.setText(s);
            }
        });
    }

    /**
     * Check content in field on Empty
     * @param tf TextField in what we input text
     * @return boolean
     */
    private static boolean checkTextFieldOnEmpty(TextField tf) {
        return tf.getText() != null && !tf.getText().isEmpty();
    }

    /**
     * Hears when user click on setting menus
     * @param event callback click on menu
     */
    public void openSettings(Event event) {
        //TODO:fix bug of second settings window, if connection not found while settings window is already open
        //TODO: figure out what to do if connection wasn't found, and user just close setting window
        openSettingsWindow();
    }

    /**
     * Tell to{@link LoginLayoutController} open settings window
     */
    private void openSettingsWindow() {
        try {
            Stage settingStage = new Stage();
            ClassLoader classLoader = getClass().getClassLoader();
            Image icon = new Image(getClass().getResourceAsStream(LOGO));
            settingStage.getIcons().add(icon);
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(classLoader.getResource(SETTING_LAYOUT));
            Pane pane = loader.load();
            SettingLayoutController settingLayoutController = loader.getController();
            settingLayoutController.setLoginLayoutController(this);
            Scene scene = new Scene(pane);
            settingStage.setScene(scene);
            settingStage.setMinWidth(SETTING_LAYOUT_MIN_WIDTH);
            settingStage.setMinHeight(SETTING_LAYOUT_MIN_HEIGHT);
            settingStage.setTitle(SETTING_LAYOUT_TITLE);
            settingStage.initModality(Modality.WINDOW_MODAL);
            settingStage.initOwner(btnLogin.getScene().getWindow());
            settingStage.setResizable(false);
            settingStage.show();
        } catch (IOException e) {
            logger.error("Error when start Settings Window ", e);
        }
    }

    /**
     * Create {@link AuthenticationServerSessionManager}
     */
     void initializeAuthenticationManager() {
         showProgressIndicator();
         if (authenticationSessionManager != null) {
            authenticationSessionManager.closeSession();
         }
         authenticationSessionManager = new AuthenticationServerSessionManager(this);
    }
}