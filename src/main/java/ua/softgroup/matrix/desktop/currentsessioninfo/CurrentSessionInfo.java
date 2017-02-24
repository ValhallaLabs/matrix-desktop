package ua.softgroup.matrix.desktop.currentsessioninfo;

import ua.softgroup.matrix.server.desktop.model.ClientSettingsModel;
import ua.softgroup.matrix.server.desktop.model.ProjectModel;
import ua.softgroup.matrix.server.desktop.model.TokenModel;

import java.util.Set;

/**
 * Created by Vadim on 10.02.2017.
 */
// TODO package structure
public class CurrentSessionInfo {
    private static TokenModel tokenModel = new TokenModel("token");
    private static Set<ProjectModel> userActiveProjects;
    private static ClientSettingsModel clientSettingsModel;

    public static TokenModel getTokenModel() {
        return tokenModel;
    }

    public static void setTokenModel(TokenModel tokenModel) {
        CurrentSessionInfo.tokenModel = tokenModel;
    }

    public static Set<ProjectModel> getUserActiveProjects() {
        return userActiveProjects;
    }

    public static void setUserActiveProjects(Set<ProjectModel> userActiveProjects) {
        CurrentSessionInfo.userActiveProjects = userActiveProjects;
    }

    public static ClientSettingsModel getClientSettingsModel() {
        return clientSettingsModel;
    }

    public static void setClientSettingsModel(ClientSettingsModel clientSettingsModel) {
        CurrentSessionInfo.clientSettingsModel = clientSettingsModel;
    }
}
