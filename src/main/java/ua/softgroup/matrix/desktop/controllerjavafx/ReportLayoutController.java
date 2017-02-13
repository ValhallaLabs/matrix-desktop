package ua.softgroup.matrix.desktop.controllerjavafx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by AndriiBei on 10.02.2017.
 */
public class ReportLayoutController {
    private ClassLoader classLoader;
    private FXMLLoader loader;

    private Stage primaryStage;

    public void startReportLayoutController() {
        primaryStage=new Stage();
        startReportLayout();
    }

    private void startReportLayout() {
        try {
            classLoader = getClass().getClassLoader();
            loader = new FXMLLoader();
            loader.setLocation(classLoader.getResource("fxml/reportLayout.fxml"));
           AnchorPane anchorPane = loader.load();
            Scene scene = new Scene(anchorPane);
            primaryStage.setScene(scene);
            primaryStage.show();
            primaryStage.setMinWidth(1200);
            primaryStage.setMinHeight(800);
            primaryStage.setResizable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
