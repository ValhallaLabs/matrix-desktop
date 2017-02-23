package ua.softgroup.matrix.desktop.spykit.listeners.globaldevicelistener;

import io.reactivex.*;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;
import org.jnativehook.mouse.NativeMouseWheelEvent;
import org.jnativehook.mouse.NativeMouseWheelListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.softgroup.matrix.desktop.currentsessioninfo.CurrentSessionInfo;
import ua.softgroup.matrix.desktop.spykit.listeners.SpyKitListener;
import ua.softgroup.matrix.desktop.spykit.timetracker.TimeTracker;
import ua.softgroup.matrix.server.desktop.model.WriteKeyboard;

import java.awt.*;
import java.util.EventObject;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
public class NativeDevicesListener implements SpyKitListener {
    private static final Logger logger = LoggerFactory.getLogger(NativeDevicesListener.class);
    private static final String START_COUNT_UNTIL_DT_POINT = "Start count until downtime",
            STOP_COUNT_UNTIL_DT_POINT = "Stop count until downtime";
    private TimeTracker timeTracker;
    private Disposable downtimeControlDisposable;
    private FlowableEmitter<EventObject> startCountUntilDtEmitter, stopCountUntilDtEmitter;
    private Boolean isCountingUntilDt = false, isDowntime = false, isWorking = false;
    private StringBuilder keyboardLogs;
    private double mouseFootage;
    private Point prevMousePosition;
    private int keyboardUpdateFrequently;
    private long projectId;
    private GlobalMouseWheelListener globalMouseWheelListener;
    private GlobalMouseListener globalMouseListener;
    private GlobalKeyListener globalKeyListener;

    public NativeDevicesListener(TimeTracker timeTracker, long projectId) {
        this.timeTracker = timeTracker;
        this.projectId = projectId;
        keyboardLogs = new StringBuilder("");
        prevMousePosition = MouseInfo.getPointerInfo().getLocation();
        offGlobalScreenLogger();
        addListenersToGlobalListener();
    }

    /**
     * Turns off default GlobalsScreen logs of events
     */
    private void offGlobalScreenLogger() {
        java.util.logging.Logger logger = java.util.logging.Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
    }

    /**
     * Adds listeners of keyboard and mouse to Global screen of JNativeHook library
     */
    private void addListenersToGlobalListener(){
        globalMouseWheelListener = new GlobalMouseWheelListener();
        GlobalScreen.addNativeMouseWheelListener(globalMouseWheelListener);
        globalMouseListener = new GlobalMouseListener();
        GlobalScreen.addNativeMouseListener(globalMouseListener);
        GlobalScreen.addNativeMouseMotionListener(globalMouseListener);
        globalKeyListener = new GlobalKeyListener();
        GlobalScreen.addNativeKeyListener(globalKeyListener);
    }

    /**
     * Tries to turn on GlobalListener.
     * @return turnOnResult result of turning on GlobalListener
     */
    @Override
    public boolean turnOn() {
        if (!isWorking) {
            downtimeControlDisposable = createDowntimeControlFlowable();
            try {
                GlobalScreen.registerNativeHook();
                logger.debug("Native devices listener is turned on");
                return true;
            } catch (NativeHookException e) {
                downtimeControlDisposable.dispose();
                logger.debug("Native devices listener crashed: {}", e);
                return false;
            }
        } else {
            logger.debug("Native devices listener is turned on already");
            return false;
        }
    }

