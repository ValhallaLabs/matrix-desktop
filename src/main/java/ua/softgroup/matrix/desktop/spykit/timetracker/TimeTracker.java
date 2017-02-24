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
public class TimeTracker extends SpyKitTool {
    private static final Logger logger = LoggerFactory.getLogger(TimeTracker.class);
    private MainLayoutController mainLayoutController;
    private SpyKitListener activeWindowListener, devicesListener;
    private ScreenShooter screenShooter;
    private CountDownLatch countDownLatch;
    private long projectId;
    private Disposable controlPointObservable;

    public  TimeTracker(/*MainLayoutController mainLayoutController,*/ long projectId) {
//        this.mainLayoutController = mainLayoutController;
        this.projectId = projectId;
    }

    /**
     * Sends command to server about start tracking project with received id.
     */
    @Override
    public void turnOn() throws InterruptedException {
        if (status == NOT_USED) {
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
                logger.debug("Active windows listener crashed: {}", e);
                //TODO: global crash, turn down matrix
                e.printStackTrace();
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
                e.printStackTrace();
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
        logger.debug("Control point #{}", number);
        screenShooter.makeScreenshot();
        logger.debug("Active windows:{}", activeWindowListener.getLogs());
        logger.debug("Keyboard logs:{}", devicesListener.getLogs());
    }

    /**
     * Sends command to server about about tracking project with current id.
     */
    @Override
    public void turnOff() throws Exception {
        if (status == IS_USED) {
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

    public static void main(String[] args) throws InterruptedException {
        TimeTracker timeTracker = new TimeTracker(1);
        logger.debug("turn on");
        new Thread(() -> {
            try {
                timeTracker.turnOn();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
//        try {
//            timeTracker.turnOff();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        logger.debug("turn off");
    }

}
