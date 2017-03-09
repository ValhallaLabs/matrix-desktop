package ua.softgroup.matrix.desktop.start;

import it.sauronsoftware.junique.AlreadyLockedException;
import it.sauronsoftware.junique.JUnique;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.softgroup.matrix.desktop.controllerjavafx.LoginLayoutController;
import ua.softgroup.matrix.desktop.utils.SocketProvider;

import java.io.*;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import static java.nio.file.StandardOpenOption.CREATE_NEW;


public class Main extends Application {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final String LOGO = "/images/logoIcon.png";
    private ServerSocket socket;

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
    public void start(Stage primaryStage) throws Exception {
        checkIfRunning();
        readConfig();
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
     * Method reads host and port values from config file and sets it to SocketProvider
     */
    private void readConfig() {
        try {
            checkConfigFile();
            CompositeConfiguration config = new CompositeConfiguration();
            config.addConfiguration(new PropertiesConfiguration("config.properties"));
            SocketProvider.setHostName(config.getString("host"));
            SocketProvider.setPortNumber(config.getInt("port"));
            logger.debug("Server IP: {}:{}", SocketProvider.getHostName(), SocketProvider.getPortNumber());
        } catch (Exception e) {
            logger.debug("Server IP is not found");
            //TODO: Show user alert to change IP & port, and automatically save the new one to config.properties
        }
    }

    private void checkConfigFile() throws IOException {
        Path path = Paths.get("config.properties");
        byte defaultConfig[] = "host=192.168.11.84\nport=6666".getBytes();
        try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(path, CREATE_NEW))) {
            out.write(defaultConfig, 0, defaultConfig.length);
            logger.debug("Config file was removed. New config file is created.");
        } catch (IOException x) {
            logger.debug("Config file is already exist");
        }
    }

    /**
     * Tells {@link Main} to open login window
     * @param loginStage for create stage
     */
    public void startLoginLayout(Stage loginStage) {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            FXMLLoader loader = new FXMLLoader();
            Image icon = new Image(getClass().getResourceAsStream(LOGO));
            loginStage.getIcons().add(icon);
            loader.setLocation(classLoader.getResource("fxml/loginLayout.fxml"));
            Pane loginLayout = loader.load();
            LoginLayoutController loginLayoutController = loader.getController();
            loginLayoutController.setUpStage(loginStage);
            Scene scene = new Scene(loginLayout);
            loginStage.setScene(scene);
            loginStage.setTitle("SuperVisor");
            loginStage.setResizable(false);
            loginStage.show();
        } catch (IOException e) {
            logger.debug("Error when start Main Window "+e);
        }
    }
}
