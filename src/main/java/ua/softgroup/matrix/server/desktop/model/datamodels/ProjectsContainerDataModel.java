package ua.softgroup.matrix.server.desktop.model.datamodels;

import java.io.Serializable;
import java.util.Set;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
public class ProjectsContainerDataModel implements Serializable, DataModel {
    private static final long serialVersionUID = 1L;

    private Set<ProjectModel> projectModels;

    public ProjectsContainerDataModel(Set<ProjectModel> projectModels) {
        this.projectModels = projectModels;
    }

    public Set<ProjectModel> getProjectModels() {
        return projectModels;
    }

    public void setProjectModels(Set<ProjectModel> projectModels) {
        this.projectModels = projectModels;
    }
}
