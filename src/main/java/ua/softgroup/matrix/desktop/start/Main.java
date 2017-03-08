package ua.softgroup.matrix.desktop.start;

import com.sun.jna.platform.FileUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.softgroup.matrix.desktop.controllerjavafx.LoginLayoutController;
import ua.softgroup.matrix.desktop.utils.SocketProvider;

import java.io.*;
import java.lang.reflect.Array;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.CREATE_NEW;


public class Main extends Application {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
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
     * Method tries to bind application to some localhost's port to avoid multiply opening possibility.
     * In case if port is already used, client simply won't run.
     */
    private void checkIfRunning() {
        try {
            socket = new ServerSocket(8109, 0, InetAddress.getByAddress(new byte[] {127,0,0,1}));
            logger.debug("Bind to localhost adapter with a zero connection queue");
        } catch (IOException e) {
            logger.debug("App already running");
            System.exit(1);
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
