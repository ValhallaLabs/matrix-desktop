package ua.softgroup.matrix.desktop.spykit.timetracker;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.softgroup.matrix.desktop.controllerjavafx.MainLayoutController;
import ua.softgroup.matrix.desktop.spykit.interfaces.SpyKitTool;
import ua.softgroup.matrix.desktop.spykit.listeners.activewindowistener.ActiveWindowListener;
import ua.softgroup.matrix.desktop.spykit.listeners.activewindowistener.ActiveWindowListenerFactory;
import ua.softgroup.matrix.desktop.spykit.listeners.globaldevicelistener.NativeDevicesListener;
import ua.softgroup.matrix.desktop.spykit.screenshooter.ScreenShooter;
import ua.softgroup.matrix.desktop.utils.CommandExecutioner;
import ua.softgroup.matrix.server.desktop.model.datamodels.CheckPointModel;
import ua.softgroup.matrix.server.desktop.model.datamodels.TimeModel;
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
//TODO: when server will be done, rebuild and regist
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
//        this.mainLayoutController = mainLayoutController;
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
                //TODO: global crash, turn down matrix
                logger.debug("Time tracker crashes: {}", e);
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
                //TODO: global crash, turn down matrix
                logger.debug("Active windows listener crashed: {}", e);
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
                //TODO: global crash, turn down matrix
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
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(this::sendControlPointToServer);
    }

    private void sendControlPointToServer(long number){
        try {
            TimeModel timeModel = commandExecutioner.sendCommandWithResponse(CHECK_POINT, projectId, getCheckpointModel());
            //TODO: get TimeModel from responseModel, and set total and today time to project.
        } catch (IOException | ClassNotFoundException e) {
            logger.debug("Devices listener crashed: {}", e);
//            TODO: global crash, turn down matrix
        }
    }

    private CheckPointModel getCheckpointModel() {
        return new CheckPointModel(screenShooter.makeScreenshot(), devicesListener.getKeyboardLogs(),
                devicesListener.getMouseFootage(), activeWindowListener.getWindowTimeMap());
    }

    @Override
    public void turnOff()  {
        try {
            tryToTurnOffTimeTracker();
        } catch (Exception e) {
            //TODO: global crash, turn down matrix
            logger.debug("Time tracker crashes: {}", e);
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
            //TODO: global crash, turn down matrix
            logger.debug("Time tracker crashes: {}", e);;
        }
    }

    public void stopIdle() {
        try {
            commandExecutioner.sendCommandWithNoResponse(STOP_IDLE, projectId);
            logger.debug("Idle is stoped on server");
        } catch (IOException | ClassNotFoundException e) {
            //TODO: global crash, turn down matrix
            logger.debug("Time tracker crashes: {}", e);;
        }
    }
}
