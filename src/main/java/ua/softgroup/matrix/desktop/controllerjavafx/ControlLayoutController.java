package ua.softgroup.matrix.desktop.controllerjavafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import ua.softgroup.matrix.desktop.api.ControlPanelAPI;
import ua.softgroup.matrix.desktop.model.DayJson;
import ua.softgroup.matrix.desktop.model.ReportControlModel;
import ua.softgroup.matrix.desktop.model.UserProfile;
import ua.softgroup.matrix.desktop.model.WorkPeriod;
import ua.softgroup.matrix.desktop.model.localModel.RequestControl;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


/**
 * @author Andrii Bei <sg.andriy2@gmail.com>
 */
public class ControlLayoutController implements Initializable {
    @FXML
    public ListView listViewReportDetails;
    @FXML
    public ListView<UserProfile> listViewAllUsers;
    @FXML
    public DatePicker calendarFromDate;
    @FXML
    public DatePicker calendarToDate;
    private static final String BASE_URL = "http://127.0.0.1:8094/api/v2/";
    private static final String REPORT_TEXT = "Report text:";
    private static final String WORK_PERIOD = "Work period:";
    private ObservableList<RequestControl> observableListUserStatistics = FXCollections.observableArrayList();
    private ObservableList<UserProfile> observableListAllUsers = FXCollections.observableArrayList();
    private List<RequestControl> listUserRequestControls = new ArrayList<>();
    private ControlPanelAPI controlPanelApi;
    private UserProfile userProfile;
    private static final Logger logger = LoggerFactory.getLogger(ControlLayoutController.class);

    @SuppressWarnings("unchecked")
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (networkIsAvailable()) {
            createRetrofit();
            initializeAllUsers();
            setValueInDatePicker();
        }
    }

    private static boolean networkIsAvailable() {
        try {
            final URL url = new URL(BASE_URL);
            final URLConnection connection = url.openConnection();
            connection.connect();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void createRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(JacksonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build();
        controlPanelApi = retrofit.create(ControlPanelAPI.class);
    }

    private void setValueInDatePicker() {
        calendarFromDate.setValue(LocalDate.now().minusMonths(1));
        calendarToDate.setValue(LocalDate.now());
    }

    private void initializeAllUsers() {
        if (getAllUsers() != null) {
            observableListAllUsers.addAll(getAllUsers());
        }
        listViewAllUsers.setItems(observableListAllUsers);
    }

    private List<UserProfile> getAllUsers() {
        Call<List<UserProfile>> responseUserProfile = controlPanelApi.loadAllUsers();
        try {
            return responseUserProfile.execute().body();
        } catch (IOException e) {
            logger.debug("Error at response All Users" + e);
        }
        return null;
    }

    public void chooseUser(Event event) {
        if (listViewAllUsers.getSelectionModel().getSelectedItem() != null) {
            userProfile = listViewAllUsers.getSelectionModel().getSelectedItem();
        }
    }

    @SuppressWarnings("unchecked")
    public void show(Event event) {
        refreshStatisticsLists();
        getDataFromServerToList();
        if (listUserRequestControls != null && !listUserRequestControls.isEmpty()) {
            for (RequestControl requstControl : listUserRequestControls) {
                TreeSet<WorkPeriod> workPeriod = new TreeSet<>(this::compareStartTime);
                workPeriod.addAll(requstControl.getWorkPeriod());
                if (requstControl.getReportText() != null && !requstControl.getReportText().isEmpty()) {
                    requstControl.setReportText(REPORT_TEXT + "\n" + requstControl.getReportText() + "\n" + "\n" + WORK_PERIOD + "\n" + workPeriod);
                } else requstControl.setReportText(WORK_PERIOD + "\n" + workPeriod);
                observableListUserStatistics.add(requstControl);
            }
        }
        listViewReportDetails.setItems(observableListUserStatistics);
        listViewReportDetails.setCellFactory(new Callback<ListView<RequestControl>, ListCell<RequestControl>>() {
            @Override
            public ListCell<RequestControl> call(ListView<RequestControl> param) {
                return new ControlListViewCell();
            }
        });
    }

    private void getDataFromServerToList() {
        if (getDataFromControlPanel() != null) {
            for (ReportControlModel reportControlModel : getDataFromControlPanel()) {
                for (DayJson dayJson : reportControlModel.getWorkDays()) {
                    listUserRequestControls.add(new RequestControl(reportControlModel.getTotalWorkSeconds() / 60, reportControlModel.getTotalIdleSeconds() / 60, Math.round(reportControlModel.getTotalIdlePercentage()),
                            dayJson.getId(), dayJson.getDate(), dayJson.getStart(), dayJson.getEnd(), dayJson.getWorkSeconds() / 60, dayJson.getIdleSeconds() / 60, Math.round(dayJson.getIdlePercentage())
                            , dayJson.isChecked(), dayJson.getCheckerId(), dayJson.getCoefficient(), dayJson.getReportText(), dayJson.getRate(),
                            dayJson.getCurrencyId(), dayJson.getWorkPeriods()));
                }
            }
        }
    }

    private void refreshStatisticsLists() {
        listUserRequestControls.clear();
        observableListUserStatistics.clear();
    }

    private List<ReportControlModel> getDataFromControlPanel() {
        if (userProfile != null && userProfile.getId() != null && calendarFromDate.getValue() != null && calendarToDate.getValue() != null) {
            Call<List<ReportControlModel>> call = controlPanelApi.loadSummaryByUser(userProfile.getId(), calendarFromDate.getValue().toString(), calendarToDate.getValue().toString());
            try {
                return call.execute().body();
            } catch (IOException e) {
                logger.debug("Error at response Report Control Model" + e);
            }
        }
        return null;
    }

    private int compareStartTime(WorkPeriod o1, WorkPeriod o2) {
        DateTimeFormatter todayStartTime = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTime firstStartDate;
        LocalTime secondStartDate;
        firstStartDate = LocalTime.from(todayStartTime.parse(o1.getStart()));
        secondStartDate = LocalTime.from(todayStartTime.parse(o2.getStart()));
        return firstStartDate.isAfter(secondStartDate) ? 1 : -1;
    }


}