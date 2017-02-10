package ua.softgroup.matrix.desktop.start;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;


public class Main extends Application {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private Stage loginStage;
    private ClassLoader classLoader;
    private FXMLLoader loader;
    private BorderPane mainLayout;
    private AnchorPane projectsLayout;

    public static void main(String[] args) {
        logger.debug("Current time: {}", LocalDateTime.now());
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.loginStage = primaryStage;
        this.loginStage.setTitle("SuperVisor");
        setLoginPane();
        setProjectPane();
    }

    private void setLoginPane() {
        try {
            classLoader = getClass().getClassLoader();
            loader = new FXMLLoader();
            loader.setLocation(classLoader.getResource("fxml/mainLayout.fxml"));
            mainLayout = loader.load();
            Scene scene = new Scene(mainLayout);
            loginStage.setScene(scene);
            loginStage.show();
            loginStage.setMinWidth(1200);
            loginStage.setMinHeight(800);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setProjectPane() {
        try {
            classLoader = getClass().getClassLoader();
            loader = new FXMLLoader();
            loader.setLocation(classLoader.getResource("fxml/projectsLayout.fxml"));
            projectsLayout = loader.load();
            mainLayout.setCenter(projectsLayout);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
