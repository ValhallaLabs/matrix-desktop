package ua.softgroup.matrix.desktop.spykit.timetracker;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import javafx.application.Platform;
import org.jnativehook.NativeHookException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.softgroup.matrix.api.model.datamodels.CheckPointModel;
import ua.softgroup.matrix.api.model.datamodels.SynchronizationModel;
import ua.softgroup.matrix.api.model.datamodels.TimeModel;
import ua.softgroup.matrix.desktop.controllerjavafx.ProjectsLayoutController;
import ua.softgroup.matrix.desktop.currentsessioninfo.CurrentSessionInfo;
import ua.softgroup.matrix.desktop.spykit.interfaces.SpyKitTool;
import ua.softgroup.matrix.desktop.spykit.interfaces.SpyKitToolStatus;
import ua.softgroup.matrix.desktop.spykit.listeners.activewindowistener.ActiveWindowListener;
import ua.softgroup.matrix.desktop.spykit.listeners.activewindowistener.ActiveWindowListenerFactory;
import ua.softgroup.matrix.desktop.spykit.listeners.globaldevicelistener.IdleListener;
import ua.softgroup.matrix.desktop.spykit.screenshooter.ScreenShooter;
import ua.softgroup.matrix.desktop.utils.CommandExecutioner;

import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static ua.softgroup.matrix.api.ServerCommands.*;
import static ua.softgroup.matrix.desktop.spykit.interfaces.SpyKitToolStatus.*;

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

    public TimeTracker(ProjectsLayoutController projectsLayoutController, long projectId) {
        this.projectsLayoutController = projectsLayoutController;
        this.projectId = projectId;
        commandExecutioner = new CommandExecutioner();
    }

    /**
     * Creates new thread and calls method for set up and start time tracker
     */
    @Override
    public void turnOn() {
        new Thread(() -> {
            try {
                setUpTimeTracker();
            } catch (IOException | ClassNotFoundException |InterruptedException e) {
                logger.error("Time tracker crashes: {}", e);
                Platform.runLater(() -> projectsLayoutController.tellUserAboutCrash());
            } catch (CommandExecutioner.FailResponseException e) {
                logger.error("Access denied", e);
                Platform.runLater(() -> projectsLayoutController.tellUserAboutAccessDenied());
            }
        }).start();
    }

    /**
     * If Time tracker is not used, method sends a START_WORK command to server, turns on spy kit tools,
     * starts control point observable and changed on {@link SpyKitToolStatus#IS_USED}
     * @throws Exception
     */
    private void setUpTimeTracker() throws CommandExecutioner.FailResponseException, IOException,
            ClassNotFoundException, InterruptedException {
        logger.debug("Time tracker status: {}", status);
        if (status == NOT_USED) {
            TimeModel timeModel = commandExecutioner.sendCommandWithResponse(START_WORK, projectId);
            Platform.runLater(() -> projectsLayoutController.updateArrivalTime(timeModel.getTodayStartTime()));
            turnOnSpyKitTools();
            startCheckPointObservable();
            status = IS_USED;
            logger.info("Time tracking is started");
            (countDownLatch = new CountDownLatch(1)).await();
            return;
        }
        logger.warn("Time tracking was already started");
    }

    /**
     * Call methods of initializing and turning on all spy kit tools.
     */
    private void turnOnSpyKitTools() {
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
            } catch (InterruptedException e) {
                logger.error("Active windows listener crashed:", e);
                Platform.runLater(() -> projectsLayoutController.tellUserAboutCrash());
            } catch (ActiveWindowListenerFactory.XdotoolException e) {
                logger.error("Xdotool for Linux is not found:", e);
                Platform.runLater(() -> projectsLayoutController.tellUserAboutXdotoolNotFound());
            }
        }).start();
    }

    /**
     * Initialize and turn on active window listener.
     */
    private void turnOnActiveWindowListener() throws ActiveWindowListenerFactory.XdotoolException,
            InterruptedException {
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
            } catch (NativeHookException e) {
                logger.error("Idle listener crashed: {}", e);
                Platform.runLater(() -> projectsLayoutController.tellUserAboutCrash());
            }
        }).start();
    }

    /**
     * Initialize and turn on idle listener.
     */
    private void turnOnIdleListener() throws NativeHookException {
        idleListener = new IdleListener();
        idleListener.turnOn();
    }

    /**
     * Creates observable that emits checkpoints for sending time and logs to server
     */
    private void startCheckPointObservable() {
        checkPointObservable = Observable
                .interval(CurrentSessionInfo.getCheckPointPeriod(), TimeUnit.SECONDS)
                .map(this::getCheckpointModel)
                .subscribeOn(Schedulers.io())
                .subscribe(this::sendCheckPointToServer);
    }

    /**
     * Creates check point model with all required logs. Checks
     * @param order order of checkpoint
     * @return {@link CheckPointModel}
     */
    private CheckPointModel getCheckpointModel(long order) {
        byte[] screenshot = null;
        if (CurrentSessionInfo.getScreenshotFrequency() != 0 &&
                order % CurrentSessionInfo.getScreenshotFrequency() == 0) {
            screenshot = screenShooter.makeScreenshot();
        }
        return new CheckPointModel(order, screenshot, idleListener.getKeyboardLogs(),
                idleListener.getMouseFootage(), activeWindowListener.getWindowTimeMap(),
                idleListener.getIdleTimeSeconds());
    }

    /**
     * Calls method of checking synchronization, then tries to send a CHECK_POINT command to server and
     * retrieve {@link TimeModel} with update total and today time. In case of failure, it calls method of
     * adding checkpoint to synchronize model.
     * @param checkPointModel with logs for server
     */
    private void sendCheckPointToServer(CheckPointModel checkPointModel) {
        try {
            checkSynchronization();
            TimeModel updatedProjectTime = commandExecutioner.sendCommandWithResponse(CHECK_POINT, projectId, checkPointModel);
            Platform.runLater(() -> projectsLayoutController.synchronizedLocalTimeWorkWithServer(updatedProjectTime));
        } catch (IOException | ClassNotFoundException e) {
            logger.warn("Couldn't send checkpoint to server", e);
            addCheckpointToSynchronizationModel(checkPointModel);
        } catch (CommandExecutioner.FailResponseException e) {
            logger.error("Access denied", e);
            Platform.runLater(() -> projectsLayoutController.tellUserAboutAccessDenied());
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
            try {
                commandExecutioner.sendCommandWithNoResponse(SYNCHRONIZE,
                        CurrentSessionInfo.getSynchronizationModel(), projectId);
            } catch (CommandExecutioner.FailResponseException e) {
                logger.error("Access denied", e);
                Platform.runLater(() -> projectsLayoutController.tellUserAboutAccessDenied());
            }
            CurrentSessionInfo.setSynchronizationModel(null);
        }
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
        logger.info("Checkpoint was added to SynchronizationModel.\n " + "Current checkpoints: {}",
                CurrentSessionInfo.getSynchronizationModel().getCheckPointModels().toString());
    }

    /**
     * Calls method of trying to turn off time tracker
     */
    @Override
    public void turnOff() {
        try {
            tryToTurnOffTimeTracker();
        } catch (IOException | ClassNotFoundException | NativeHookException e) {
            logger.error("Time tracker crashes: {}", e);
            Platform.runLater(() -> projectsLayoutController.tellUserAboutCrash());
        } catch (CommandExecutioner.FailResponseException e) {
            logger.error("Access denied", e);
            Platform.runLater(() -> projectsLayoutController.tellUserAboutAccessDenied());
        }
    }

    /**
     * If time tracker has status {@link SpyKitToolStatus#IS_USED}, it sends a END_WORK command to
     * server, then calls method of turning off spykit tools and changes status on {@link SpyKitToolStatus#WAS_USED}
     * @throws Exception
     */
    private void tryToTurnOffTimeTracker() throws CommandExecutioner.FailResponseException,
            IOException, ClassNotFoundException, NativeHookException {
        if (status == IS_USED) {
            sendCheckPointToServer(getCheckpointModel(0));
            commandExecutioner.sendCommandWithResponse(END_WORK, projectId);
            countDownLatch.countDown();
            turnOffSpyKitTools();
            status = WAS_USED;
            logger.info("Time tracking is stopped");
            return;
        }
        logger.warn("Time tracking was stopped already");
    }

    /**
     * Call methods of turning off all spy kit tools.
     */
    private void turnOffSpyKitTools() throws NativeHookException {
        checkPointObservable.dispose();
        screenShooter = null;
        activeWindowListener.turnOff();
        activeWindowListener = null;
        idleListener.turnOff();
        idleListener = null;
    }
}
