package ua.softgroup.matrix.desktop.controllerjavafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.softgroup.matrix.desktop.model.ReportControlModel;
import ua.softgroup.matrix.desktop.model.UserProfile;
import ua.softgroup.matrix.desktop.model.localModel.RequestControl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrii Bei <sg.andriy2@gmail.com>
 */
public class ControlListViewCell extends ListCell<RequestControl> {
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
