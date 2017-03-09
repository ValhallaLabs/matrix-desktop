package ua.softgroup.matrix.desktop.controllerjavafx;


import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.softgroup.matrix.desktop.sessionmanagers.AuthenticationServerSessionManager;

import java.io.IOException;
import java.util.Optional;


/**
 * @author Andrii Bei <sg.andriy2@gmail.com>
 */

public class LoginLayoutController {

    private static final Logger logger = LoggerFactory.getLogger(LoginLayoutController.class);
    private static final String EMPTY_FIElD = "Error: Please Fill All Field";
    private static final String INVALID_LOGIN_PASSWORD = "Error: Wrong Login or Password";
    private static final String LOGO = "/images/logoIcon.png";
    private static final String MAIN_LAYOUT = "fxml/mainLayout.fxml";
    private static final String ALERT_TITLE_TEXT = "Supervisor";
    private static final String ALERT_CONTENT_TEXT = "Target ip:port is Unreachable";
    private static final String ALERT_HEADER_TEXT = "NETWORK ERROR";
    private static final int MAIN_LAYOUT_MIN_WIDTH = 1200;
    private static final int MAIN_LAYOUT_MIN_HEIGHT = 800;
    private static final String SETTING_LAYOUT = "fxml/settingLayout.fxml";
    private static final int SETTING_LAYOUT_MIN_WIDTH = 500;
    private static final int SETTING_LAYOUT_MIN_HEIGHT = 250;
    private Stage stage;
    private AuthenticationServerSessionManager authenticationSessionManager;
    @FXML
    public TextField loginTextField;
    @FXML
    public PasswordField passwordTextField;
    @FXML
    public Button btnLogin;
    @FXML
    public Label labelErrorMessage;

    /**
     * After Load/Parsing fxml call this method
     * Create {@link AuthenticationServerSessionManager}
     */
    @FXML
    public void initialize() {
        initializeAuthenticationManager();
        maxInputTextLimiter(loginTextField, 20);
        maxInputTextLimiter(passwordTextField, 20);
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
        startMainControllerLayout();
    }

    /**
     * Tells {@link LoginLayoutController} to open main Window and send to project layout stage
     */
    private void startMainControllerLayout() {
        try {
            Stage mainStage = new Stage();
            Image icon = new Image(getClass().getResourceAsStream(LOGO));
            mainStage.getIcons().add(icon);
            ClassLoader classLoader = getClass().getClassLoader();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(classLoader.getResource(MAIN_LAYOUT));
            BorderPane mainLayout = loader.load();
            Scene scene = new Scene(mainLayout);
            mainStage.setScene(scene);
            mainStage.setMinWidth(MAIN_LAYOUT_MIN_WIDTH);
            mainStage.setMinHeight(MAIN_LAYOUT_MIN_HEIGHT);
            mainStage.setResizable(false);
            mainStage.setTitle("SuperVisor");
            mainStage.show();
            MainLayoutController mainController = loader.getController();
            mainController.startProjectsLayoutController(mainLayout);
        } catch (IOException e) {
            logger.debug("Error when start Main Layout " + e);
        }
    }

    /**
     * Limit of amount on entry text
     *
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
     *
     * @param tf TextField in what we input text
     * @return boolean
     */
    private static boolean checkTextFieldOnEmpty(TextField tf) {
        return tf.getText() != null && !tf.getText().isEmpty();
    }

    /**
     * Hears when user click on setting menus item and
     * tells {@link MainLayoutController} to open setting window
     *
     * @param event callback click on menu
     */
    public void openSettings(Event event) {
        openSettingsWindow();
    }

    public void openSettingsWindow(){
        try {
            Stage settingStage = new Stage();
            ClassLoader classLoader = getClass().getClassLoader();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(classLoader.getResource(SETTING_LAYOUT));
            Pane pane = loader.load();
            SettingLayoutController settingLayoutController = loader.getController();
            settingLayoutController.setLoginLayoutController(this);
            Scene scene = new Scene(pane);
            settingStage.setScene(scene);
            settingStage.setMinWidth(SETTING_LAYOUT_MIN_WIDTH);
            settingStage.setMinHeight(SETTING_LAYOUT_MIN_HEIGHT);
            settingStage.initModality(Modality.WINDOW_MODAL);
            settingStage.initOwner(btnLogin.getScene().getWindow());
            settingStage.setResizable(false);
            settingStage.show();
        } catch (IOException e) {
            logger.debug("Error when start Setting Window " + e);
        }

    }

    public void initializeAuthenticationManager() {
        authenticationSessionManager = new AuthenticationServerSessionManager(this);
    }
}
