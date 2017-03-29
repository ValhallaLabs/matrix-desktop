package ua.softgroup.matrix.desktop.controllerjavafx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.AnchorPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.softgroup.matrix.desktop.model.localModel.RequestControl;

import java.io.IOException;

/**
 * @author Andrii Bei <sg.andriy2@gmail.com>
 */
class ControlListViewCell extends ListCell<RequestControl> {
    @FXML
    private Label labelDate;
    @FXML
    private Label labelProject;
    @FXML
    private Label labelStart;
    @FXML
    private Label labelEnd;
    @FXML
    private Label labelSupervisorId;
    @FXML
    private Label labelChecked;
    @FXML
    private Label labelWorkSeconds;
    @FXML
    private Label labelIdleSeconds;
    @FXML
    private Label labelIdlePercentage;
    @FXML
    private Label labelRate;
    @FXML
    private Label labelCoefficient;
    @FXML
    private Label labelCurrency;
    @FXML
    private Label labelTextReport;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Label labelTotalWorkSecond;
    @FXML
    private Label labelTotalIdleSecond;
    @FXML
    private Label labelITotalIdlePercentage;
    private FXMLLoader mLoader;
    private static final Logger logger = LoggerFactory.getLogger(ControlListViewCell.class);

    @Override
    protected void updateItem(RequestControl item, boolean empty) {
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
                    logger.debug("Error when start Control List Cell Window" + e.toString());
                }
            }
            if (item.getDate() != null) {
                labelDate.setText(String.valueOf(item.getDate()));
            }
            labelTotalWorkSecond.setText(String.valueOf(item.getTotalWorkSeconds()));
            labelTotalIdleSecond.setText(String.valueOf(item.getTotalIdleSeconds()));
            labelITotalIdlePercentage.setText(String.valueOf(item.getTotalIdlePercentage()));
            labelProject.setText(String.valueOf(item.getProjectId()));
            labelStart.setText(item.getStart());
            labelEnd.setText(item.getEnd());
            labelSupervisorId.setText(String.valueOf(item.getCheckerId()));
            labelChecked.setText(String.valueOf(item.isChecked()));
            labelWorkSeconds.setText(String.valueOf(item.getWorkSeconds()));
            labelIdleSeconds.setText(String.valueOf(item.getIdleSeconds()));
            labelIdlePercentage.setText(String.valueOf(item.getIdlePercentage()));
            labelRate.setText(String.valueOf(item.getRate()));
            labelCoefficient.setText(String.valueOf(item.getCoefficient()));
            if (item.getCurrencyId() == 1) {
                labelCurrency.setText("$");
            } else labelCurrency.setText("₴");
            labelTextReport.setText(item.getReportText());
            setText(null);
            setGraphic(anchorPane);
        }
    }
}
