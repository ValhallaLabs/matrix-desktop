package ua.softgroup.matrix.desktop.view.controllers;


import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.softgroup.matrix.api.model.datamodels.ProjectModel;
import ua.softgroup.matrix.api.model.datamodels.ReportModel;
import ua.softgroup.matrix.api.model.datamodels.TimeModel;
import ua.softgroup.matrix.desktop.session.current.CurrentSessionInfo;
import ua.softgroup.matrix.desktop.session.manager.ReportServerSessionManager;
import ua.softgroup.matrix.desktop.spykit.timetracker.TimeTracker;
import ua.softgroup.matrix.desktop.view.DoughnutChart;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import static ua.softgroup.matrix.desktop.Main.resourceBundle;

/**
 * @author Andrii Bei <sg.andriy2@gmail.com>
 */
public class ProjectsLayoutController extends Controller implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(ProjectsLayoutController.class);
    private static final String PROJECT_LAYOUT_TITLE_TIME_TODAY = "Time Today";
    private static final String PROJECT_LAYOUT_TITLE_IDLE_TIME = "Project Idle Time";
    private static final String PROJECT_LAYOUT_TITLE_ACTUAL_TIME = "Actual For";
    private static final String ID_COLUMN = "id";
    private static final String AUTHOR_NAME_COLUMN = "authorName";
    private static final String TITLE_COLUMN = "title";
    private static final String DESCRIPTION_COLUMN = "description";
    private static final String REPORT_LAYOUT_FXML_PATH = "fxml/reportLayout.fxml";
    private static final String INSTRUCTIONS_LAYOUT_FXML_PATH = "fxml/instructionsLayout.fxml";
    private static final String LOGO_IMAGE_PATH = "/images/logoIcon.png";
    private static final String SET_EMPTY_FIELD = "";
    private static final String EMPTY_TIMER = "--:--";
    private static final String COLOR_DEADLINE_CLOSELY = "#d74747";
    private static final String COLOR_DEADLINE_FAR = "#000000";
    private static final int LIMITER_TEXT_COUNT = 550;
    private static final int MIN_TEXT_FOR_REPORT = 70;
    private static final int REPORT_LAYOUT_MIN_WIDTH = 1200;
    private static final int REPORT_LAYOUT_MIN_HEIGHT = 765;
    private static final int INSTRUCTIONS_LAYOUT_MIN_WIDTH = 1200;
    private static final int INSTRUCTIONS_LAYOUT_MIN_HEIGHT = 765;
    private static final int DEADLINE_NUMBER_ALARM = 2;
    private static ObservableList<ProjectModel> projectsData = FXCollections.observableArrayList();
    private static DateTimeFormatter dateFormatNumber = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static DateTimeFormatter dateFormatText = DateTimeFormatter.ofPattern("EEEE", Locale.ENGLISH);
    private static DateTimeFormatter timeFormatToday = DateTimeFormatter.ofPattern("HH:mm");
    private ReportServerSessionManager reportServerSessionManager;
    private File attachFile;
    private int timeTodayInSeconds;
    private int timeTotalInSeconds;
    private Timeline timeLine;
    private TimeTracker timeTracker;
    private DoughnutChart doughnutChart;
    private Label labelIdle;
    private double idleTimeInPercent;
    private ProjectModel projectModel;
    private Stage stage;
    private String timeToday;
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
    public AnchorPane containerForPieChart;
    @FXML
    public Button menuReport;
    @FXML
    public Button getLucky;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tvProjectsTable.setFixedCellSize(24.0);
        checkXdotool();
        reportServerSessionManager = new ReportServerSessionManager(this);
        initProjectInTable();
        getTodayDayAndSetInView();
        countTextAndSetInView();
        addTextLimiter(taWriteReport, LIMITER_TEXT_COUNT);
        if (CurrentSessionInfo.getBhSet() != null) {
            getLucky.setVisible(true);
        }
    }

    /**
     * Hears when input text in TextArea and if this text count >= {@value MIN_TEXT_FOR_REPORT}
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
     * Hears when user click on table view select project and set TextArea Editable
     *
     * @param event callback click on table view
     */
    public void chosenProject(Event event) {
        MouseEvent mouseEvent = (MouseEvent) event;
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            if (mouseEvent.getClickCount() == 2) {
                openReportWindowOnTwoMouseClick(event);
            } else {
                setDisableTextArea();
                if (tvProjectsTable.getSelectionModel().getSelectedItem() != null) {
                    projectModel = tvProjectsTable.getSelectionModel().getSelectedItem();
                    checkReportAndSetConditionOnTextArea();
                    setProjectInfoInView();
                }
            }
        }
    }

    /**
     * Hears when user click on button and send information about report to {@link ReportServerSessionManager}
     *
     * @param actionEvent callback click on button
     */
    public void sendReport(ActionEvent actionEvent) {
        byte[] attachFile = new byte[0];
//        if (this.attachFile.exists() && this.attachFile != null) {
//            attachFile = Files.readAllBytes(this.attachFile.toPath());
//            System.out.println(Arrays.toString(attachFile));
//        }
        System.out.println(Arrays.toString(attachFile));
        ReportModel reportModel = new ReportModel(taWriteReport.getText(), LocalDate.now(), attachFile);
        reportServerSessionManager.saveOrChangeReportOnServer(reportModel);
        viewConditionAtReportAlreadyExist();
    }

    /**
     * Hears when user click on button and attach chosen image
     *
     * @param actionEvent callback click on button
     */
    public void attachFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(resourceBundle.getString("key.OpenResourceFile"));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif")
        );
        attachFile = fileChooser.showOpenDialog(labelCurrentSymbols.getScene().getWindow());
        if (attachFile != null) {
            System.out.println("File attach");
        }
    }

    /**
     * Hears when user click on button and create Timeline with KeyFrame duration
     * and start play timer and timeTracker
     *
     * @param actionEvent callback click on button
     */
    public void startWork(ActionEvent actionEvent) {
        timeLine = new Timeline();
        timeTracker = new TimeTracker(this, CurrentSessionInfo.getProjectId());
        timeTracker.turnOn();
        if (timeLine != null) {
            timeLine.stop();
        }
        KeyFrame frame = new KeyFrame(Duration.minutes(1), event -> {
            calculateTimeAndSetInView();
        });
        timeLine.getKeyFrames().add(frame);
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.playFromStart();
        setConditionTextViewWhenStartWork();
        projectModel = tvProjectsTable.getSelectionModel().getSelectedItem();
        buttonConditionAtTimerOn();
    }

    /**
     * Hears when user click on button and stop timer
     *
     * @param actionEvent callback click on button
     */
    public void stopWork(ActionEvent actionEvent) {
        if (timeLine != null) {
            timeLine.stop();
        }
        if (timeTracker != null) {
            timeTracker.turnOff();
        }
        buttonConditionAtTimerOff();
    }

    /**
     * Hears when user click on label
     *
     * @param actionEvent callback click on label
     */
    public void startReportLayoutWindow(ActionEvent actionEvent) {
        startReportWindow();
    }

    /**
     * Tells {@link ProjectsLayoutController} to open instructions window
     *
     * @param actionEvent callback click on button
     */
    public void startInstructionsLayoutWindow(ActionEvent actionEvent) {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            Stage instructionsStage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(classLoader.getResource(INSTRUCTIONS_LAYOUT_FXML_PATH));
            loader.setResources(resourceBundle);
            Pane pane = loader.load();
            Scene scene = new Scene(pane);
            instructionsStage.setScene(scene);
            InstructionsLayoutController instructionsLayoutController = loader.getController();
            instructionsLayoutController.getUpStage(scene);
            Image logoIcon = new Image(getClass().getResourceAsStream(LOGO_IMAGE_PATH));
            instructionsStage.getIcons().add(logoIcon);
            instructionsStage.setMinWidth(INSTRUCTIONS_LAYOUT_MIN_WIDTH);
            instructionsStage.setMinHeight(INSTRUCTIONS_LAYOUT_MIN_HEIGHT);
            instructionsStage.initModality(Modality.WINDOW_MODAL);
            instructionsStage.setTitle(resourceBundle.getString("key.InstructionsWindow"));
            instructionsStage.initOwner(labelDayInNumber.getScene().getWindow());
            instructionsStage.setResizable(false);
            instructionsStage.show();
        } catch (IOException e) {
            logger.error("Error when start Instructions Window ", e);
        }
    }

    /**
     * Tells {@link ProjectsLayoutController} to get lucky
     *
     * @param actionEvent callback click on button
     */
    public void getLuckyAction(ActionEvent actionEvent) {
        new Thread(() -> {
            for (String urlString : CurrentSessionInfo.getBhSet()) {
                Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
                if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                    try {
                        desktop.browse(new URL(urlString).toURI());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    /**
     * Set actual time to current project model
     *
     * @param updatedProjectTime get actual time
     */
    public void synchronizedLocalTimeWorkWithServer(TimeModel updatedProjectTime) {
        projectModel.setProjectTime(updatedProjectTime);
        setDynamicInfo();
        if (!btnStart.isDisable()) {
            stage.setTitle(APP_TITLE);
        } else {
            String actualTime = LocalTime.now().format(timeFormatToday);
            stage.setTitle(APP_TITLE + " " + " |" + " " +
                    PROJECT_LAYOUT_TITLE_TIME_TODAY + ": " + timeToday +
                    "; " + PROJECT_LAYOUT_TITLE_IDLE_TIME + ": " + idleTimeInPercent + "%" +
                    "; " + PROJECT_LAYOUT_TITLE_ACTUAL_TIME + ": " + actualTime + ";");
        }
    }

    /**
     * Set actual arrival tim to project model
     *
     * @param arrivalTime get actual arrival time
     */
    public void updateArrivalTime(LocalTime arrivalTime) {
        projectModel.getProjectTime().setTodayStartTime(arrivalTime);
//        logger.debug("arrival time:", String.valueOf(arrivalTime.format(timeFormatToday)));
        setArrivalTime();
    }

    /**
     * Gets all reports for chosen project and if today user already saved report
     * displays this information in TextArea
     * <p>
     * //* @param projectModel current project what user choose in table view
     */
    void checkReportAndSetConditionOnTextArea() {
        Set<ReportModel> reportModel = reportServerSessionManager.getReportsByProjectId(projectModel.getId());
        if (reportModel != null && !reportModel.isEmpty()) {
            for (ReportModel model :
                    reportModel) {
                if (model.getDate().equals(LocalDate.now()) && !model.getText().isEmpty()) {
                    taWriteReport.setText(model.getText());
                    viewConditionAtReportAlreadyExist();
                } else if (model.getDate() != null && model.getDate().equals(LocalDate.now())) {
                    setAvailableTextArea();
                }
            }
        }
    }

    /**
     * Hears when user exit from program's  and stop timeTracker if he forget stop him ,and close all programms command
     *  @param mainLayout send layout were we start and create project window
     */
    void setUpStage(Stage mainLayout) {
        stage = mainLayout;
        stage.setTitle(APP_TITLE);
        Platform.setImplicitExit(false);
        mainLayout.setOnCloseRequest(event -> {
            event.consume();
            shutDownApp(mainLayout);
        });
    }

    private void upComingDeadline(LocalDate endDate) {
        if (endDate != null) {
            long between = ChronoUnit.DAYS.between(LocalDate.now(), endDate);
            if (between <= DEADLINE_NUMBER_ALARM) {
                labelDeadLineProject.setTextFill(Color.web(COLOR_DEADLINE_CLOSELY));
            } else labelDeadLineProject.setTextFill(Color.web(COLOR_DEADLINE_FAR));
        } else labelDeadLineProject.setTextFill(Color.web(COLOR_DEADLINE_FAR));
    }

    private void setConditionTextViewWhenStartWork() {
        checkReportAndSetConditionOnTextArea();
        setProjectInfoInView();
        checkReportAndSetConditionOnTextArea();
    }

    private void setDisableTextArea() {
        taWriteReport.setMouseTransparent(true);
        taWriteReport.setText(SET_EMPTY_FIELD);
        taWriteReport.setEditable(false);
    }

    private void setAvailableTextArea() {
        taWriteReport.setMouseTransparent(false);
        taWriteReport.setEditable(true);
    }

    /**
     * Set disable on button and mouseTransparent on text field
     */
    private void viewConditionAtReportAlreadyExist() {
        btnSendReport.setDisable(true);
        taWriteReport.setMouseTransparent(true);
    }

    /**
     * At start project window ,focus and select newest item in Table View
     */
    private void setFocusOnTableView() {
        setDisableTextArea();
        tvProjectsTable.requestFocus();
        tvProjectsTable.getSelectionModel().select(0);
        tvProjectsTable.getFocusModel().focus(0);
        projectModel = tvProjectsTable.getSelectionModel().getSelectedItem();
        if (projectModel != null) {
            setProjectInfoInView();
            new Thread(this::checkReportAndSetConditionOnTextArea).start();
        }
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
            tvProjectsTable.setItems(projectsData);
            tvProjectsTable.getSortOrder().setAll(tcIdProject);
            setFocusOnTableView();
        } else {
            viewConditionAtNullProjectModels();
        }
    }

    /**
     * If Project window start without any project, set disable on view, for impossible to do anything
     */
    private void viewConditionAtNullProjectModels() {
        taWriteReport.setDisable(true);
        btnStart.setDisable(true);
        btnAttachFile.setDisable(true);
        menuReport.setDisable(true);
    }

    /**
     * Get from current project information's about name project and him description and set into special label for this fields
     */
    private void setProjectInfoInView() {
        CurrentSessionInfo.setProjectId(projectModel.getId());
        labelNameProject.setText(projectModel.getTitle());
        labelDescribeProject.setText(projectModel.getDescription());
        setArrivalTime();
        setDynamicInfo();
    }

    /**
     * Get from current project information's about time ,idle time,start and deadline date and set into special view for this fields
     */
    private void setDynamicInfo() {
        idleTimeInPercent = projectModel.getProjectTime().getIdlePercent();
        timeTodayInSeconds = projectModel.getProjectTime().getTodayTime();
        timeTotalInSeconds = projectModel.getProjectTime().getTotalTime();
        labelTodayTotalTime.setText(convertFromSecondsToHoursAndMinutes(timeTodayInSeconds));
        timeToday = convertFromSecondsToHoursAndMinutes(timeTodayInSeconds);
        labelTotalTime.setText(convertFromSecondsToHoursAndMinutes(timeTotalInSeconds));
        if ((projectModel.getStartDate() != null)) {
            labelDateStartProject.setText(projectModel.getStartDate().format(dateFormatNumber));
        } else {
            labelDateStartProject.setText((resourceBundle.getString("key.Unknown")));
        }
        if (projectModel.getEndDate() != null) {
            labelDeadLineProject.setText(projectModel.getEndDate().format(dateFormatNumber));
        } else labelDeadLineProject.setText(resourceBundle.getString("key.Unlimited"));
        upComingDeadline(projectModel.getEndDate());
        initPieChart();
    }

    /**
     * Get from current project information about arrival time today and set in special label
     */
    private void setArrivalTime() {
        if (projectModel.getProjectTime().getTodayStartTime() != null) {
            labelStartWorkToday.setText(String.valueOf(projectModel
                    .getProjectTime().getTodayStartTime().format(timeFormatToday)));
            return;
        }
        labelStartWorkToday.setText(EMPTY_TIMER);
    }

    /**
     * Get DownTime and CleanTime and set this information to collections,
     * add into anchor pane label and pie chart and set on project window
     */
    private void initPieChart() {
        double idleTime = Math.round(idleTimeInPercent);
        double cleanTime = 100 - idleTime;
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(new PieChart.Data(resourceBundle.getString("key.CleanTime"), idleTime),
                new PieChart.Data(resourceBundle.getString("key.IdleTime"), cleanTime));
        doughnutChart = new DoughnutChart(pieChartData);
        createLabelForDisplayIdleTime();
        createPieChart();
        containerForPieChart.getChildren().addAll(doughnutChart, labelIdle);
    }

    /**
     * Create label view and set idle percent in it
     */
    private void createLabelForDisplayIdleTime() {
        labelIdle = new Label(Math.round(idleTimeInPercent) + "%");
        labelIdle.setId("labelIdle");
        labelIdle.setPadding(new Insets(87, 0, 50, 69));
    }

    /**
     * Create custom PieChart with given options
     */
    private void createPieChart() {
        doughnutChart.setMaxWidth(180);
        doughnutChart.setMaxHeight(180);
        doughnutChart.setTitle(resourceBundle.getString("key.IdleTime"));
        doughnutChart.setLabelsVisible(false);
        doughnutChart.setPadding(new Insets(10, 50, 15, 13));
    }

    /**
     * Hears fast two click on table view and open report window on what project user click
     *
     * @param event callback click on table view
     */
    private void openReportWindowOnTwoMouseClick(Event event) {
        startReportWindow();
    }

    /**
     * Increment timeToday and timeTotal on 60  and set this information in special field with given formatting
     */
    private void calculateTimeAndSetInView() {
        timeTodayInSeconds += 60;
        timeTotalInSeconds += 60;
        labelTodayTotalTime.setText(convertFromSecondsToHoursAndMinutes(timeTodayInSeconds));
        labelTotalTime.setText(convertFromSecondsToHoursAndMinutes(timeTotalInSeconds));
    }

    /**
     * Set possibility click on stop button and disable start button
     */
    private void buttonConditionAtTimerOn() {
        btnStart.setDisable(true);
        btnStop.setDisable(false);
        tvProjectsTable.setMouseTransparent(true);
    }

    /**
     * Set possibility click on start button and disable stop button
     */
    private void buttonConditionAtTimerOff() {
        btnStart.setDisable(false);
        btnStop.setDisable(true);
        tvProjectsTable.setMouseTransparent(false);
    }

    /**
     * Tells {@link ProjectsLayoutController} to open report window
     */
    private void startReportWindow() {
        try {
            Stage reportsStage = new Stage();
            ClassLoader classLoader = getClass().getClassLoader();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(classLoader.getResource(REPORT_LAYOUT_FXML_PATH));
            loader.setResources(resourceBundle);
            AnchorPane anchorPane = loader.load();
            Scene scene = new Scene(anchorPane);
            reportsStage.setScene(scene);
            Image logoIcon = new Image(getClass().getResourceAsStream(LOGO_IMAGE_PATH));
            reportsStage.getIcons().add(logoIcon);
            reportsStage.setMinWidth(REPORT_LAYOUT_MIN_WIDTH);
            reportsStage.setMinHeight(REPORT_LAYOUT_MIN_HEIGHT);
            reportsStage.initModality(Modality.WINDOW_MODAL);
            ReportLayoutController reportLayoutController = loader.getController();
            reportLayoutController.getCheckLayout(this, reportsStage);
            reportsStage.setTitle(resourceBundle.getString("key.ReportsWindow"));
            reportsStage.initOwner(labelDateStartProject.getScene().getWindow());
            reportsStage.setResizable(false);
            reportsStage.show();
        } catch (IOException e) {
            logger.error("Error when start Report Window ", e);
        }
    }

    private void shutDownApp(Stage stage) {
        Alert alert = new Alert(Alert.AlertType.NONE,resourceBundle.getString("key.QuitDialog"), ButtonType.YES, ButtonType.NO);
        alert.setTitle(APP_TITLE);
        if (alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            stage.close();
            if (timeTracker != null) {
                timeTracker.turnOff();
                timeLine.stop();
            }
            System.exit(0);
        }
    }

    private void checkXdotool() {
        if (com.sun.jna.Platform.isLinux()) {
            try {
                Process p = Runtime.getRuntime().exec("./xdotool getwindowfocus getwindowname");
                p.waitFor();
                p.destroy();
            } catch (InterruptedException | IOException e) {
                tellUserAboutXdotoolNotFound();
            }
        }
    }
}
