package ua.softgroup.matrix.desktop.controllerjavafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import ua.softgroup.matrix.server.desktop.model.ReportModel;
import java.io.IOException;
import java.time.LocalDate;

/**
 * @author Andrii Bei <sg.andriy2@gmail.com>
 */
public class ReportLayoutController {

    @FXML
    public TableView<ReportModel> tableViewReport;
    @FXML
    public TableColumn<ReportModel, Integer> reportTableColumnDate;
    @FXML
    public TableColumn<ReportModel, Long> reportTableColumnTime;
    @FXML
    public TableColumn<ReportModel, Boolean> reportTableColumnVerified;
    @FXML
    public TableColumn<ReportModel, String> reportTableColumnReport;
    @FXML
    public Button btnChangeReport;
    @FXML
    public Button btnCancelReport;

    ObservableList<ReportModel> reportData = FXCollections.observableArrayList();
    private Stage primaryStage;

    @FXML
    private void initialize() {
        initReport();
    }

    public void startReportLayoutController() {
        primaryStage = new Stage();
        startReportLayout();
        initReport();
    }

    private void initReport() {
        reportTableColumnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        reportTableColumnTime.setCellValueFactory(new PropertyValueFactory<>("id"));
        reportTableColumnVerified.setCellValueFactory(new PropertyValueFactory<>("checked"));
        reportTableColumnReport.setCellValueFactory(new PropertyValueFactory<>("description"));
        reportData.add(new ReportModel(LocalDate.now(), 1, true, "ddffdsggdfgdfgdfgdf"));
        reportData.add(new ReportModel(LocalDate.now(), 3, true, "ddffdsggdfgdfgdfgdf"));
        reportData.add(new ReportModel(LocalDate.now(), 2, true, "ddffdsggdfgdfgdfgdf"));
        reportData.add(new ReportModel(LocalDate.now(), 4, true, "ddffdsggdfgdfgdfgdf"));
        reportData.add(new ReportModel(LocalDate.now(), 4, true, "ddffdsggdfgdfgdfgdf"));
        reportData.add(new ReportModel(LocalDate.now(), 4, true, "ddffdsggdfgdfgdfgdf"));
        reportData.add(new ReportModel(LocalDate.now(), 4, true, "ddffdsggdfgdfgdfgdf"));
        reportData.add(new ReportModel(LocalDate.now(), 4, true, "ddffdsggdfgdfgdfgdf"));
        reportData.add(new ReportModel(LocalDate.now(), 4, true, "ddffdsggdfgdfgdfgdf"));
        reportData.add(new ReportModel(LocalDate.now(), 4, true, "ddffdsggdfgdfgdfgdf"));
        reportData.add(new ReportModel(LocalDate.now(), 4, true, "ddffdsggdfgdfgdfgdf"));
        reportData.add(new ReportModel(LocalDate.now(), 4, true, "ddffdsggdfgdfgdfgdf"));
        reportData.add(new ReportModel(LocalDate.now(), 4, true, "ddffdsggdfgdfgdfgdf"));
        reportData.add(new ReportModel(LocalDate.now(), 4, true, "ddffdsggdfgdfgdfgdf"));
        reportData.add(new ReportModel(LocalDate.now(), 4, true, "ddffdsggdfgdfgdfgdf"));
        reportData.add(new ReportModel(LocalDate.now(), 4, true, "ddffdsggdfgdfgdfgdf"));
        reportData.add(new ReportModel(LocalDate.now(), 4, true, "ddffdsggdfgdfgdfgdf"));
        reportData.add(new ReportModel(LocalDate.now(), 4, true, "ddffdsggdfgdfgdfgdf"));
        reportData.add(new ReportModel(LocalDate.now(), 4, true, "ddffdsggdfgdfgdfgdf"));
        reportData.add(new ReportModel(LocalDate.now(), 4, true, "ddffdsggdfgdfgdfgdf"));
        tableViewReport.setItems(reportData);
    }

    private void startReportLayout() {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(classLoader.getResource("fxml/reportLayout.fxml"));
            AnchorPane anchorPane = loader.load();
            Scene scene = new Scene(anchorPane);
            primaryStage.setScene(scene);
            primaryStage.show();
            primaryStage.setMinWidth(1200);
            primaryStage.setMinHeight(750);
            primaryStage.setResizable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void CancelReportWindow(ActionEvent actionEvent) {
        ((Node) actionEvent.getSource()).getScene().getWindow().hide();
    }

    public void ChangeReportWindow(ActionEvent actionEvent) {
        ((Node) actionEvent.getSource()).getScene().getWindow().hide();
    }
}
