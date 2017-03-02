package ua.softgroup.matrix.desktop.spykit.timetracker;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.softgroup.matrix.desktop.controllerjavafx.MainLayoutController;
import ua.softgroup.matrix.desktop.currentsessioninfo.CurrentSessionInfo;
import ua.softgroup.matrix.desktop.spykit.interfaces.SpyKitTool;
import ua.softgroup.matrix.desktop.spykit.listeners.activewindowistener.ActiveWindowListener;
import ua.softgroup.matrix.desktop.spykit.listeners.activewindowistener.ActiveWindowListenerFactory;
import ua.softgroup.matrix.desktop.spykit.listeners.globaldevicelistener.NativeDevicesListener;
import ua.softgroup.matrix.desktop.spykit.screenshooter.ScreenShooter;
import ua.softgroup.matrix.desktop.utils.CommandExecutioner;
import ua.softgroup.matrix.server.desktop.model.datamodels.*;

import java.io.IOException;
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
    private MainLayoutController mainLayoutController;
    private ActiveWindowListener activeWindowListener;
    private NativeDevicesListener devicesListener;
    private ScreenShooter screenShooter;
    private CountDownLatch countDownLatch;
    private long projectId;
    private Disposable controlPointObservable;
    private CommandExecutioner commandExecutioner;


    //TODO: Uncomment, when time tracker will be merged with main part
    public  TimeTracker(/*MainLayoutController mainLayoutController,*/ long projectId) {
        this.mainLayoutController = mainLayoutController;
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
            } catch (Exception e) {
                logger.debug("Time tracker crashes: {}", e);
                mainLayoutController.tellUserAboutCrash();
            }
        }).start();
    }

    private void setUpTimeTracker() throws Exception {
        if (status == NOT_USED) {
            commandExecutioner.sendCommandWithNoResponse(START_WORK, projectId);
            turnOnSpyKitTools();
            startControlPointObservable();
            status = IS_USED;
            logger.debug("Time tracking is started");
            (countDownLatch = new CountDownLatch(1)).await();
            return;
        }
        logger.debug("Time tracking was already started");
    }

    /**
     * Call methods of initializing and turning on all spy kit tools.
     */
    private void turnOnSpyKitTools() {
        screenShooter = new ScreenShooter();
        starActiveWindowListenerThread();
        starDevicesListenerThread();
    }

    /**
     * Starts thread of active window listener.
     */
    private void starActiveWindowListenerThread() {
        new Thread(() -> {
            try {
                turnOnActiveWindowListener();
            } catch (Exception e) {
                logger.debug("Active windows listener crashed: {}", e);
                mainLayoutController.tellUserAboutCrash();
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
     * Starts thread of devices listener.
     */
    private void starDevicesListenerThread() {
        new Thread(() -> {
            try {
                turnOnDevicesListener();
            } catch (Exception e) {
                logger.debug("Devices listener crashed: {}", e);
                mainLayoutController.tellUserAboutCrash();
            }
        }).start();
    }

    /**
     * Initialize and turn on devices listener.
     */
    private void turnOnDevicesListener() throws Exception {
        devicesListener = new NativeDevicesListener(this);
        devicesListener.turnOn();
    }

    /**
     * Creates observable that emits control for sending time and logs to server
     */
    private void startControlPointObservable() {
        controlPointObservable = Observable
                .interval(10, TimeUnit.SECONDS)
                .filter(number -> number != 0)
                .map(this::getCheckpointModel)
                .subscribeOn(Schedulers.io())
                .subscribe(this::sendCheckPointToServer);
    }

    private CheckPointModel getCheckpointModel(long order) {
        return new CheckPointModel(order, screenShooter.makeScreenshot(), devicesListener.getKeyboardLogs(),
                devicesListener.getMouseFootage(), activeWindowListener.getWindowTimeMap());
    }

    private void sendCheckPointToServer(CheckPointModel checkPointModel) {
        try {
            checkSynchronization();
            setUpdatedProjectTime(commandExecutioner.sendCommandWithResponse(CHECK_POINT, projectId, checkPointModel));
            //TODO: update time on UI.
        } catch (IOException | ClassNotFoundException e) {
            logger.debug("Couldn't send checkpoint to server. Add checkpoint to synchronized model", e);
            addCheckpointToSynchronizationModel(checkPointModel);
        }
    }

    private void checkSynchronization() throws IOException, ClassNotFoundException {
        if(CurrentSessionInfo.getSynchronizationModel() != null) {
            commandExecutioner.sendCommandWithNoResponse(SYNCHRONIZE, CurrentSessionInfo.getSynchronizationModel(), projectId);
            CurrentSessionInfo.setSynchronizationModel(null);
        }
    }

    private void setUpdatedProjectTime(TimeModel updatedProjectTime){
        CurrentSessionInfo.getProjectModels().stream().filter(projectModel -> projectModel.getId() == projectId)
                .forEach(projectModel -> projectModel.setProjectTime(updatedProjectTime));
    }

    private void addCheckpointToSynchronizationModel(CheckPointModel checkPointModel) {
        if(CurrentSessionInfo.getSynchronizationModel() == null) {
            CurrentSessionInfo.setSynchronizationModel(new SynchronizationModel());
        }
        CurrentSessionInfo.getSynchronizationModel().getCheckPointModels().add(checkPointModel);
    }

    @Override
    public void turnOff() {
        try {
            tryToTurnOffTimeTracker();
        } catch (Exception e) {
            logger.debug("Time tracker crashes: {}", e);
            mainLayoutController.tellUserAboutCrash();
        }
    }

    private void tryToTurnOffTimeTracker() throws Exception {
        if (status == IS_USED) {
            commandExecutioner.sendCommandWithNoResponse(CLOSE, projectId);
            countDownLatch.countDown();
            turnOffSpyKitTools();
            status = WAS_USED;
            logger.debug("Time tracking is stopped");
            return;
        }
        logger.debug("Time tracking was stopped already");
    }

    /**
     * Call methods of initializing and turning on all spy kit tools.
     */
    private void turnOffSpyKitTools() throws Exception {
        controlPointObservable.dispose();
        screenShooter = null;
        activeWindowListener.turnOff();
        activeWindowListener = null;
        devicesListener.turnOff();
        devicesListener = null;
    }

    public void startIdle() {
        try {
            commandExecutioner.sendCommandWithNoResponse(START_IDLE, projectId);
            logger.debug("Idle is started on server");
        } catch (IOException | ClassNotFoundException e) {
            logger.debug("Time tracker crashes: {}", e);;
            mainLayoutController.tellUserAboutCrash();
        }
    }

    public void stopIdle() {
        try {
            commandExecutioner.sendCommandWithNoResponse(STOP_IDLE, projectId);
            logger.debug("Idle is stoped on server");
        } catch (IOException | ClassNotFoundException e) {
            logger.debug("Time tracker crashes: {}", e);;
            mainLayoutController.tellUserAboutCrash();
        }
    }
}
