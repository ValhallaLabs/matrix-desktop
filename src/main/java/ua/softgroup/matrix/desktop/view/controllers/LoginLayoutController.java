package ua.softgroup.matrix.desktop.view.controllers;


import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.softgroup.matrix.desktop.session.manager.AuthenticationServerSessionManager;

import java.io.IOException;
import java.util.Optional;
import java.util.prefs.Preferences;


/**
 * @author Andrii Bei <sg.andriy2@gmail.com>
 */

public class LoginLayoutController extends Controller {
    private static final Logger logger = LoggerFactory.getLogger(LoginLayoutController.class);
    private static final String EMPTY_FIElD = "Error: Please Fill All Field";
    private static final String INVALID_LOGIN_PASSWORD = "Error: Wrong Login or Password";
    private static final String LOGO = "/images/logoIcon.png";
    private static final String ALERT_TITLE_TEXT = "Supervisor";
    private static final String ALERT_CONTENT_TEXT = "Target ip:port is Unreachable";
    private static final String ALERT_HEADER_TEXT = "NETWORK ERROR";
    private static final String SETTING_LAYOUT = "fxml/settingLayout.fxml";
    private static final String SETTING_LAYOUT_TITLE ="Settings";
    private static final String PROJECT_LAYOUT = "fxml/projectsLayout.fxml";
    private static final String USER_NAME = "userName";
    private static final String USER_PASSWORD = "password";
    private static final String USER_SWITCH_SETTINGS = "false";
    private static final int MAIN_LAYOUT_MIN_WIDTH = 1200;
    private static final int MAIN_LAYOUT_MIN_HEIGHT = 800;
    private static final int SETTING_LAYOUT_MIN_WIDTH = 500;
    private static final int SETTING_LAYOUT_MIN_HEIGHT = 250;
    private Stage loginStage;
    private AuthenticationServerSessionManager authenticationSessionManager;
    private Preferences preferences;
    private Stage settingStage;
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
    public VBox vBoxLoginWindow;
    @FXML
    public ProgressIndicator progressIndWaitConnection;

    /**
     * After Load/Parsing fxml call this method
     */
    @FXML
    public void initialize() {
        preferences = Preferences.userRoot().node(this.getClass().getName());
        getPreferencesAndSetLoginPassword();
        initializeAuthenticationManager();
        setTextLimiterOnField();
        loginTextField.requestFocus();

    }

    /**
     * Hears when user click on setting menus
     * @param event callback click on menu
     */
    public void openSettings(Event event) {
        settingStage = new Stage();
        if(!settingStage.isShowing()){
            openSettingsWindow();
        }
    }

    /**
     * Set possibility click on VBox panel and dismiss ProgressIndicator
     */
    public void unlockLoginWindowAfterConnect(){
        vBoxLoginWindow.setDisable(false);
        progressIndWaitConnection.setVisible(false);
        progressIndWaitConnection.setDisable(true);
        cbRememberMe.setDisable(false);
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
            settingStage = new Stage();
            openSettingsWindow();
        }
    }

    /**
     * Hears when login window close and close current authentication session manager
     *
     * @param stage for close loginStage
     * @param scene
     */
    public void setUpStage(Stage stage, Scene scene) {
        scene.setOnKeyPressed(event -> {
            if(event.getCode()== KeyCode.ENTER){
              Platform.runLater(() -> checkLoginAndStartLayout());
            }
        });
        this.loginStage = stage;
        stage.setOnCloseRequest(event -> authenticationSessionManager.closeSession());
    }

    /**
     * Hears when user click on button and check input field if something go wrong
     * displays message
     *
     * @param actionEvent callback click on button
     */
    public void startLoginWindow(ActionEvent actionEvent) {
        checkLoginAndStartLayout();
    }

    private void checkLoginAndStartLayout(){
        if (!checkTextFieldOnEmpty(loginTextField) || !checkTextFieldOnEmpty(passwordTextField)) {
            labelErrorMessage.setText(EMPTY_FIElD);
            return;
        }
        sendAuthDataToNotificationManager();
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
     * Close current loginStage and prepare for start main window
     */
    public void closeLoginLayoutAndStartMainLayout() {
        loginStage.close();
        startProjectsControllerLayout();
        saveLoginAndPasswordToPreferencesManager();
    }

    private void setTextLimiterOnField() {
        addTextLimiter(loginTextField, 20);
        addTextLimiter(passwordTextField, 20);
    }

    /**
     * Set impossibility click on VBox panel and show ProgressIndicator
     */
    private void showProgressIndicator(){
        vBoxLoginWindow.setDisable(true);
        progressIndWaitConnection.setVisible(true);
        progressIndWaitConnection.setDisable(false);
        cbRememberMe.setDisable(true);
    }

    void stopProgressIndicator(){
        vBoxLoginWindow.setDisable(true);
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
     * Get String field from login and password and set this value in
     * method  of {@link AuthenticationServerSessionManager }
     */
    private void sendAuthDataToNotificationManager() {
        String login = loginTextField.getText();
        String password = passwordTextField.getText();
        authenticationSessionManager.sendUserAuthData(login, password);
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
//            projectsStage.setTitle(PROJECT_LAYOUT_TITLE);
            projectsStage.show();
        } catch (IOException e) {
            logger.error("Error when start Main Layout ", e);
        }
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
     * Tell to{@link LoginLayoutController} open settings window
     */
    private void openSettingsWindow() {
        try {
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
            if(settingStage.getModality()!=Modality.WINDOW_MODAL){
                settingStage.initModality(Modality.WINDOW_MODAL);
                settingStage.initOwner(btnLogin.getScene().getWindow());
            }
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

    AuthenticationServerSessionManager getAuthenticationSessionManager() {
        return authenticationSessionManager;
    }

}