package ua.softgroup.matrix.desktop.controllerjavafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;

/**
 * @author Andrii Bei <sg.andriy2@gmail.com>
 */

/**
 * TODO For Andrii Bei:
 * 1) Create method that will load user projects in table form CurrentSessionManager(need to use synchronization,
 * will think about it together later, just make some sketches).
 */
public class MainLayoutController {

    @FXML
    public Menu menuReport;
    @FXML
    public MenuBar menuBar;

    public void startReportLayoutWindow(ActionEvent actionEvent) {
        try {
            Stage  primaryStage = new Stage();
            ClassLoader classLoader = getClass().getClassLoader();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(classLoader.getResource("fxml/reportLayout.fxml"));
            AnchorPane anchorPane = loader.load();
            Scene scene = new Scene(anchorPane);
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1200);
            primaryStage.setMinHeight(750);
            primaryStage.initModality(Modality.WINDOW_MODAL);
            primaryStage.initOwner(menuBar.getScene().getWindow());
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startProjectsLayoutController(BorderPane mainLayout) {
        try {
            ClassLoader  classLoader = getClass().getClassLoader();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(classLoader.getResource("fxml/projectsLayout.fxml"));
            AnchorPane projectsLayout = loader.load();
            mainLayout.setCenter(projectsLayout);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void startSettingLayoutWindow(ActionEvent actionEvent) {
        try {
            Stage  settingStage = new Stage();
            ClassLoader classLoader = getClass().getClassLoader();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(classLoader.getResource("fxml/settingsLayout.fxml"));
            Pane pane = loader.load();
            Scene scene = new Scene(pane);
            settingStage.setScene(scene);
            settingStage.setMinWidth(500);
            settingStage.setMinHeight(250);
            settingStage.initModality(Modality.WINDOW_MODAL);
            settingStage.initOwner(menuBar.getScene().getWindow());
            settingStage.setResizable(false);
            settingStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
