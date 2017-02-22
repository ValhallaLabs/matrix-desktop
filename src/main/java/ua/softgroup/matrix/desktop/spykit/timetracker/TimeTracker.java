package ua.softgroup.matrix.desktop.spykit.timetracker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.softgroup.matrix.desktop.controllerjavafx.MainLayoutController;
import ua.softgroup.matrix.desktop.spykit.globaldevicelistener.GlobalDeviceListener;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
public class TimeTracker {
    private static TimeTracker timeTracker;
    protected static final Logger logger = LoggerFactory.getLogger(TimeTracker.class);
    private MainLayoutController mainLayoutController;
    private boolean isTracking = false;
    private GlobalDeviceListener globalDeviceListener;

    public static TimeTracker getInstance(MainLayoutController mainLayoutController) {
        if(timeTracker == null){
            timeTracker = new TimeTracker(mainLayoutController);
        }
        return timeTracker;
    }

    private TimeTracker(MainLayoutController mainLayoutController) {
        this.mainLayoutController = mainLayoutController;
//        globalDeviceListener = new NativeDevicesListener(this);
    }

    /**
     * Sends command to server about start tracking project with received id.
     *
     * @param projectId id of the project to track
     * @return startTrackingResult result is tracker starts to count time
     */
    public boolean startTracking(long projectId) {
        /**TODO: temporary implementation of startTracking method
         * 1) Turn on GlobalDeviceListener
         * 2) If it starts successfully, then send command to server to Start work
         * 3) If server received it successfully, then everything is okay.
         *    If not, turn off GlobalDeviceListener, and shut down fucking matrix.
         */
        //If tracker isn't working, you can start it. Else, you can't, fucker.
        if (isTracking) {
            return false;
        } else {
//            globalDeviceListener.turnOn(this);
            return true;
        }
    }

    /**
     * Sends command to server about about tracking project with current id.
     * @return stopTrackingResult result is tracker stops to count time
     */
    public boolean stopTracking() {
        /**TODO: temporary implementation of stopTracking method
         * 1) Sends to server command to Stop work
         * 2) Turn off GlobalDeviceListener
         */
        //If tracker is working, you can stop it. Else, you can't, fucker.
        if (isTracking) {
            globalDeviceListener.turnOff();
            return true;
        } else {
            return false;
        }
    }

    void startDownTime() {
    }

    void stopDownTime() {
    }

    //TODO: use interval for emitting control points

}
