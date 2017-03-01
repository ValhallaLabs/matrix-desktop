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
import javafx.stage.Window;
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
    private static final String REPORT_LAYOUT = "fxml/reportLayout.fxml";
    private static final int REPORT_LAYOUT_MIN_WIDTH = 1200;
    private static final int REPORT_LAYOUT_MIN_HEIGHT = 750;
    private static final String PROJECT_LAYOUT = "fxml/projectsLayout.fxml";
    private static final String SETTING_LAYOUT = "fxml/settingsLayout.fxml";
    private static final int SETTING_LAYOUT_MIN_WIDTH = 500;
    private static final int SETTING_LAYOUT_MIN_HEIGHT = 250;
    private static final String INSTRUCTIONS_LAYOUT = "fxml/instructionsLayout.fxml";
    private static final int INSTRUCTIONS_LAYOUT_MIN_WIDTH = 900;
    private static final int INSTRUCTIONS_LAYOUT_MIN_HEIGHT = 600;
    private static final Logger logger = LoggerFactory.getLogger(MainLayoutController.class);
    @FXML
    public Menu menuReport;
    @FXML
    public MenuBar menuBar;

    /**
     * Hears when user click on report menus item
     *
     * @param actionEvent callback click on menu
     */
    public void startReportLayoutWindow(ActionEvent actionEvent) {
        startReport(menuBar.getScene().getWindow());
    }

    /**
     * Tells {@link MainLayoutController} open report window
     *
     * @param window Window what will be owner by modality report window
     */
    public void startReport(Window window) {
        try {
            Stage primaryStage = new Stage();
            ClassLoader classLoader = getClass().getClassLoader();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(classLoader.getResource(REPORT_LAYOUT));
            AnchorPane anchorPane = loader.load();
            Scene scene = new Scene(anchorPane);
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(REPORT_LAYOUT_MIN_WIDTH);
            primaryStage.setMinHeight(REPORT_LAYOUT_MIN_HEIGHT);
            primaryStage.initModality(Modality.WINDOW_MODAL);
            primaryStage.initOwner(window);
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (IOException e) {
            logger.debug("Error when start Report Window " + e);
            e.printStackTrace();
        }
    }

    /**
     * Tells {@link MainLayoutController} to open and set in the center of it project window
     *
     * @param mainLayout
     */
    public void startProjectsLayoutController(BorderPane mainLayout) {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(classLoader.getResource(PROJECT_LAYOUT));
            AnchorPane projectsLayout = loader.load();
            mainLayout.setCenter(projectsLayout);
        } catch (IOException e) {
            logger.debug("Error when start Projects Window " + e);
        }
    }

    /**
     * Hears when user click on setting menus item and
     * tells {@link MainLayoutController} to open setting window
     *
     * @param actionEvent callback click on menu
     */
    public void startSettingLayoutWindow(ActionEvent actionEvent) {
        try {
            Stage settingStage = new Stage();
            ClassLoader classLoader = getClass().getClassLoader();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(classLoader.getResource(SETTING_LAYOUT));
            Pane pane = loader.load();
            Scene scene = new Scene(pane);
            settingStage.setScene(scene);
            settingStage.setMinWidth(SETTING_LAYOUT_MIN_WIDTH);
            settingStage.setMinHeight(SETTING_LAYOUT_MIN_HEIGHT);
            settingStage.initModality(Modality.WINDOW_MODAL);
            settingStage.initOwner(menuBar.getScene().getWindow());
            settingStage.setResizable(false);
            settingStage.show();
        } catch (IOException e) {
            logger.debug("Error when start Setting Window " + e);
        }
    }

    /**
     * Hears when user click on setting menus item and
     * tells {@link MainLayoutController} to open instructions window
     *
     * @param actionEvent callback click on menu
     */
    public void startInstructionsLayoutWindow(ActionEvent actionEvent) {
        try {
            Stage InstructionsStage = new Stage();
            ClassLoader classLoader = getClass().getClassLoader();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(classLoader.getResource(INSTRUCTIONS_LAYOUT));
            Pane pane = loader.load();
            Scene scene = new Scene(pane);
            InstructionsStage.setScene(scene);
            InstructionsStage.setMinWidth(INSTRUCTIONS_LAYOUT_MIN_WIDTH);
            InstructionsStage.setMinHeight(INSTRUCTIONS_LAYOUT_MIN_HEIGHT);
            InstructionsStage.initModality(Modality.WINDOW_MODAL);
            InstructionsStage.initOwner(menuBar.getScene().getWindow());
            InstructionsStage.setResizable(false);
            InstructionsStage.show();
        } catch (IOException e) {
            logger.debug("Error when start Instructions Window " + e);
        }
    }
}
