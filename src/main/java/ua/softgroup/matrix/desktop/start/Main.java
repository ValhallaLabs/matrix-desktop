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

    public static void main(String[] args) {
        logger.debug("Current time: {}", LocalDateTime.now());
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Stage loginStage = primaryStage;
        loginStage.setTitle("SuperVisor");
        startLoginLayout(loginStage);
    }

    public void startLoginLayout(Stage loginStage) {
        System.out.println("pizdec");
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(classLoader.getResource("fxml/loginLayout.fxml"));
            Pane loginLayout = loader.load();
            LoginLayoutController loginLayoutController = loader.getController();
            loginLayoutController.setUpStage(loginStage);
            Scene scene = new Scene(loginLayout);
            loginStage.setScene(scene);
            loginStage.initStyle(StageStyle.UTILITY);
            loginStage.setResizable(false);
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
