package ua.softgroup.matrix.desktop;

import it.sauronsoftware.junique.AlreadyLockedException;
import it.sauronsoftware.junique.JUnique;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.softgroup.matrix.desktop.view.controllers.LoginLayoutController;
import ua.softgroup.matrix.desktop.utils.ConfigManager;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author Andrii Bei <sg.andriy2@gmail.com>
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
public class Main extends Application {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final String LOGO = "/images/logoIcon.png";
    private static final String LOGIN_LAYOUT = "fxml/loginLayout.fxml";
    private static final String LOGIN_LAYOUT_TITLE = "SuperVisor";
    private static final String ALERT_ERROR_TITLE = "Supervisor";
    private static final String ALERT_CONTENT_TEXT = "Something go wrong .Programs will be close";
    private static final String ALERT_HEADER_TEXT = "Supervisor ERROR";

    public static void main(String[] args) {
        logger.debug("Current time: {}", LocalDateTime.now());
        launch(args);
    }

    /**
     * Point of start Application
     * @param primaryStage get default Stage from Application class
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            checkIfRunning();
            ConfigManager.readConfig();
            startLoginLayout(primaryStage);
        } catch (ConfigManager.ConfigCrashException e) {
            logger.error("Config wasn't set to default", e);
            tellUserAboutConfigCrash();
        }
    }

    /**
     * Method tries to lock application to avoid multiply opening possibility by JUnique library
     * In case if application is already running, client simply won't start.
     */
    private void checkIfRunning() {
        String appId = "Make Matrix great again!";
        try {
            JUnique.acquireLock(appId);
        } catch (AlreadyLockedException e) {
            logger.warn("Application is already running!");
            System.exit(0);
        }
    }

    /**
     * Tells {@link Main} to open login window
     * @param loginStage for create stage
     */
    private void startLoginLayout(Stage loginStage) {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            FXMLLoader loader = new FXMLLoader();
            Image icon = new Image(getClass().getResourceAsStream(LOGO));
            loginStage.getIcons().add(icon);
            loader.setLocation(classLoader.getResource(LOGIN_LAYOUT));
            Pane loginLayout = loader.load();
             LoginLayoutController loginLayoutController =loader.getController();
            loginLayoutController.setUpStage(loginStage);
            Scene scene = new Scene(loginLayout);
            loginStage.setScene(scene);
            loginStage.setTitle(LOGIN_LAYOUT_TITLE);
            loginStage.setResizable(false);
            loginStage.show();
        } catch (IOException e) {
            logger.error("Error when start Login Window ", e);
        }
    }

    public void tellUserAboutConfigCrash() {
        Alert mainAlert = new Alert(Alert.AlertType.INFORMATION);
        mainAlert.setTitle(ALERT_ERROR_TITLE);
        mainAlert.setHeaderText(ALERT_HEADER_TEXT);
        mainAlert.setContentText(ALERT_CONTENT_TEXT);
        mainAlert.initStyle(StageStyle.UTILITY);
        mainAlert.setOnCloseRequest(event -> Platform.exit());
        Optional<ButtonType> result = mainAlert.showAndWait();
            Platform.exit();
    }
}
