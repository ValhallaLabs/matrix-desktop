package ua.softgroup.matrix.desktop.spykit.timetracker;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.softgroup.matrix.desktop.controllerjavafx.ProjectsLayoutController;
import ua.softgroup.matrix.desktop.currentsessioninfo.CurrentSessionInfo;
import ua.softgroup.matrix.desktop.spykit.interfaces.SpyKitTool;
import ua.softgroup.matrix.desktop.spykit.interfaces.SpyKitToolStatus;
import ua.softgroup.matrix.desktop.spykit.listeners.activewindowistener.ActiveWindowListener;
import ua.softgroup.matrix.desktop.spykit.listeners.activewindowistener.ActiveWindowListenerFactory;
import ua.softgroup.matrix.desktop.spykit.listeners.globaldevicelistener.IdleListener;
import ua.softgroup.matrix.desktop.spykit.screenshooter.ScreenShooter;
import ua.softgroup.matrix.desktop.utils.CommandExecutioner;
import ua.softgroup.matrix.server.desktop.api.ServerCommands;
import ua.softgroup.matrix.server.desktop.model.datamodels.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static ua.softgroup.matrix.desktop.spykit.interfaces.SpyKitToolStatus.IS_USED;
import static ua.softgroup.matrix.desktop.spykit.interfaces.SpyKitToolStatus.NOT_USED;
import static ua.softgroup.matrix.desktop.spykit.interfaces.SpyKitToolStatus.WAS_USED;
import static ua.softgroup.matrix.server.desktop.api.ServerCommands.*;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
public class TimeTracker extends SpyKitTool {
    private static final Logger logger = LoggerFactory.getLogger(TimeTracker.class);
    private ProjectsLayoutController projectsLayoutController;
    private ActiveWindowListener activeWindowListener;
    private IdleListener idleListener;
    private ScreenShooter screenShooter;
    private CountDownLatch countDownLatch;
    private long projectId;
    private Disposable checkPointObservable;
    private CommandExecutioner commandExecutioner;

    public  TimeTracker(ProjectsLayoutController projectsLayoutController, long projectId) {
        this.projectsLayoutController = projectsLayoutController;
        this.projectId = projectId;
        commandExecutioner = new CommandExecutioner();
    }

    /**
     * Creates new thread and calls method for set up and start time tracker
     */
    @Override
    public void turnOn() {
        logger.debug("Time tracker attempts to starts");
        new Thread(() -> {
            try {
                setUpTimeTracker();
            } catch (Exception e) {
                logger.debug("Time tracker crashes: {}", e);
                Platform.runLater(() -> projectsLayoutController.tellUserAboutCrash());
            }
        }).start();
    }

    /**
     * If Time tracker is not used, method sends {@link ServerCommands#START_WORK} to server, turns on spy kit tools,
     * starts control point observable and changed on {@link SpyKitToolStatus#IS_USED}
     * @throws Exception
     */
    private void setUpTimeTracker() throws Exception {
        logger.debug("Time tracker status: {}", status);
        if (status == NOT_USED) {
            commandExecutioner.sendCommandWithNoResponse(START_WORK, projectId);
            turnOnSpyKitTools();
            startCheckPointObservable();
            status = IS_USED;
            logger.debug("Time tracking is started");
            (countDownLatch = new CountDownLatch(1)).await();
            return;
        }
    }

    /**
     * Call methods of initializing and turning on all spy kit tools.
     */
    private void turnOnSpyKitTools() {
        logger.debug("Time tracker attempts to start spy kit's tools");
        screenShooter = new ScreenShooter();
        startActiveWindowListenerThread();
        startIdleListenerThread();
    }

    /**
     * Starts thread of active window listener.
     */
    private void startActiveWindowListenerThread() {
        new Thread(() -> {
            try {
                turnOnActiveWindowListener();
            } catch (Exception e) {
                logger.debug("Active windows listener crashed: {}", e);
                Platform.runLater(() -> projectsLayoutController.tellUserAboutCrash());
            }
        }).start();
    }

    /**
     * Initialize and turn on active window listener.
     */
    private void turnOnActiveWindowListener() throws Exception {
        activeWindowListener = ActiveWindowListenerFactory.getListener();
        if(activeWindowListener != null) {
            activeWindowListener.turnOn();
        }
    }

    /**
     * Starts thread of idle listener.
     */
    private void startIdleListenerThread() {
        new Thread(() -> {
            try {
                turnOnIdleListener();
            } catch (Exception e) {
                logger.debug("Idle listener crashed: {}", e);
                Platform.runLater(() -> projectsLayoutController.tellUserAboutCrash());
            }
        }).start();
    }

