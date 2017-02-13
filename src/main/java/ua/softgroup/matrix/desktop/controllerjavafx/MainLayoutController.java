package ua.softgroup.matrix.desktop.controllerjavafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by AndriiBei on 09.02.2017.
 */
public class MainLayoutController {
    @FXML
    public Menu menuReport;
    @FXML
    public MenuBar menuBar;
    private ClassLoader classLoader;
    private FXMLLoader loader;
    private BorderPane mainLayout;
    private Stage primaryStage;

    public void startMainControllerLayout() {
        primaryStage=new Stage();
        Image icon = new Image(getClass().getResourceAsStream("/images/logo.png"));
        primaryStage.getIcons().add(icon);
        startMainLayout();
        startProjectLayout();
    }

    private void startMainLayout() {
        try {
            classLoader = getClass().getClassLoader();
            loader = new FXMLLoader();
            loader.setLocation(classLoader.getResource("fxml/mainLayout.fxml"));
            mainLayout = loader.load();
            Scene scene = new Scene(mainLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
            primaryStage.setMinWidth(1200);
            primaryStage.setMinHeight(800);
            primaryStage.setResizable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startProjectLayout() {
        try {
            classLoader = getClass().getClassLoader();
            loader = new FXMLLoader();
            loader.setLocation(classLoader.getResource("fxml/projectsLayout.fxml"));
          AnchorPane projectsLayout = loader.load();
            mainLayout.setCenter(projectsLayout);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startReportLayoutWindow(ActionEvent actionEvent) {
//        ((Node)menuBar).getScene().getWindow().hide();
        new ReportLayoutController().startReportLayoutController();
    }
}
