package ua.softgroup.matrix.desktop.controllerjavafx;


import com.sun.org.apache.bcel.internal.generic.FADD;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import ua.softgroup.matrix.desktop.currentsessioninfo.CurrentSessionInfo;
import ua.softgroup.matrix.desktop.sessionmanagers.ReportServerSessionManager;
import ua.softgroup.matrix.server.desktop.model.ProjectModel;
import ua.softgroup.matrix.server.desktop.model.ReportModel;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.EventListener;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * @author Andrii Bei <sg.andriy2@gmail.com>
 */
public class ProjectsLayoutController  {

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
    public Label labelSymbolsNeedsToReport;
    @FXML
    public Label labelCurrentSymbols;
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
        setFocusOnTableView();
        countTextAndSetInView();
        addTextLimiter(taWriteReport,999);
    }

    @FXML
    private void countTextAndSetInView() {
        taWriteReport.textProperty().addListener((observable, oldValue, newValue) -> {
            int size=newValue.length();
            labelCurrentSymbols.setText(String.valueOf(size));
            if (size>=70){
                btnSendReport.setDisable(false);
            }
        });
    }

    private void setFocusOnTableView() throws IOException {
        tvProjectsTable.requestFocus();
        tvProjectsTable.getSelectionModel().select(CurrentSessionInfo.getUserActiveProjects().size() - 1);
        tvProjectsTable.getFocusModel().focus(CurrentSessionInfo.getUserActiveProjects().size() - 1);
        ProjectModel projectModel=tvProjectsTable.getSelectionModel().getSelectedItem();
        setOtherProjectInfoInView(projectModel);
        new Thread(() -> {
            try {
                setReportInfoInTextAreaAndButton(projectModel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
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
        if (projectModelSet != null && !projectModelSet.isEmpty()) {
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
        labelNameProject.setText(projectModel.getTitle());
        labelDiscribeProject.setText(projectModel.getDescription());
        if ((projectModel.getStartDate() != null && projectModel.getEndDate() != null)) {
            labelDateStartProject.setText(projectModel.getStartDate().format(dateFormatNumber));
            labelDeadLineProject.setText(projectModel.getEndDate().format(dateFormatNumber));
        }
    }

    private void initPieChart() {
        ObservableList<PieChart.Data> pieChartList = FXCollections.observableArrayList(new PieChart.Data("Простой", 7),
                new PieChart.Data("Чистое", 93));
        missPieCharts.setData(pieChartList);
    }

    public void chosenProject(Event event) throws IOException {
         MouseEvent mouseEvent=(MouseEvent)event;
        if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
            if(mouseEvent.getClickCount()==2){
           MainLayoutController main=new MainLayoutController();
                main.startReport(labelCurrentSymbols.getScene().getWindow());
            }
        }
        taWriteReport.setText("");
        taWriteReport.setEditable(true);
        if (tvProjectsTable.getSelectionModel().getSelectedItem() != null) {
            ProjectModel selectProject = tvProjectsTable.getSelectionModel().getSelectedItem();
            setReportInfoInTextAreaAndButton(selectProject);
            setOtherProjectInfoInView(selectProject);
        }
    }

    private void setReportInfoInTextAreaAndButton(ProjectModel projectModel) throws IOException {
        Set<ReportModel> reportModel = null;
            reportModel = reportServerSessionManager.sendProjectDataAndGetReportById(projectModel.getId());
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
        ReportModel reportModel = new ReportModel(CurrentSessionInfo.getTokenModel().getToken(), taWriteReport.getText(), CurrentSessionInfo.getProjectId(), LocalDate.now());
        reportModel.setTitle("kaban gay");
        reportServerSessionManager.saveReportToServer(reportModel);
        btnSendReport.setDisable(true);
        taWriteReport.setEditable(false);
    }

    public static void addTextLimiter(final TextArea ta, final int maxLength) {
        ta.textProperty().addListener((ov, oldValue, newValue) -> {
            if (ta.getText().length() > maxLength) {
                String s = ta.getText().substring(0, maxLength);
                ta.setText(s);
            }
        });
    }

}
