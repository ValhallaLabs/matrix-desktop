package ua.softgroup.matrix.desktop.spykit.globaldevicelistener;

import io.reactivex.*;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.softgroup.matrix.desktop.spykit.timetracker.TimeTracker;

import java.awt.*;
import java.util.EventObject;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
public class NativeDevicesListener implements GlobalDeviceListener {
    private static final Logger logger = LoggerFactory.getLogger(NativeDevicesListener.class);
    private static final String START_COUNT_UNTIL_DT_POINT = "Start count until downtime",
            STOP_COUNT_UNTIL_DT_POINT = "Stop count until downtime";
    private TimeTracker timeTracker;
    private Disposable downtimeControlDisposable;
    private FlowableEmitter<EventObject> startCountUntilDtEmitter, stopCountUntilDtEmitter;
    private Boolean isCountingUntilDt = false, isDowntime = false;
    private StringBuilder keyboardLogs;
    private double mouseFootage = 0;
    private Point prevMousePosition;

    public NativeDevicesListener(/*TimeTracker timeTracker*/) {
        this.timeTracker = timeTracker;
        keyboardLogs = new StringBuilder("");
        prevMousePosition = MouseInfo.getPointerInfo().getLocation();
        System.out.println(prevMousePosition.toString());
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
        GlobalScreen.addNativeMouseWheelListener(this::receiveEvent);
        GlobalMouseListener globalMouseListener = new GlobalMouseListener();
        GlobalScreen.addNativeMouseListener(globalMouseListener);
        GlobalScreen.addNativeMouseMotionListener(globalMouseListener);
        GlobalScreen.addNativeKeyListener(new GlobalKeyListener());
    }

    /**
     * Tries to turn on GlobalListener.
     * @return turnOnResult result of turning on GlobalListener
     */
    @Override
    public boolean turnOn() {
        downtimeControlDisposable = createDowntimeControlFlowable();
        try {
            GlobalScreen.registerNativeHook();
            logger.debug("Global devices listener turn on");
            return true;
        } catch (NativeHookException e) {
            downtimeControlDisposable.dispose();
            logger.debug("Global devices listener crashed: {}", e);
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
                //TODO: uncomment when you bind Listener to main part
//                .debounce(CurrentSessionInfo.getClientSettingsModel().getDownTime(), TimeUnit.MINUTES)
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
            //TODO: send to spykit command END_DOWNTIME
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
            //TODO: send to spykit command START_DOWNTIME
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
        try {
            downtimeControlDisposable.dispose();
            GlobalScreen.unregisterNativeHook();
            logger.debug("Global devices listener turn off");
            return true;
        } catch (NativeHookException e) {
            logger.debug("Global devices listener crashed: {}", e);
            return false;
        }
    }

    public String getKeyboardLogging() {
        String keyboardLogsToSend = keyboardLogs.toString();
        keyboardLogs = new StringBuilder("");
        return keyboardLogsToSend;
    }

    private class GlobalMouseListener implements NativeMouseInputListener {
        public void nativeMousePressed(NativeMouseEvent e) {
            receiveEvent(e);
        }

        public void nativeMouseMoved(NativeMouseEvent e) {
            mouseFootage += (Math.sqrt(Math.pow(e.getX() - prevMousePosition.getX(), 2) +
                    Math.pow(e.getY() - prevMousePosition.getY(), 2)))/3779.5275;
            prevMousePosition.setLocation(e.getX(), e.getY());
            receiveEvent(e);
        }

        public void nativeMouseClicked(NativeMouseEvent e) {}

        public void nativeMouseReleased(NativeMouseEvent e) {}

        public void nativeMouseDragged(NativeMouseEvent e) {}
    }

    private class GlobalKeyListener implements NativeKeyListener {
        public void nativeKeyPressed(NativeKeyEvent e) {
            receiveEvent(e);
            keyboardLogs.append(NativeKeyEvent.getKeyText(e.getKeyCode()));
        }

        public void nativeKeyReleased(NativeKeyEvent e) {}

        public void nativeKeyTyped(NativeKeyEvent e) {}
    }


    public static void main(String[] args) {
        NativeDevicesListener nativeDevicesListener = new NativeDevicesListener();
        nativeDevicesListener.turnOn();
    }
}
