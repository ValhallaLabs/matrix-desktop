package ua.softgroup.matrix.desktop.controllerjavafx;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import ua.softgroup.matrix.desktop.api.ControlAPI;
import ua.softgroup.matrix.desktop.model.DayJson;
import ua.softgroup.matrix.desktop.model.ReportControlModel;
import ua.softgroup.matrix.desktop.model.localModel.RequestControl;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


/**
 * @author Andrii Bei <sg.andriy2@gmail.com>
 */
public class ControlLayoutController implements Initializable, retrofit2.Callback<List<ReportControlModel>> {
    private static final String BASE_URL = "http://127.0.0.1:8094/api/v2/";
    @FXML
    public ListView listViewReportDetails;
    @FXML
    public ListView listViewAllUsers;
    @FXML
    public TextField testUsers;
    @FXML
    public DatePicker calendarFromDate;
    @FXML
    public DatePicker calendarToDate;
    private ObservableList<RequestControl> controlList = FXCollections.observableArrayList();
    private List<RequestControl> requestControls=  new ArrayList<>();


    @SuppressWarnings("unchecked")
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listViewReportDetails.setItems(controlList);
        listViewReportDetails.setCellFactory(new Callback<ListView<RequestControl>, ListCell<RequestControl>>() {
            @Override
            public ListCell<RequestControl> call(ListView<RequestControl> param) {
                return new ControlListViewCell();
            }
        });
    }

    @Override
    public void onResponse(Call<List<ReportControlModel>> call, Response<List<ReportControlModel>> response) {
        System.out.println("here");
        System.out.println(response.toString());
        List<ReportControlModel> reportControlList = response.body();
        if (reportControlList != null && !reportControlList.isEmpty()) {
           for (ReportControlModel reportControlModel: reportControlList){
               for (DayJson dayJson:reportControlModel.getWorkDays()) {
                   requestControls.add(new RequestControl(dayJson.getId(),dayJson.getDate(),dayJson.getStart(),dayJson.getEnd(),dayJson.getWorkSeconds(),dayJson.getIdleSeconds(),
                           dayJson.getIdlePercentage(),dayJson.isChecked(),dayJson.getCheckerId(),dayJson.getCoefficient(),dayJson.getReportText(),dayJson.getRate(),dayJson.getCurrencyId()));
               }
           }
            for (RequestControl requstControl : requestControls) {
                controlList.add(requstControl);

            }

        }
    }

    @Override
    public void onFailure(Call<List<ReportControlModel>> call, Throwable t) {
        System.out.println("network error" + t);

    }

    public void show(Event event) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(new PropertyNamingStrategy.PascalCaseStrategy());
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(JacksonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build();
        ControlAPI controlApi = retrofit.create(ControlAPI.class);
        System.out.println(testUsers.getText() + calendarFromDate.getValue().toString() + calendarToDate.getValue().toString());
        Call<List<ReportControlModel>> call = controlApi.loadSummaryByUser(Long.parseLong(testUsers.getText()), calendarFromDate.getValue().toString(), calendarToDate.getValue().toString());
        call.enqueue(this);
    }
}
