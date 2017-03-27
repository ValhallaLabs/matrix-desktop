package ua.softgroup.matrix.desktop.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import ua.softgroup.matrix.desktop.model.ReportControlModel;
import ua.softgroup.matrix.desktop.model.UserProfile;

import java.util.List;

/**
 * @author Andrii Bei <sg.andriy2@gmail.com>
 */
public interface ControlAPI {
    @GET("summary/users/{userId}")
    Call<List<ReportControlModel>> loadSummaryByUser(@Path("userId") long userId,@Query("fromDate") String fromDate,@Query("toDate") String toDate);
    @GET("summary/users")
    Call<List<UserProfile>> loadAllUsers();
}
