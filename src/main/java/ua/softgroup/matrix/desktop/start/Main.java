package ua.softgroup.matrix.desktop.start;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.time.LocalDateTime;


public class Main extends Application {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private Stage loginStage;

    public static void main(String[] args) {
        logger.debug("Current time: {}", LocalDateTime.now());
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.loginStage = primaryStage;
        this.loginStage.setTitle("SuperVisor");

        startLoginLayout();
    }

    private void startLoginLayout() {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(classLoader.getResource("fxml/loginLayout.fxml"));
            Pane loginLayout = loader.load();
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
