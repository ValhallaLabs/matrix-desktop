package ua.softgroup.matrix.desktop.controllerjavafx;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ua.softgroup.matrix.desktop.currentsessioninfo.CurrentSessionInfo;
import ua.softgroup.matrix.desktop.sessionmanagers.ReportServerSessionManager;
import ua.softgroup.matrix.server.desktop.model.ProjectModel;
import ua.softgroup.matrix.server.desktop.model.ReportModel;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.EventListener;
import java.util.Locale;
import java.util.Set;

/**
 * @author Andrii Bei <sg.andriy2@gmail.com>
 */
public class ProjectsLayoutController {

    @FXML
    public TableView<ProjectModel> tvProjectsTable;
    @FXML
    public TableColumn<ProjectModel, Long> tcIdProject;
    @FXML
    public TableColumn<ProjectModel, String> tcAuthorName;
    @FXML
    public TableColumn<ProjectModel, String> tcTitle;
    @FXML
    public TableColumn<ProjectModel, String> tcDescription;
    @FXML
    public TableColumn tcStatus;
    @FXML
    public PieChart missPieCharts;
    @FXML
    public Label labelDayInWord;
    @FXML
    public Label labelDayInNumber;
    @FXML
    public Label labelNameSales;
    @FXML
    public Label labelNameProject;
    @FXML
    public Label labelDiscribeProject;
    @FXML
    public Label labelStartWorkToday;
    @FXML
    public Label labelTodayTotalTime;
    @FXML
    public Label labelTotalTime;
    @FXML
    public Label labelDateStartProject;
    @FXML
    public Label labelDeadLineProject;
    @FXML
    public Button btnStart;
    @FXML
    public Button btnStop;
    @FXML
    public TextArea taWriteReport;
    @FXML
    public Button btnAttachFile;
    @FXML
    public Button btnSendReport;
    @FXML
    public Label labelSymbolsNeedReport;
    static ObservableList<ProjectModel> projectsData = FXCollections.observableArrayList();
    private static DateTimeFormatter dateFormatNumber = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static DateTimeFormatter dateFormatText = DateTimeFormatter.ofPattern("EEEE", Locale.ENGLISH);
    private static final String ID_COLUMN = "id";
    private static final String AUTHOR_NAME_COLUMN = "authorName";
    private static final String TITLE_COLUMN = "title";
    private static final String DESCRIPTION_COLUMN = "description";
    private ReportServerSessionManager reportServerSessionManager;

    @FXML
    private void initialize() throws IOException {
        reportServerSessionManager = new ReportServerSessionManager();
        initPieChart();
        initTable();
        getTodayDayAndSetInView();
    }

    private void getTodayDayAndSetInView() {
        LocalDate date = LocalDate.now();
        String dayOfWeekText = date.format(dateFormatText);
        String dayOfWeekNumber = date.format(dateFormatNumber);
        labelDayInWord.setText(dayOfWeekText);
        labelDayInNumber.setText(dayOfWeekNumber);
    }

    private void initTable() {
        tcIdProject.setCellValueFactory(new PropertyValueFactory<>(ID_COLUMN));
        tcAuthorName.setCellValueFactory(new PropertyValueFactory<>(AUTHOR_NAME_COLUMN));
        tcTitle.setCellValueFactory(new PropertyValueFactory<>(TITLE_COLUMN));
        tcDescription.setCellValueFactory(new PropertyValueFactory<>(DESCRIPTION_COLUMN));
        setProjectInTable();
    }

    private void setProjectInTable() {
        Set<ProjectModel> projectModelSet = CurrentSessionInfo.getUserActiveProjects();
        if(projectModelSet!=null&&!projectModelSet.isEmpty()){
            projectModelSet.forEach(projectsData::add);
//        for (ProjectModel projectModel : projectModelSet) {
//            projectsData.add(projectModel);
//        }
            tvProjectsTable.setItems(projectsData);
            setOtherProjectInfoInView(projectsData.get(0));
        }
    }
    private void setOtherProjectInfoInView(ProjectModel projectModel) {
        CurrentSessionInfo.setProjectId(projectModel.getId());
            labelNameSales.setText(projectModel.getAuthorName());
            labelNameProject.setText(":" + projectModel.getTitle());
            labelDiscribeProject.setText(projectModel.getDescription());
           if ((projectModel.getStartDate()!=null && projectModel.getEndDate()!=null)){
            labelDateStartProject.setText(projectModel.getStartDate().format(dateFormatNumber));
            labelDeadLineProject.setText(projectModel.getEndDate().format(dateFormatNumber));
        }
    }

    private void initPieChart() {
        ObservableList<PieChart.Data>  pieChartList = FXCollections.observableArrayList(new PieChart.Data("Простой", 7),
                new PieChart.Data("Чистое", 93));
        missPieCharts.setData(pieChartList);
    }

    public void chosenProject(Event event) throws IOException {
        taWriteReport.setText("");
        btnSendReport.setDisable(false);
       taWriteReport.setEditable(true);
        if (tvProjectsTable.getSelectionModel().getSelectedItem()!=null){
            ProjectModel selectProject = tvProjectsTable.getSelectionModel().getSelectedItem();
            setReportInfoInTextAreaAndButton(selectProject);
            setOtherProjectInfoInView(selectProject);
        }

    }

    private void setReportInfoInTextAreaAndButton(ProjectModel projectModel) throws IOException {
        Set<ReportModel> reportModel = reportServerSessionManager.sendProjectDataAndGetReportById(projectModel.getId());
        for (ReportModel model :
                reportModel) {
            if (model.getDate().equals(LocalDate.now())) {
                taWriteReport.setText("You have already saved a report today");
                btnSendReport.setDisable(true);
                taWriteReport.setEditable(false);
            }
        }
    }

    public void saveReportToServer(ActionEvent actionEvent) throws IOException {
        ReportModel reportModel=new ReportModel(CurrentSessionInfo.getTokenModel().getToken(),taWriteReport.getText(),CurrentSessionInfo.getProjectId(),LocalDate.now());
        reportModel.setTitle("kaban gay");
        reportServerSessionManager.saveReportToServer(reportModel);
        btnSendReport.setDisable(true);
        taWriteReport.setEditable(false);
    }
}
