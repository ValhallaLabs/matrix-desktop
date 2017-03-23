package ua.softgroup.matrix.desktop.controllerjavafx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.AnchorPane;
import ua.softgroup.matrix.desktop.model.ReportControlModel;

import java.io.IOException;

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
    @FXML
    public AnchorPane anchorPane;
    private FXMLLoader mLoader;

    @Override
    protected void updateItem(ReportControlModel item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {


            if (mLoader == null) {
                mLoader = new FXMLLoader(getClass().getResource("/fxml/controlListCell.fxml"));
                mLoader.setController(this);

                try {
                    mLoader.load();
                } catch (IOException e) {
                    System.out.println("Error when start Control List Cell Window" + e.toString());
                }
            }
            if (item.getDate() != null) {
                labelDate.setText(String.valueOf(item.getDate()));
            }

            labelProject.setText(String.valueOf(item.getWorkDays().iterator().next().getId()));
            labelStart.setText(item.getWorkDays().iterator().next().getStart());
            labelEnd.setText(item.getWorkDays().iterator().next().getEnd());
            labelSupervisorId.setText(String.valueOf(item.getWorkDays().iterator().next().getJailerId()));
            labelChecked.setText(String.valueOf(item.getWorkDays().iterator().next().isChecked()));
            labelWorkSeconds.setText(String.valueOf(item.getTotalWorkSeconds()));
            labelIdleSeconds.setText(String.valueOf(item.getTotalIdleSeconds()));
            labelIdlePercentage.setText(String.valueOf(item.getTotalIdlePercentage()));
            labelRate.setText(String.valueOf(item.getWorkDays().iterator().next().getRate()));
            labelCoefficient.setText(String.valueOf(item.getWorkDays().iterator().next().getCoefficient()));
            if (item.getWorkDays().iterator().next().getCurrencyId() == 1) {
                labelCurrency.setText("$");
            } else labelCurrency.setText("₴");
            labelTextReport.setText(item.getWorkDays().iterator().next().getReportText());

            setText(null);
            setGraphic(anchorPane);

        }
    }
}
