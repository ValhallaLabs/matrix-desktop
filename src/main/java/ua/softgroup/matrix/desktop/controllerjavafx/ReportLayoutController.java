package ua.softgroup.matrix.desktop.controllerjavafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ua.softgroup.matrix.api.model.datamodels.ProjectModel;
import ua.softgroup.matrix.api.model.datamodels.ReportModel;
import ua.softgroup.matrix.desktop.currentsessioninfo.CurrentSessionInfo;
import ua.softgroup.matrix.desktop.sessionmanagers.ReportServerSessionManager;

import java.util.Set;


/**
 * @author Andrii Bei <sg.andriy2@gmail.com>
 */
public class ReportLayoutController extends Controller {
    @FXML
    public TableView<ReportModel> tableViewReport;
    @FXML
    public TableColumn<ReportModel, Integer> reportTableColumnDate;
    @FXML
    public TableColumn<ReportModel, String> reportTableColumnTime;
    @FXML
    public TableColumn<ReportModel, Boolean> reportTableColumnVerified;
    @FXML
    public TableColumn<ReportModel, String> reportTableColumnReport;
    @FXML
    public TableColumn<ReportModel, Double> reportTableCoefficient;
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
    private static final String CHECKED_COLUMN = "checked";
    private static final String DESCRIPTION_COLUMN = "text";
    private static final String WORK_TIME_COLUMN = "currency";
    private static final String COEFFICIENT_COLUMN = "coefficient";
    private static final int MIN_TEXT_FOR_REPORT = 70;
    private static final String UNKNOWN_DATA = "Unknown";
    private static final String UNLIMITED_DATA = "Unlimited";
    private ObservableList<ReportModel> reportData = FXCollections.observableArrayList();
    private ReportServerSessionManager reportServerSessionManager;
    private Long currentProjectId;
    private Set<ReportModel> report;
    private Long currentReportId;

    /**
     * After Load/Parsing fxml call this method
     * Create {@link ReportLayoutController} and if project has reports set this data in Set of ReportModel
     */
    @FXML
    private void initialize() {
        currentProjectId = CurrentSessionInfo.getProjectId();
        reportServerSessionManager = new ReportServerSessionManager(this);
        getAllReportAndSetToCollection();
    }

    /**
     * Check on null and is Empty Set of report
     */
    private void getAllReportAndSetToCollection() {
        initializeReport();
        if (report != null && !report.isEmpty()) {
            initReportInTable();
            setProjectInfoInView(currentProjectId);
            setFocusOnTableView();
        }
    }

    /**
     * Create {@link ReportServerSessionManager}
     */
    private void initializeReport() {
        report = reportServerSessionManager.getReportsByProjectId(currentProjectId);
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
        countTextAndSetButtonCondition(reportModel);
        currentReportId = reportModel.getId();
        if (reportModel.getText() != null) {
            taEditReport.setText(reportModel.getText());
        }
    }

    /**
     * Get from current project information's and set their in label view element
     *
     * @param id of project what user choose in project window
     */
    private void setProjectInfoInView(Long id) {
        Set<ProjectModel> projectAll = CurrentSessionInfo.getProjectModels();
        for (ProjectModel model : projectAll) {
            if (model.getId() == id) {
                labelResponsible.setText(model.getAuthorName());
                labelProjectName.setText(model.getTitle());
                if (model.getEndDate() != null && model.getStartDate() != null) {
                    labelStartDate.setText(model.getStartDate().toString());
                    labelDeadlineDate.setText(model.getEndDate().toString());
                } else {
                    labelStartDate.setText(UNKNOWN_DATA);
                    labelDeadlineDate.setText(UNLIMITED_DATA);
                }
            }
        }
    }

    /**
     * Set connect table column with {@link ReportModel} for future pull date in this field
     */
    private void initReportInTable() {
        reportTableColumnDate.setCellValueFactory(new PropertyValueFactory<>(DATE_COLUMN));
        reportTableColumnTime.setCellValueFactory(new PropertyValueFactory<>(WORK_TIME_COLUMN));
        reportTableColumnVerified.setCellValueFactory(new PropertyValueFactory<>(CHECKED_COLUMN));
        reportTableColumnReport.setCellValueFactory(new PropertyValueFactory<>(DESCRIPTION_COLUMN));
        reportTableCoefficient.setCellValueFactory(new PropertyValueFactory<>(COEFFICIENT_COLUMN));
        setReportInfoInView();
    }

    /**
     * If project has report displays this data in Table View and sort Date column in ASCENDING type
     */
    @SuppressWarnings("unchecked")
    private void setReportInfoInView() {
        for (ReportModel model : report) {
            model.setCurrency(convertFromSecondsToHoursAndMinutes(model.getWorkTime()) + " x " + model.getRate() + convertFromCurrencyToSymbol(model.getCurrency()));
            reportData.add(model);
        }
        tableViewReport.setItems(reportData);
        tableViewReport.getSortOrder().setAll(reportTableColumnDate);
    }

    /**
     * Hears when user click on button and close stage without any change
     *
     * @param actionEvent callback click on button
     */
    public void CancelAndCloseReportWindow(ActionEvent actionEvent) {
        ((Node) actionEvent.getSource()).getScene().getWindow().hide();
    }

    /**
     * Hears when user click on button and send report also clear data in collections
     *
     * @param actionEvent callback click on button
     */
    public void createOrChangeReport(ActionEvent actionEvent) {
        ReportModel reportModel = new ReportModel(currentReportId, taEditReport.getText());
        reportServerSessionManager.saveOrChangeReportOnServer(reportModel);
        reportData.clear();
        notifyChangeInTableViewDynamic(reportModel);
    }

    /**
     * Set report what we create ore change in the text area
     *
     * @param report report what we create or change
     */
    private void notifyChangeInTableViewDynamic(ReportModel report) {
        initializeReport();
        setReportInfoInView();
        taEditReport.setText(report.getText());
    }

    /**
     * Hears when user click on table view,get chosen report and set Description information in TextArea
     *
     * @param event callback click on table view
     */
    public void chooseReport(Event event) {
        if (tableViewReport.getSelectionModel().getSelectedItem() != null && report != null) {
            ReportModel selectReport = tableViewReport.getSelectionModel().getSelectedItem();
            countTextAndSetButtonCondition(selectReport);
            currentReportId = selectReport.getId();
            if (selectReport.getText() != null) {
                taEditReport.setText(selectReport.getText());
            } else taEditReport.setText("");
        }
    }

    /**
     * Hears when text input in TextArea and if this text count >= {@value MIN_TEXT_FOR_REPORT}
     * button for change report became active
     */
    @FXML
    private void countTextAndSetButtonCondition(ReportModel reportModel) {
        taEditReport.textProperty().addListener((observable, oldValue, newValue) -> {
            int size = newValue.length();
            if (size >= MIN_TEXT_FOR_REPORT && !reportModel.isChecked()) {
                btnChangeReport.setDisable(false);
                taEditReport.setEditable(true);
            } else {
                btnChangeReport.setDisable(true);
            }
        });
    }


    /**
     * Check what currency we have and return necessary symbol for it
     *
     * @param currency current currency what we get
     * @return String symbol of currency
     */
    private String convertFromCurrencyToSymbol(String currency) {
        return "USD".equals(currency) ? "$" : "₴";
    }

}
