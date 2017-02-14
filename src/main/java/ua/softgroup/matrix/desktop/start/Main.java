package ua.softgroup.matrix.desktop.start;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.softgroup.matrix.desktop.controllerjavafx.LoginLayoutController;

import java.io.IOException;
import java.time.LocalDateTime;


public class Main extends Application {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private Stage loginStage;
    private LoginLayoutController loginLayoutController;

    public static void main(String[] args) {
        logger.debug("Current time: {}", LocalDateTime.now());
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        loginLayoutController=new LoginLayoutController();
        this.loginStage = primaryStage;
        this.loginStage.setTitle("SuperVisor");
        loginLayoutController.startLoginLayout(loginStage);
        loginStage.close();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }
}
