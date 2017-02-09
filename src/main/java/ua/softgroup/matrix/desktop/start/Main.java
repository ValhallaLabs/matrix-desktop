package ua.softgroup.matrix.desktop.start;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.IOException;


public class Main extends Application {

    private Stage loginStage;
    private ClassLoader classLoader;
    private FXMLLoader loader;
    private BorderPane mainLayout;
    private AnchorPane projectsLayout;
    private Pane loginLayout;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.loginStage = primaryStage;
        this.loginStage.setTitle("SuperVisor");
        setLogin();
//        setLoginPane();
//        setProjectPane();
    }

    private void setLogin() {

        try {
            classLoader = getClass().getClassLoader();
            loader = new FXMLLoader();
            loader.setLocation(classLoader.getResource("fxml/loginLayout.fxml"));
            loginLayout=loader.load();
            Scene scene = new Scene(loginLayout);
            loginStage.setScene(scene);
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
