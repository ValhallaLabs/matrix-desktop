package ua.softgroup.matrix.desktop.controllerjavafx;


import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import ua.softgroup.matrix.desktop.currentsessioninfo.CurrentSessionInfo;
import ua.softgroup.matrix.desktop.sessionmanagers.ReportServerSessionManager;
import ua.softgroup.matrix.desktop.spykit.timetracker.TimeTracker;
import ua.softgroup.matrix.desktop.view.DoughnutChart;
import ua.softgroup.matrix.server.desktop.model.datamodels.ProjectModel;
import ua.softgroup.matrix.server.desktop.model.datamodels.ReportModel;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
    public Label labelDayInWord;
    @FXML
    public Label labelDayInNumber;
    @FXML
    public Label labelNameProject;
    @FXML
    public Label labelDescribeProject;
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
    @FXML
    public AnchorPane containetForPieChart;
    private static ObservableList<ProjectModel> projectsData = FXCollections.observableArrayList();
    private static DateTimeFormatter dateFormatNumber = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static DateTimeFormatter dateFormatText = DateTimeFormatter.ofPattern("EEEE", Locale.ENGLISH);
    private static final String ID_COLUMN = "id";
    private static final String AUTHOR_NAME_COLUMN = "authorName";
    private static final String TITLE_COLUMN = "title";
    private static final String DESCRIPTION_COLUMN = "description";
    private static final int LIMITER_TEXT_COUNT = 999;
    private static final int MIN_TEXT_FOR_REPORT = 70;
    private static final String ALERT_ERROR_TITLE = "Supervisor";
    private static final String ALERT_CONTENT_TEXT = "Something go wrong .Programs will be close";
    private static final String ALERT_HEADER_TEXT = "Supervisor ERROR";

    private ReportServerSessionManager reportServerSessionManager;
    private File attachFile;
    private long timeTodayMinutes;
    private Timeline timeTimer;
    private TimeTracker timeTracker;
    private DoughnutChart doughnutChart;
    private Label labelIdle;

    /**
     * After Load/Parsing fxml call this method
     * Create {@link ReportServerSessionManager} and TimeTimer
     *
     * @throws IOException
     */
    @FXML
    private void initialize() throws IOException {
        reportServerSessionManager = new ReportServerSessionManager();
        timeTimer = new Timeline();
        initPieChart();
        initProjectInTable();
        getTodayDayAndSetInView();
        setFocusOnTableView();
        countTextAndSetInView();
        addTextLimiter(taWriteReport, LIMITER_TEXT_COUNT);
    }

    /**
     * Hears when text input in TextArea and if this text count >= {@value MIN_TEXT_FOR_REPORT}
     * button for send report became active
     */
    @FXML
    private void countTextAndSetInView() {
        taWriteReport.textProperty().addListener((observable, oldValue, newValue) -> {
            int size = newValue.length();
            labelCurrentSymbols.setText(String.valueOf(size));
            if (size >= MIN_TEXT_FOR_REPORT) {
                btnSendReport.setDisable(false);
            } else btnSendReport.setDisable(true);
        });
    }

    /**
     * At start project window select last item in Table View
     *
     * @throws IOException
     */
    private void setFocusOnTableView() throws IOException {
        tvProjectsTable.requestFocus();
        tvProjectsTable.getSelectionModel().select(0);
        tvProjectsTable.getFocusModel().focus(0);
        ProjectModel projectModel = tvProjectsTable.getSelectionModel().getSelectedItem();
        setOtherProjectInfoInView(projectModel);
        new Thread(() -> {
            try {
                setReportInfoInTextAreaAndButton(projectModel);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Get system current date and display this data in label view
     */
    private void getTodayDayAndSetInView() {
        LocalDate date = LocalDate.now();
        String dayOfWeekText = date.format(dateFormatText);
        String dayOfWeekNumber = date.format(dateFormatNumber);
        labelDayInWord.setText(dayOfWeekText);
        labelDayInNumber.setText(dayOfWeekNumber);
    }

    /**
     * Set connect table column with {@link ProjectModel} for future pull date in this field
     */
    private void initProjectInTable() {
        tcIdProject.setCellValueFactory(new PropertyValueFactory<>(ID_COLUMN));
        tcAuthorName.setCellValueFactory(new PropertyValueFactory<>(AUTHOR_NAME_COLUMN));
        tcTitle.setCellValueFactory(new PropertyValueFactory<>(TITLE_COLUMN));
        tcDescription.setCellValueFactory(new PropertyValueFactory<>(DESCRIPTION_COLUMN));
        setProjectInTable();
    }

    /**
     * Get in{@link CurrentSessionInfo} Set of  all active project and set their in table view
     * and sort this set by newest project id
     */
    @SuppressWarnings("unchecked")
    private void setProjectInTable() {
        Set<ProjectModel> projectModelSet = CurrentSessionInfo.getProjectModels();
        if (projectModelSet != null && !projectModelSet.isEmpty()) {
            projectModelSet.forEach(projectsData::add);
//        for (ProjectModel projectModel : projectModelSet) {
//            projectsData.add(projectModel);
//        }
            tvProjectsTable.setItems(projectsData);
            tvProjectsTable.getSortOrder().setAll(tcIdProject);
        }
    }

    /**
     * Get from current project information's and set their in label view element
     *
     * @param projectModel current project what user choose in table view
     */
    private void setOtherProjectInfoInView(ProjectModel projectModel) {
        CurrentSessionInfo.setProjectId(projectModel.getId());
        labelNameProject.setText(projectModel.getTitle());
        labelDescribeProject.setText(projectModel.getDescription());
        if ((projectModel.getStartDate() != null && projectModel.getEndDate() != null)) {
            labelDateStartProject.setText(projectModel.getStartDate().format(dateFormatNumber));
            labelDeadLineProject.setText(projectModel.getEndDate().format(dateFormatNumber));
        } else {
            labelDateStartProject.setText("Unknown");
            labelDeadLineProject.setText("Unknown");
        }
    }

    /**
     * Get DownTime and CleanTime and set this information in Pie Chart
     */
    private void initPieChart() {
        createLabelForDisplayIdleTime();
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(new PieChart.Data("Down Time",88),
                new PieChart.Data("Clean Time", 12));
        doughnutChart = new DoughnutChart(pieChartData);
        createPieChart();
        containetForPieChart.getChildren().addAll(doughnutChart,labelIdle);

    }

    private void createLabelForDisplayIdleTime() {
        labelIdle = new Label("88%");
        labelIdle.setMinWidth(60);
        labelIdle.setMinHeight(20);
        labelIdle.setStyle("-fx-font-weight: bold");
        labelIdle.setFont(new Font(16));
        labelIdle.setPadding(new Insets(87, 0, 50, 69));
    }

    private void createPieChart() {
        doughnutChart.setMaxWidth(180);
        doughnutChart.setMaxHeight(180);
        doughnutChart.setTitle("Idle Time");
        doughnutChart.setLabelsVisible(false);
        doughnutChart.setPadding(new Insets(10, 50, 15, 13));
    }

    /**
     * Hears when user click on table view select project and set TextArea Editable
     *
     * @param event callback click on table view
     * @throws IOException
     */
    public void chosenProject(Event event) throws IOException, ClassNotFoundException {
        MouseEvent mouseEvent = (MouseEvent) event;
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            if (mouseEvent.getClickCount() == 2) {
                openReportWindowOnTwoMouseClick(event);
            } else {
                taWriteReport.setText("");
                taWriteReport.setEditable(true);
                if (tvProjectsTable.getSelectionModel().getSelectedItem() != null) {
                    ProjectModel selectProject = tvProjectsTable.getSelectionModel().getSelectedItem();
                    setReportInfoInTextAreaAndButton(selectProject);
                    setOtherProjectInfoInView(selectProject);
                }
            }
        }

    }

    /**
     * Hears fast two click on table view and open report window on what project user click
     *
     * @param event callback click on table view
     */
    private void openReportWindowOnTwoMouseClick(Event event) {
        MainLayoutController main = new MainLayoutController();
        main.startReport(labelCurrentSymbols.getScene().getWindow());
    }

    /**
     * Gets all reports for chosen project and if today user already saved report
     * displays this information in TextArea
     *
     * @param projectModel current project what user choose in table view
     * @throws IOException
     */
    private void setReportInfoInTextAreaAndButton(ProjectModel projectModel) throws IOException, ClassNotFoundException {
        Set<ReportModel> reportModel = null;
        reportModel = reportServerSessionManager.sendProjectDataAndGetReportById(projectModel.getId());
        //TODO: add check on null
        for (ReportModel model :
                reportModel) {
            if (model.getDate().equals(LocalDate.now())) {
                taWriteReport.setText("You have already saved a report today");
                btnSendReport.setDisable(true);
                taWriteReport.setEditable(false);
            }
        }
    }

    /**
     * Hears when user click on button and send information about report to {@link ReportServerSessionManager}
     *
     * @param actionEvent callback click on button
     * @throws IOException
     */
    public void sendReport(ActionEvent actionEvent) throws IOException, ClassNotFoundException {
        byte[] attachFile = new byte[0];
//        if (this.attachFile.exists() && this.attachFile != null) {
//            attachFile = Files.readAllBytes(this.attachFile.toPath());
//            System.out.println(Arrays.toString(attachFile));
//        }
        System.out.println(Arrays.toString(attachFile));
        ReportModel reportModel = new ReportModel(taWriteReport.getText(), LocalDate.now(), attachFile);
        reportServerSessionManager.saveOrChangeReportOnServer(reportModel);
        btnSendReport.setDisable(true);
        taWriteReport.setEditable(false);
    }

    /**
     * Limit of amount on entry text
     *
     * @param ta        TextField in what input text
     * @param maxLength int number of max text amount
     */
    private static void addTextLimiter(final TextArea ta, final int maxLength) {
        ta.textProperty().addListener((ov, oldValue, newValue) -> {
            if (ta.getText().length() > maxLength) {
                String s = ta.getText().substring(0, maxLength);
                ta.setText(s);
            }
        });
    }

    /**
     * Hears when user click on button and attach chosen image
     *
     * @param actionEvent callback click on button
     */
    public void attachFile(ActionEvent actionEvent) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif")
        );
        attachFile = fileChooser.showOpenDialog(labelCurrentSymbols.getScene().getWindow());
        if (attachFile != null) {
            System.out.println("hFile attach");
        }
    }

    /**
     * Hears when user click on button and create Timeline with KeyFrame duration every 1 minutes
     * and start play timer
     *
     * @param actionEvent callback click on button
     * @throws InterruptedException
     */
    public void startWork(ActionEvent actionEvent) throws InterruptedException {
        timeTracker = new TimeTracker(this, CurrentSessionInfo.getProjectId());
        timeTracker.turnOn();
        timeTimer.setCycleCount(Timeline.INDEFINITE);
        if (timeTimer != null) {
            timeTimer.stop();
        }
        KeyFrame frame = new KeyFrame(Duration.minutes(1), event -> {
            calculateTimeAndSetInView();
        });

        timeTimer.getKeyFrames().add(frame);
        timeTimer.playFromStart();
        buttonConditionAtTimerOn();
    }

    /**
     * Hears when user click on button and stop timer
     *
     * @param actionEvent callback click on button
     */
    public void stopWork(ActionEvent actionEvent) {
        timeTimer.stop();
        buttonConditionAtTimerOff();
        if (timeTracker != null) {
            timeTracker.turnOff();
        }
    }

    /**
     * Increment minutes and set this info into label view
     */
    private void calculateTimeAndSetInView() {
        timeTodayMinutes++;
        labelTodayTotalTime.setText(String.valueOf(timeTodayMinutes / 60 + "h " + timeTodayMinutes % 60 + "m"));
    }

    /**
     * Set possibility click on stop button and disable start button
     */
    private void buttonConditionAtTimerOn() {
        btnStart.setDisable(true);
        btnStop.setDisable(false);
    }

    /**
     * Set possibility click on start button and disable stop button
     */
    private void buttonConditionAtTimerOff() {
        btnStart.setDisable(false);
        btnStop.setDisable(true);
    }

    /**
     * When something go wrong , create alert with message to user
     * and then click on button close programme
     */
    public void tellUserAboutCrash() {
        Alert mainAlert = new Alert(Alert.AlertType.INFORMATION);
        mainAlert.setTitle(ALERT_ERROR_TITLE);
        mainAlert.setHeaderText(ALERT_HEADER_TEXT);
        mainAlert.setContentText(ALERT_CONTENT_TEXT);
        mainAlert.initStyle(StageStyle.UTILITY);
        mainAlert.setOnCloseRequest(event -> Platform.exit());
        Optional<ButtonType> result = mainAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Platform.exit();
        }
    }

}
