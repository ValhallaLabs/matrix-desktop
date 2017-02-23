package ua.softgroup.matrix.desktop.spykit.timetracker;

import org.jnativehook.NativeHookException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.softgroup.matrix.desktop.controllerjavafx.MainLayoutController;
import ua.softgroup.matrix.desktop.spykit.listeners.SpyKitListener;
import ua.softgroup.matrix.desktop.spykit.listeners.activewindowistener.ActiveWindowListenerFactory;
import ua.softgroup.matrix.desktop.spykit.listeners.globaldevicelistener.NativeDevicesListener;
import ua.softgroup.matrix.desktop.spykit.screenshooter.ScreenShooter;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
public class TimeTracker {
    private static TimeTracker timeTracker;
    protected static final Logger logger = LoggerFactory.getLogger(TimeTracker.class);
    private MainLayoutController mainLayoutController;
    private boolean isUsed = false;
    private SpyKitListener activeWindowListener, devicesListener;
    private ScreenShooter screenShooter;

    public  TimeTracker(/*MainLayoutController mainLayoutController*/) {
//        this.mainLayoutController = mainLayoutController;
    }

    /**
     * Sends command to server about start tracking project with received id.
     * @param projectId id of the project to track
     * @return startTrackingResult result is tracker starts to count time
     */
    public void startTracking(long projectId) {
        if (!isUsed) {
            turnOnSpyKitTools(projectId);
            isUsed = true;
            logger.debug("Time tracking is started");
        } else {
            logger.debug("Time tracking was already started");
        }
    }

    private void turnOnSpyKitTools(long projectId) {
        starActiveWindowListenerThread(projectId);
        starDevicesListenerThread(projectId);
    }

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

    private void turnOnActiveWindowListener(long projectId) throws NativeHookException, InterruptedException {
        activeWindowListener = ActiveWindowListenerFactory.getListener(projectId);
        if(activeWindowListener != null) {
            activeWindowListener.turnOn();
        }
    }

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

    private void turnOnDevicesListener(long projectId) throws NativeHookException, InterruptedException {
        devicesListener = new NativeDevicesListener(this, projectId);
        devicesListener.turnOn();
    }

    /**
     * Sends command to server about about tracking project with current id.
     * @return stopTrackingResult result is tracker stops to count time
     */
    public void stopTracking() {
        if (isUsed) {
            try {
                activeWindowListener.turnOff();
                devicesListener.turnOff();
                logger.debug("Time tracking is stopped");
            } catch (NativeHookException e) {
                logger.debug("Time tracker crashed: {}", e);
                //TODO: global crash, turn down matrix
            };
        } else {
            logger.debug("Time tracking was stopped already");
        }
    }

    public static void main(String[] args) {
        TimeTracker timeTracker = new TimeTracker();
        timeTracker.startTracking(1);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        timeTracker.stopTracking();
    }
}