    /**
     * Creates and subscribe flowable that received items emitted by startCountFlowable and stopCountFlowable.
     * The flowable controls start and stop of counting downtime, and time until starts count it.
     * @return downtimeControlDisposable
     */
    private Disposable createDowntimeControlFlowable() {
        return Flowable.merge(createStartCountUntilDtFlowable(), createStopCountUntilDtFlowable())
                .doOnNext(s -> logger.debug("Count until down time: {}", s))
                .doOnNext(this::stopDowntime)
                .debounce(CurrentSessionInfo.getClientSettingsModel().getDownTime(), TimeUnit.MINUTES)
                .debounce(5000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe(this::startDowntime);
    }

    /**
     * Creates flowable that emits points of starting count time until count downtime.
     * A start count point emits after 1000 milliseconds after last event.
     * @return startCountFlowable
     */
    private Flowable<String> createStartCountUntilDtFlowable() {
        return Flowable.create(this::createStartCountUntilDtFlowableEmitter, BackpressureStrategy.LATEST)
                .debounce(1000, TimeUnit.MILLISECONDS)
                .map(this::startCountUntilDt)
                .subscribeOn(Schedulers.io());
    }

    /**
     * Set flowable emitter into own emitter for sending events to flowable.
     * Emits one event at the begin for start count time until downtime.
     * @param e emitter of StartCountFlowable
     */
    private void createStartCountUntilDtFlowableEmitter(FlowableEmitter<EventObject> e) {
        startCountUntilDtEmitter = e;
        startCountUntilDtEmitter.onNext(new EventObject(this));
    }

    /**
     * Sets isCountingUntilDt status true.
     * @param eventObject event of devices
     * @return START_COUNT_UNTIL_DT_POINT point of starting count time until downtime
     */
    private String startCountUntilDt(EventObject eventObject) {
        isCountingUntilDt = true;
        return START_COUNT_UNTIL_DT_POINT;
    }

    /**
     * Creates flowable that emits points of stopping count time until count downtime.
     * A stop count point emits only if isCountingUntilDt status is true.
     * @return stopCountFlowable
     */
    private Flowable<String> createStopCountUntilDtFlowable() {
        return Flowable.create(this::createStopCountUntilDtFlowableEmitter, BackpressureStrategy.DROP)
                .filter(eventObject -> isCountingUntilDt)
                .map(this::stopCountUntilDt)
                .subscribeOn(Schedulers.io());
    }

    /**
     * Set flowable emitter into own emitter for sending events to flowable.
     * @param e emitter of StopCountFlowable
     */
    private void createStopCountUntilDtFlowableEmitter(FlowableEmitter<EventObject> e) {
        stopCountUntilDtEmitter = e;
    }

    /**
     * Sets isCountingUntilDt status false.
     * @param eventObject event of devices
     * @return STOP_COUNT_UNTIL_DT_POINT point of starting count time until downtime
     */
    private String stopCountUntilDt(EventObject eventObject) {
        isCountingUntilDt = false;
        return STOP_COUNT_UNTIL_DT_POINT;
    }

    /**
     * Stops downtime counting if isDowntime true
     * @param stopPoint point of stop counting downtime
     * @return STOP_COUNT_UNTIL_DT_POINT point of starting count time until downtime
     */
    private String stopDowntime(String stopPoint) {
        if (isDowntime) {
            //TODO: send to timeTracker command END_DOWNTIME
            logger.debug("Down time is stopped!");
            isDowntime = false;
        }
        return stopPoint;
    }

    /**
     * Starts downtime counting if method receive START_COUNT_UNTIL_DT_POINT
     * @param point can be point of start or stop counting downtime
     */
    private void startDowntime(String point){
        if (START_COUNT_UNTIL_DT_POINT.equals(point)) {
            //TODO: send to timeTracker command START_DOWNTIME
            logger.debug("Down time is started!");
            isDowntime = true;
        }
    }

    /**
     * Receive events from keyboard and mouse and sending its to emitters.
     * @param eventObject event which was emitted by keyboard or mouse
     */
    private void receiveEvent(EventObject eventObject) {
        startCountUntilDtEmitter.onNext(eventObject);
        stopCountUntilDtEmitter.onNext(eventObject);
    }

    /**
     * Tries to turn off GlobalListener.
     * @return turnOffResult result of turning off GlobalListener
     */
    public boolean turnOff() {
        if (isWorking) {
            try {
                downtimeControlDisposable.dispose();
                removeListenersFromGlobalListener();
                GlobalScreen.unregisterNativeHook();
                logger.debug("Native devices listener is turned off");
                return true;
            } catch (NativeHookException e) {
                logger.debug("Native devices listener crashed: {}", e);
                return false;
            }
        } else {
            logger.debug("Native devices listener is turned off already");
            return false;
        }
    }

    /**
     * Removes listeners from {@link GlobalScreen}.
     */
    private void removeListenersFromGlobalListener(){
        GlobalScreen.removeNativeMouseWheelListener(globalMouseWheelListener);
        GlobalScreen.removeNativeMouseListener(globalMouseListener);
        GlobalScreen.removeNativeMouseMotionListener(globalMouseListener);
        GlobalScreen.removeNativeKeyListener(globalKeyListener);
    }

    /**
     * Returns {@link WriteKeyboard} model with logs of keyboard and footage of mouse.
     * Clears keyboardLogs string builder and mouseFootage count.
     * @return writeKeyboard model with keyboard logs
     */
    public WriteKeyboard getKeyboardLogging() {
        //TODO: Add to WriteKeyboard field of mouse footage and set here, and then clear mouse footage
        WriteKeyboard writeKeyboard = new WriteKeyboard(keyboardLogs.toString(), projectId);
        keyboardLogs = new StringBuilder("");
        return writeKeyboard;
    }

    private class GlobalMouseWheelListener implements NativeMouseWheelListener {

        /**
         * Send event of mouse wheel into emitters.
         * @param e native key event
         */
        public void nativeMouseWheelMoved(NativeMouseWheelEvent e) {
            receiveEvent(e);
        }
    }

    private class GlobalMouseListener implements NativeMouseInputListener {
        private final static double PIXELS_PER_METER = 3779.5275;

        /**
         * Send event of mouse buttons into emitters.
         * @param e native key event
         */
        public void nativeMousePressed(NativeMouseEvent e) {
            receiveEvent(e);
        }

        /**
         * Send event of mouse moves into emitters and calculate mouse footage in meters.
         * @param e native key event
         */
        public void nativeMouseMoved(NativeMouseEvent e) {
            mouseFootage += (Math.sqrt(Math.pow(e.getX() - prevMousePosition.getX(), 2) +
                    Math.pow(e.getY() - prevMousePosition.getY(), 2)))/PIXELS_PER_METER;
            prevMousePosition.setLocation(e.getX(), e.getY());
            receiveEvent(e);
        }

        public void nativeMouseClicked(NativeMouseEvent e) {}

        public void nativeMouseReleased(NativeMouseEvent e) {}

        public void nativeMouseDragged(NativeMouseEvent e) {}
    }

    private class GlobalKeyListener implements NativeKeyListener {

        /**
         * Send event of keyboard into emitters of listeners and logging keyboard.
         * @param e native key event
         */
        public void nativeKeyPressed(NativeKeyEvent e) {
            receiveEvent(e);
            keyboardLogs.append(NativeKeyEvent.getKeyText(e.getKeyCode()));
        }

        public void nativeKeyReleased(NativeKeyEvent e) {}

        public void nativeKeyTyped(NativeKeyEvent e) {}
    }
}
