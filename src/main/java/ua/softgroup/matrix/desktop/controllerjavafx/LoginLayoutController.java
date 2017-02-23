package ua.softgroup.matrix.desktop.controllerjavafx;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.softgroup.matrix.desktop.sessionmanagers.AuthenticationServerSessionManager;


import java.io.IOException;


/**
 * @author Andrii Bei <sg.andriy2@gmail.com>
 */

public class LoginLayoutController {

    private static final Logger logger = LoggerFactory.getLogger(LoginLayoutController.class);
    private static final String EMPTY_FIElD = "Error: Please Fill All Field";
    private static final String INVALID_LOGIN_PASSWORD = "Error: Wrong Login or Password";
    private static final String LOGO = "/images/testLogoIcon.png";
    private static final String MAIN_LAYOUT = "fxml/mainLayout.fxml";
    private static final int MAIN_LAYOUT_MIN_WIDTH = 1200;
    private static final int MAIN_LAYOUT_MIN_HEIGHT = 800;
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

    @FXML
    public void initialize() {
        authenticationSessionManager = new AuthenticationServerSessionManager(this);
        addTextLimiter(loginTextField, 20);
        addTextLimiter(passwordTextField, 20);
    }

    public void setUpStage(Stage stage) {
        this.stage = stage;
        stage.setOnCloseRequest(event -> authenticationSessionManager.closeSession());
    }

    public void startMainWindow(ActionEvent actionEvent) {
        if (!textFieldNotEmpty(loginTextField) || !textFieldNotEmpty(passwordTextField)) {
            labelErrorMessage.setText(EMPTY_FIElD);
            return;
        }
        sendAuthDataToNotificationManager();
    }

    private void sendAuthDataToNotificationManager() {
        String login = loginTextField.getText();
        String password = passwordTextField.getText();
        authenticationSessionManager.sendUserAuthData(login, password);
    }

    public void errorLoginPassword() {
        labelErrorMessage.setText(INVALID_LOGIN_PASSWORD);
    }

    public void closeLoginLayoutAndStartMainLayout() {
        stage.close();
        startMainControllerLayout();
    }

    private void startMainControllerLayout() {
        try {
            Stage primaryStage = new Stage();
            Image icon = new Image(getClass().getResourceAsStream(LOGO));
            primaryStage.getIcons().add(icon);
            ClassLoader classLoader = getClass().getClassLoader();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(classLoader.getResource(MAIN_LAYOUT));
            BorderPane mainLayout = loader.load();
            Scene scene = new Scene(mainLayout);
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(MAIN_LAYOUT_MIN_WIDTH);
            primaryStage.setMinHeight(MAIN_LAYOUT_MIN_HEIGHT);
            primaryStage.setResizable(false);
            primaryStage.show();
            MainLayoutController mainController = loader.getController();
            mainController.startProjectsLayoutController(mainLayout);
        } catch (IOException e) {
            logger.debug("Error when start Main Layout");
            e.printStackTrace();
        }
    }

    public AuthenticationServerSessionManager getAuthenticationSessionManager() {
        return authenticationSessionManager;
    }

    public static void addTextLimiter(final TextField tf, final int maxLength) {
        tf.textProperty().addListener((ov, oldValue, newValue) -> {
            if (tf.getText().length() > maxLength) {
                String s = tf.getText().substring(0, maxLength);
                tf.setText(s);
            }
        });
    }

    public static boolean textFieldNotEmpty(TextField tf) {
        if (tf.getText() != null && !tf.getText().isEmpty()) {
            return true;
        }
        return false;
    }

}
