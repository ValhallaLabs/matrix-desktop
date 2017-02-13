package ua.softgroup.matrix.desktop.currentsessioninfo;

import ua.softgroup.matrix.server.desktop.model.ProjectModel;
import ua.softgroup.matrix.server.desktop.model.TokenModel;

import java.util.Set;

/**
 * Created by Vadim on 10.02.2017.
 */
public class CurrentSessionInfo {
    private static TokenModel tokenModel;
    private static Set<ProjectModel> userActiveProjects;

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
}
