package ua.softgroup.matrix.desktop.spykit.timetracker;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.softgroup.matrix.desktop.controllerjavafx.MainLayoutController;
import ua.softgroup.matrix.desktop.spykit.interfaces.SpyKitTool;
import ua.softgroup.matrix.desktop.spykit.interfaces.SpyKitListener;
import ua.softgroup.matrix.desktop.spykit.listeners.activewindowistener.ActiveWindowListenerFactory;
import ua.softgroup.matrix.desktop.spykit.listeners.globaldevicelistener.NativeDevicesListener;
import ua.softgroup.matrix.desktop.spykit.screenshooter.ScreenShooter;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
//TODO: when server will be done, rebuild and regist
public class TimeTracker extends SpyKitTool {
    private static final Logger logger = LoggerFactory.getLogger(TimeTracker.class);
    private MainLayoutController mainLayoutController;
    private SpyKitListener activeWindowListener, devicesListener;
    private ScreenShooter screenShooter;
    private CountDownLatch countDownLatch;
    private long projectId;
    private Disposable controlPointObservable;

    //TODO: Uncomment, when time tracker will be merged with main part
    public  TimeTracker(/*MainLayoutController mainLayoutController,*/ long projectId) {
//        this.mainLayoutController = mainLayoutController;
        this.projectId = projectId;
    }

    /**
     * Creates new thread and calls method for set up and start time tracker
     */
    @Override
    public void turnOn() {
        new Thread(() -> {
            try {
                setUpTimeTracker();
            } catch (InterruptedException e) {
                //TODO: global crash, turn down matrix
                logger.debug("Time tracker crashes: {}", e);
            }
        }).start();
    }

    /**
     * Sends command to server about start tracking project with received id.
     */
    private void setUpTimeTracker() throws InterruptedException {
        if (status == NOT_USED) {
            //TODO: add method for sending start work command to server
            turnOnSpyKitTools();
            startControlPointObservable();
            status = IS_USED;
            logger.debug("Time tracking is started");
            (countDownLatch = new CountDownLatch(1)).await();
        } else {
            logger.debug("Time tracking was already started");
        }
    }

    /**
     * Call methods of initializing and turning on all spy kit tools.
     */
    private void turnOnSpyKitTools() {
        screenShooter = new ScreenShooter(projectId);
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
        activeWindowListener = ActiveWindowListenerFactory.getListener(projectId);
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
        devicesListener = new NativeDevicesListener(this, projectId);
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

    //TODO: rewrite this method, when server problems will be resolved
    private void sendControlPointToServer(long number){
        //Temporary realization
        logger.debug("Control point #{}", number);
        screenShooter.makeScreenshot();
        logger.debug("Active windows:{}", activeWindowListener.getLogs());
        logger.debug("Keyboard logs:{}", devicesListener.getLogs());
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
            //TODO: add method for sending end work command to server
            countDownLatch.countDown();
            turnOffSpyKitTools();
            status = WAS_USED;
            logger.debug("Time tracking is stopped");
        } else {
            logger.debug("Time tracking was stopped already");
        }
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

    public void startDowntime() {
        //TODO: send command to server about start downtime
        logger.debug("Down time is started on server");
    }

    public void stopDowntime() {
        //TODO: send command to server about stop downtime
        logger.debug("Down time is stopped on server");
    }
}
