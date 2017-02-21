package ua.softgroup.matrix.desktop.controllerjavafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ua.softgroup.matrix.desktop.currentsessioninfo.CurrentSessionInfo;
import ua.softgroup.matrix.desktop.sessionmanagers.ReportServerSessionManager;
import ua.softgroup.matrix.server.desktop.model.ProjectModel;
import ua.softgroup.matrix.server.desktop.model.ReportModel;

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
    private ObservableList<ReportModel> reportData = FXCollections.observableArrayList();
    private ReportServerSessionManager reportServerSessionManager;
    private Long projectId;
    private Set<ReportModel> report;
    private String reportText;

    @FXML
    private void initialize() throws IOException {
        projectId = CurrentSessionInfo.getProjectId();
        reportServerSessionManager = new ReportServerSessionManager();
        System.out.println(projectId);
        if (projectId != 0) {
            report = reportServerSessionManager.sendProjectData(projectId);
        }
        initReport();
        setOtherProjectInfoInView(projectId);
    }

    private void setOtherProjectInfoInView(Long id) {
        Set<ProjectModel> projectAll = CurrentSessionInfo.getUserActiveProjects();
        for (ProjectModel model :
                projectAll) {
            if (model.getId() == id) {
                labelResponsible.setText(model.getAuthorName());
                labelProjectName.setText(model.getTitle());
                taEditReport.setText(reportText);
                if(model.getEndDate()!=null&&model.getStartDate()!=null){
                    labelStartDate.setText(model.getStartDate().toString());
                    labelDeadlineDate.setText(model.getEndDate().toString());
                }
            }
        }
    }

    private void initReport() {
        reportTableColumnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        reportTableColumnTime.setCellValueFactory(new PropertyValueFactory<>("id"));
        reportTableColumnVerified.setCellValueFactory(new PropertyValueFactory<>("checked"));
        reportTableColumnReport.setCellValueFactory(new PropertyValueFactory<>("description"));

        if (report != null && !report.isEmpty()) {
            for (ReportModel model :
                    report) {
                reportData.add(model);
                reportText=model.getDescription();
            }
            tableViewReport.setItems(reportData);
        }
    }

    public void CancelReportWindow(ActionEvent actionEvent) {
        ((Node) actionEvent.getSource()).getScene().getWindow().hide();
    }

    public void ChangeReportWindow(ActionEvent actionEvent) {
        ((Node) actionEvent.getSource()).getScene().getWindow().hide();
    }
}
