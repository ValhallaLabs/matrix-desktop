package ua.softgroup.matrix.server.desktop.model.responsemodels;

import com.google.common.base.Optional;
import ua.softgroup.matrix.server.desktop.model.datamodels.DataModel;

import java.io.Serializable;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
public class ResponseModel<T extends DataModel> implements Serializable {
    private static final long serialVersionUID = 1L;

    private ResponseStatus responseStatus;

    private Optional<T> container;

    public ResponseModel(ResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

    public ResponseModel(ResponseStatus responseStatus, T dataModel) {
        this.responseStatus = responseStatus;
        container = Optional.of(dataModel);
    }

    public ResponseStatus getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(ResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

    public Optional<T> getContainer() {
        return container;
    }

    public void setContainer(Optional<T> container) {
        this.container = container;
    }
}
