package ua.softgroup.matrix.desktop.controllerjavafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
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
public class MainLayoutController {
    private static final String REPORT_LAYOUT = "fxml/reportLayout.fxml";
    private static final int REPORT_LAYOUT_MIN_WIDTH = 1200;
    private static final int REPORT_LAYOUT_MIN_HEIGHT = 750;
    private static final String PROJECT_LAYOUT = "fxml/projectsLayout.fxml";
    private static final String INSTRUCTIONS_LAYOUT = "fxml/instructionsLayout.fxml";
    private static final int INSTRUCTIONS_LAYOUT_MIN_WIDTH = 900;
    private static final int INSTRUCTIONS_LAYOUT_MIN_HEIGHT = 600;
    private static final String LOGO = "/images/logoIcon.png";
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
    void startReport(Window window) {
        try {
            Stage reportsStage = new Stage();
            ClassLoader classLoader = getClass().getClassLoader();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(classLoader.getResource(REPORT_LAYOUT));
            AnchorPane anchorPane = loader.load();
            Scene scene = new Scene(anchorPane);
            reportsStage.setScene(scene);
            Image logoIcon = new Image(getClass().getResourceAsStream(LOGO));
            reportsStage.getIcons().add(logoIcon);
            reportsStage.setMinWidth(REPORT_LAYOUT_MIN_WIDTH);
            reportsStage.setMinHeight(REPORT_LAYOUT_MIN_HEIGHT);
            reportsStage.initModality(Modality.WINDOW_MODAL);
            reportsStage.setTitle("Reports Window");
            reportsStage.initOwner(window);
            reportsStage.setResizable(false);
            reportsStage.show();
        } catch (IOException e) {
            logger.debug("Error when start Report Window " + e);
            e.printStackTrace();
        }
    }

    /**
     * Tells {@link MainLayoutController} to open and set in the center of it project window
     *
     * @param mainLayout get BorderPane from main window
     */
    void startProjectsLayoutController(BorderPane mainLayout) {
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
     * tells {@link MainLayoutController} to open instructions window
     *
     * @param actionEvent callback click on menu
     */
    public void startInstructionsLayoutWindow(ActionEvent actionEvent) {
        try {

            ClassLoader classLoader = getClass().getClassLoader();
            Stage instructionsStage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(classLoader.getResource(INSTRUCTIONS_LAYOUT));
            Pane pane = loader.load();
            Scene scene = new Scene(pane);
            instructionsStage.setScene(scene);
            Image logoIcon = new Image(getClass().getResourceAsStream(LOGO));
            instructionsStage.getIcons().add(logoIcon);
            instructionsStage.setMinWidth(INSTRUCTIONS_LAYOUT_MIN_WIDTH);
            instructionsStage.setMinHeight(INSTRUCTIONS_LAYOUT_MIN_HEIGHT);
            instructionsStage.initModality(Modality.WINDOW_MODAL);
            instructionsStage.setTitle("Instructions Window");
            instructionsStage.initOwner(menuBar.getScene().getWindow());
            instructionsStage.setResizable(false);
            instructionsStage.show();
        } catch (IOException e) {
            logger.debug("Error when start Instructions Window " + e);
        }
    }


}