    /**
     * Initialize and turn on idle listener.
     */
    private void turnOnIdleListener() throws Exception {
        idleListener = new IdleListener();
        idleListener.turnOn();
    }

    /**
     * Creates observable that emits control for sending time and logs to server
     */
    private void startCheckPointObservable() {
        checkPointObservable = Observable
                .interval(10, TimeUnit.SECONDS)
                .filter(number -> number != 0)
                .map(this::getCheckpointModel)
                .subscribeOn(Schedulers.io())
                .subscribe(this::sendCheckPointToServer);
    }

    /**
     * Creates check point model with all required logs.
     * @param order order of checkpoint
     * @return {@link CheckPointModel}
     */
    private CheckPointModel getCheckpointModel(long order) {
        return new CheckPointModel(order, screenShooter.makeScreenshot(), idleListener.getKeyboardLogs(),
                idleListener.getMouseFootage(), activeWindowListener.getWindowTimeMap(),
                idleListener.getIdleTimeSeconds());
    }

    /**
     * Calls method of checking synchronization, then tries to send {@link ServerCommands#CHECK_POINT} to server and
     * retrieve {@link TimeModel} with update total and today time. In case of failure, it calls method of
     * adding checkpoint to synchronize model.
     * @param checkPointModel with logs for server
     */
    private void sendCheckPointToServer(CheckPointModel checkPointModel) {
        try {
            checkSynchronization();
            setUpdatedProjectTime(commandExecutioner.sendCommandWithResponse(CHECK_POINT, projectId, checkPointModel));
            //TODO: update time on UI, when server will be done
        } catch (IOException | ClassNotFoundException e) {
            logger.debug("Couldn't send checkpoint to server. Add checkpoint to synchronized model", e);
            addCheckpointToSynchronizationModel(checkPointModel);
        }
    }

    /**
     * Checks presence of a synchronization model and in case of presence tries to send it to server, and then
     * removes it.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void checkSynchronization() throws IOException, ClassNotFoundException {
        if(CurrentSessionInfo.getSynchronizationModel() != null) {
            commandExecutioner.sendCommandWithNoResponse(SYNCHRONIZE, CurrentSessionInfo.getSynchronizationModel(), projectId);
            CurrentSessionInfo.setSynchronizationModel(null);
        }
    }

    /**
     * Updates project time in project models specified by current project id.
     * @param updatedProjectTime
     */
    private void setUpdatedProjectTime(TimeModel updatedProjectTime){
        CurrentSessionInfo.getProjectModels().stream().filter(projectModel -> projectModel.getId() == projectId)
                .forEach(projectModel -> projectModel.setProjectTime(updatedProjectTime));
    }

    /**
     * Adds checkPoint to synchronization model.
     * @param checkPointModel model that wasn't send to server.
     */
    private void addCheckpointToSynchronizationModel(CheckPointModel checkPointModel) {
        if(CurrentSessionInfo.getSynchronizationModel() == null) {
            CurrentSessionInfo.setSynchronizationModel(new SynchronizationModel());
            CurrentSessionInfo.getSynchronizationModel().setCheckPointModels(new HashSet<>());
        }
        CurrentSessionInfo.getSynchronizationModel().getCheckPointModels().add(checkPointModel);
    }

    /**
     * Calls method of trying to turn off time tracker
     */
    @Override
    public void turnOff() {
        try {
            tryToTurnOffTimeTracker();
        } catch (Exception e) {
            logger.debug("Time tracker crashes: {}", e);
            Platform.runLater(() -> projectsLayoutController.tellUserAboutCrash());
        }
    }

    /**
     * If time tracker has status {@link SpyKitToolStatus#IS_USED}, it sends a {@link ServerCommands#END_WORK} to
     * server, then calls method of turning off spykit tools and changes status on {@link SpyKitToolStatus#WAS_USED}
     * @throws Exception
     */
    private void tryToTurnOffTimeTracker() throws Exception {
        if (status == IS_USED) {
            commandExecutioner.sendCommandWithNoResponse(END_WORK, projectId);
            countDownLatch.countDown();
            turnOffSpyKitTools();
            status = WAS_USED;
            logger.debug("Time tracking is stopped");
            return;
        }
        logger.debug("Time tracking was stopped already");
    }

    /**
     * Call methods of turning off all spy kit tools.
     */
    private void turnOffSpyKitTools() throws Exception {
        checkPointObservable.dispose();
        screenShooter = null;
        activeWindowListener.turnOff();
        activeWindowListener = null;
        idleListener.turnOff();
        idleListener = null;
    }
}
