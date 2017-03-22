package ua.softgroup.matrix.desktop.controllerjavafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import ua.softgroup.matrix.desktop.model.ReportControlModel;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

/**
 * @author Andrii Bei <sg.andriy2@gmail.com>
 */
public class ControlLayoutController implements Initializable {
    @FXML
    public ListView listViewReportDetails;
    @FXML
    public ListView listViewAllUsers;
    private ObservableList<ReportControlModel> controlList;

    public ControlLayoutController() {
        controlList = FXCollections.observableArrayList();
        controlList.addAll(new ReportControlModel(LocalDate.now(), 1, "20", "20", 7, 8, 6, 7, true, 4.5, 5, 6, "fdfdsfdsfds"));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listViewReportDetails.setItems(controlList);
    }
}
