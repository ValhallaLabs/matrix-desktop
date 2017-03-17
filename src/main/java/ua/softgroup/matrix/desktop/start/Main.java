package ua.softgroup.matrix.desktop.start;

import it.sauronsoftware.junique.AlreadyLockedException;
import it.sauronsoftware.junique.JUnique;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.softgroup.matrix.desktop.controllerjavafx.LoginLayoutController;
import ua.softgroup.matrix.desktop.utils.ConfigManager;

import java.io.*;
import java.net.ServerSocket;
import java.time.LocalDateTime;



public class Main extends Application {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final String LOGO = "/images/logoIcon.png";
    private static final String LOGIN_LAYOUT = "fxml/loginLayout.fxml";
    private static final String LOGIN_LAYOUT_TITLE = "SuperVisor";
    private ServerSocket socket;

    public static void main(String[] args) {
        String matrixLogo ="\n" +
                "            ___  ___        _          _         _____    _____     \n" +
                "            |  \\/  |       | |        (_)       / __  \\  |  _  |  _ \n" +
                "            | .  . |  __ _ | |_  _ __  _ __  __ `' / /'  | |/' | (_)\n" +
                "            | |\\/| | / _` || __|| '__|| |\\ \\/ /   / /    |  /| |    \n" +
                "            | |  | || (_| || |_ | |   | | >  <  ./ /___ _\\ |_/ /  _ \n" +
                "            \\_|  |_/ \\__,_| \\__||_|   |_|/_/\\_\\ \\_____/(_)\\___/  (_)\n" +
                "                                                                    " + "\n" +
                "      ________     _ _____ _______ _______    _____________ _____  ______\n" +
                "      |______|     ||_____]|______|_____/ \\  /   |  |______|     ||_____/\n" +
                "      ______||_____||      |______|    \\_  \\/  __|________||_____||    \\_\n" +
                "                                                                         " + "\n" +
                "                            Make Matrix Great Again!\n";

        logger.info(matrixLogo);



        logger.debug("Current time: {}", LocalDateTime.now());
        launch(args);
    }

    /**
     * Point of start Application
     * @param primaryStage get default Stage from Application class
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        checkIfRunning();
        ConfigManager.readConfig();
        startLoginLayout(primaryStage);
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
            logger.debug("Application is already running!");
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
            logger.debug("Error when start Login Window "+e);
        }
    }
}
