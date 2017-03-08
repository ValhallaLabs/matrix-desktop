package ua.softgroup.matrix.desktop.controllerjavafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ua.softgroup.matrix.desktop.currentsessioninfo.CurrentSessionInfo;
import ua.softgroup.matrix.desktop.sessionmanagers.ReportServerSessionManager;
import ua.softgroup.matrix.server.desktop.model.datamodels.ProjectModel;
import ua.softgroup.matrix.server.desktop.model.datamodels.ReportModel;


import java.io.IOException;
import java.util.Set;


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
    @FXML
    public Label labelProjectName;
    @FXML
    public Label labelResponsible;
    @FXML
    public Label labelStartDate;
    @FXML
    public Label labelDeadlineDate;
    @FXML
    public TextArea taEditReport;
    private static final String DATE_COLUMN = "date";
    private static final String ID_COLUMN = "id";
    private static final String CHECKED_COLUMN = "checked";
    private static final String DESCRIPTION_COLUMN = "text";
    private static final int MIN_TEXT_FOR_REPORT = 70;
    private ObservableList<ReportModel> reportData = FXCollections.observableArrayList();
    private ReportServerSessionManager reportServerSessionManager;
    private Long currentProjectId;
    private Set<ReportModel> report;
    private String reportText;
    private Long currentReportId;


    /**
     *  After Load/Parsing fxml call this method
     * Create {@link ReportLayoutController} and if project has reports set this data in Set of ReportModel
     * @throws IOException
     */
    @FXML
    private void initialize() throws IOException, ClassNotFoundException {
        currentProjectId = CurrentSessionInfo.getProjectId();
        reportServerSessionManager = new ReportServerSessionManager();
            report = reportServerSessionManager.sendProjectDataAndGetReportById(currentProjectId);
        initReportInTable();
        setProjectInfoInView(currentProjectId);
        setFocusOnTableView();
        countTextAndSetButtonCondition();

    }

    /**
     * At start report window select first item in Table View {@link ReportModel}
     * Get from current report information and set their in textArea
     */
    private void setFocusOnTableView() {
        tableViewReport.requestFocus();
        tableViewReport.getSelectionModel().select(0);
        tableViewReport.getFocusModel().focus(0);
        ReportModel reportModel = tableViewReport.getSelectionModel().getSelectedItem();
        checkVerifyReportAndSetButtonCondition(reportModel);
        currentReportId = reportModel.getId();
        taEditReport.setText(reportModel.getText());
    }

    /**
     *  Get from current project information's and set their in label view element
     *
     * @param id of project what user choose in project window
     */
    private void setProjectInfoInView(Long id) {
        Set<ProjectModel> projectAll = CurrentSessionInfo.getProjectModels();
        for (ProjectModel model :
                projectAll) {
            if (model.getId() == id) {
                labelResponsible.setText(model.getAuthorName());
                labelProjectName.setText(model.getTitle());
                if (model.getEndDate() != null && model.getStartDate() != null) {
                    labelStartDate.setText(model.getStartDate().toString());
                    labelDeadlineDate.setText(model.getEndDate().toString());
                }
            }
        }
    }

    /**
     * Set connect table column with {@link ReportModel} for future pull date in this field
     */
    private void initReportInTable() {
        reportTableColumnDate.setCellValueFactory(new PropertyValueFactory<>(DATE_COLUMN));
        reportTableColumnTime.setCellValueFactory(new PropertyValueFactory<>(ID_COLUMN));
        reportTableColumnVerified.setCellValueFactory(new PropertyValueFactory<>(CHECKED_COLUMN));
        reportTableColumnReport.setCellValueFactory(new PropertyValueFactory<>(DESCRIPTION_COLUMN));
        setReportInfoInView();
    }

    /**
     * If project has report displays this data in Table View and sort Date column in ASCENDING type
     */
    @SuppressWarnings("unchecked")
    private void setReportInfoInView() {
        if (report != null && !report.isEmpty()) {
            for (ReportModel model :
                    report) {
                reportData.add(model);
                reportText = model.getText();
            }

            tableViewReport.setItems(reportData);
            tableViewReport.getSortOrder().setAll(reportTableColumnDate);
        }
    }

    /**
     * Check if this report checked or no and set disable condition of button
     * @param reportModel current report what user selected in table
     */
    private void checkVerifyReportAndSetButtonCondition(ReportModel reportModel) {
        //TODO: check on null
        if (reportModel.isChecked()) {
            btnChangeReport.setDisable(true);
        } else btnChangeReport.setDisable(false);
    }

    /**
     * Hears when user click on button and close stage without any change
     * @param actionEvent callback click on button
     */
    public void CancelAndCloseReportWindow(ActionEvent actionEvent) {
        ((Node) actionEvent.getSource()).getScene().getWindow().hide();
    }

    /**
     * Hears when user click on button and send change {@link ReportModel} to {@link ReportLayoutController} and close stage
     * @param actionEvent callback click on button
     * @throws IOException
     */
    public void changeReport(ActionEvent actionEvent) throws IOException, ClassNotFoundException {
        ReportModel reportModel = new ReportModel(currentReportId, taEditReport.getText(), currentProjectId);
        reportServerSessionManager.saveOrChangeReportOnServer(reportModel);
        ((Node) actionEvent.getSource()).getScene().getWindow().hide();
    }

    /**
     * Hears when user click on table view,get chosen report and set Description information in TextArea
     * @param event callback click on table view
     */
    public void chooseReport(Event event) {
        if (tableViewReport.getSelectionModel().getSelectedItem() != null) {
            ReportModel selectReport = tableViewReport.getSelectionModel().getSelectedItem();
            checkVerifyReportAndSetButtonCondition(selectReport);
            currentReportId = selectReport.getId();
            taEditReport.setText(selectReport.getText());
        }
    }

    /**
     *  Hears when text input in TextArea and if this text count >= {@value MIN_TEXT_FOR_REPORT}
     * button for change report became active
     */
    @FXML
    private void countTextAndSetButtonCondition() {
        taEditReport.textProperty().addListener((observable, oldValue, newValue) -> {
            int size = newValue.length();
            if (size >= MIN_TEXT_FOR_REPORT) {
                btnChangeReport.setDisable(false);
            } else btnChangeReport.setDisable(true);
        });
    }
}
