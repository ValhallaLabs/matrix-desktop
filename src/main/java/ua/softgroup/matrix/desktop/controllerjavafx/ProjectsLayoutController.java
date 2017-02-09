package ua.softgroup.matrix.desktop.controllerjavafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ua.softgroup.matrix.server.desktop.model.ProjectModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by AndriiBei on 09.02.2017.
 */
public class ProjectsLayoutController {

    private ObservableList<PieChart.Data> pieChartList;
    static ObservableList<ProjectModel> projectsData = FXCollections.observableArrayList();


    @FXML
    public TableView<ProjectModel> tvProjectsTable;
    @FXML
    public TableColumn<ProjectModel,Long> tcIdProject;
    @FXML
    public TableColumn<ProjectModel,String> tcAuthorName;
    @FXML
    public TableColumn<ProjectModel,String> tcTitle;
    @FXML
    public TableColumn<ProjectModel,String> tcDescription;
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

    @FXML
    private void initialize() {
        initTable();
        initColumn();
        getTodayDayAndSetInView();
    }

    private void getTodayDayAndSetInView() {
        Date date = new Date();
        SimpleDateFormat dateFormatText = new SimpleDateFormat("EEEEE", Locale.ENGLISH);
        String dayOfWeekText = dateFormatText.format(date);
        SimpleDateFormat dateFormatNumber=new SimpleDateFormat("d.MM.yyyy");
        String dayOfWeekNumber=dateFormatNumber.format(date);
        labelDayInWord.setText(dayOfWeekText);
        labelDayInNumber.setText(dayOfWeekNumber);
    }

    private void initColumn() {
        tcIdProject.setCellValueFactory(new PropertyValueFactory<>("id"));
        tcAuthorName.setCellValueFactory(new PropertyValueFactory<>("authorName"));
        tcTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        tcDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        projectsData.add(new ProjectModel(1,"1sdsd","1sdsd","1sdsdsds"));
        projectsData.add(new ProjectModel(2,"2sdsd","2sdsd","2sdsdsds"));
        projectsData.add(new ProjectModel(3,"3sdsd","3sdsd","3sdsdsds"));
        projectsData.add(new ProjectModel(4,"4sdsd","4sdsd","4sdsdsds"));
        tvProjectsTable.setItems(projectsData);
    }

    private void initTable() {
        pieChartList = FXCollections.observableArrayList(new PieChart.Data("Простой", 7),
                new PieChart.Data("Чистое", 93));
        missPieCharts.setData(pieChartList);
    }
}
