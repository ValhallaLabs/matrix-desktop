package ua.softgroup.matrix.desktop.spykit.timetracker;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.jnativehook.NativeHookException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.softgroup.matrix.desktop.controllerjavafx.MainLayoutController;
import ua.softgroup.matrix.desktop.spykit.listeners.SpyKitListener;
import ua.softgroup.matrix.desktop.spykit.listeners.activewindowistener.ActiveWindowListenerFactory;
import ua.softgroup.matrix.desktop.spykit.listeners.globaldevicelistener.NativeDevicesListener;
import ua.softgroup.matrix.desktop.spykit.screenshooter.ScreenShooter;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
public class TimeTracker {
    protected static final Logger logger = LoggerFactory.getLogger(TimeTracker.class);
    private MainLayoutController mainLayoutController;
    private boolean isUsed = false;
    private SpyKitListener activeWindowListener, devicesListener;
    private ScreenShooter screenShooter;
    private CountDownLatch countDownLatch;

    public  TimeTracker(/*MainLayoutController mainLayoutController*/) {
//        this.mainLayoutController = mainLayoutController;
    }

    /**
     * Sends command to server about start tracking project with received id.
     * @param projectId id of the project to track
     */
    public void startTracking(long projectId) throws InterruptedException {
        if (!isUsed) {
            turnOnSpyKitTools(projectId);
            isUsed = true;
            startControlPointObservable();
            (countDownLatch = new CountDownLatch(1)).await();
            logger.debug("Time tracking is started");
        } else {
            logger.debug("Time tracking was already started");
        }
    }

    /**
     * Call methods of initializing and turning on all spy kit tools.
     * @param projectId id for binding tools to project
     */
    private void turnOnSpyKitTools(long projectId) {
        screenShooter = new ScreenShooter(projectId);
        starActiveWindowListenerThread(projectId);
        starDevicesListenerThread(projectId);
    }

    /**
     * Starts thread of active window listener.
     * @param projectId id for binding tools to project
     */
    private void starActiveWindowListenerThread(long projectId) {
        new Thread(() -> {
            try {
                turnOnActiveWindowListener(projectId);
            } catch (NativeHookException | InterruptedException e) {
                logger.debug("Active windows listener crashed: {}", e);
                //TODO: global crash, turn down matrix
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Initialize and turn on active window listener.
     * @param projectId id for binding tools to project
     */
    private void turnOnActiveWindowListener(long projectId) throws NativeHookException, InterruptedException {
        activeWindowListener = ActiveWindowListenerFactory.getListener(projectId);
        if(activeWindowListener != null) {
            activeWindowListener.turnOn();
        }
    }

    /**
     * Starts thread of devices listener.
     * @param projectId id for binding tools to project
     */
    private void starDevicesListenerThread(long projectId) {
        new Thread(() -> {
            try {
                turnOnDevicesListener(projectId);
            } catch (NativeHookException | InterruptedException e) {
                logger.debug("Devices listener crashed: {}", e);
                //TODO: global crash, turn down matrix
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Initialize and turn on devices listener.
     * @param projectId id for binding tools to project
     */
    private void turnOnDevicesListener(long projectId) throws NativeHookException, InterruptedException {
        devicesListener = new NativeDevicesListener(this, projectId);
        devicesListener.turnOn();
    }

    private void startControlPointObservable() {
        Disposable controlPointObservable = Observable
                .interval(1, TimeUnit.MINUTES)
                .filter(number -> number != 0)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(this::sendControlPointToServer);
    }

    private void sendControlPointToServer(long number){
        logger.debug("Control point #{}", number);
        screenShooter.makeScreenshot();
        System.out.println(activeWindowListener.getLogs());
        System.out.println(devicesListener.getLogs());
        System.out.println("/////////////");
    }

    /**
     * Sends command to server about about tracking project with current id.
     */
    public void stopTracking() {
        if (isUsed) {
            try {
                countDownLatch.countDown();
                turnOffSpyKitTools();
                logger.debug("Time tracking is stopped");
            } catch (NativeHookException e) {
                logger.debug("Time tracker crashed: {}", e);
                //TODO: global crash, turn down matrix
            };
        } else {
            logger.debug("Time tracking was stopped already");
        }
    }

    /**
     * Call methods of initializing and turning on all spy kit tools.
     */
    private void turnOffSpyKitTools() throws NativeHookException {
        screenShooter = null;
        activeWindowListener.turnOff();
        activeWindowListener = null;
        devicesListener.turnOff();
        devicesListener = null;
    }

    public static void main(String[] args) throws InterruptedException {
        TimeTracker timeTracker = new TimeTracker();
        timeTracker.startTracking(1);
    }

}
