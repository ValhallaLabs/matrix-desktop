package ua.softgroup.matrix.desktop.controllerjavafx;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import ua.softgroup.matrix.desktop.model.ReportControlModel;

/**
 * @author Andrii Bei <sg.andriy2@gmail.com>
 */
public class ControlListViewCell extends ListCell<ReportControlModel> {
    @FXML
    public Label labelDate;
    @FXML
    public Label labelProject;
    @FXML
    public Label labelStart;
    @FXML
    public Label labelEnd;
    @FXML
    public Label labelSupervisorId;
    @FXML
    public Label labelChecked;
    @FXML
    public Label labelWorkSeconds;
    @FXML
    public Label labelIdleSeconds;
    @FXML
    public Label labelIdlePercentage;
    @FXML
    public Label labelRate;
    @FXML
    public Label labelCoefficient;
    @FXML
    public Label labelCurrency;
    @FXML
    public Label labelTextReport;

    @Override
    protected void updateItem(ReportControlModel item, boolean empty) {
        super.updateItem(item, empty);
    }
}
