package ua.softgroup.matrix.desktop.timetracker.globaldevicelistener;

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
import ua.softgroup.matrix.desktop.timetracker.TimeTracker;

import java.util.EventObject;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
public class NativeDevicesListener implements GlobalDeviceListener {
    private static final Logger logger = LoggerFactory.getLogger(NativeDevicesListener.class);
    private static final String START_COUNT_POINT = "Start count", STOP_COUNT_POINT = "Stop count";
    private TimeTracker timeTracker;
    private Disposable eventObjectsDisposable;
    private FlowableEmitter<EventObject> startCountFlowableEmitter, stopCountFlowableEmitter;
    private Boolean isCounting = false;
    private Boolean isDowntime = false;

    private NativeDevicesListener(TimeTracker timeTracker) {
        this.timeTracker = timeTracker;
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
        createDowntimeControlFlowable();
        try {
            GlobalScreen.registerNativeHook();
            logger.debug("Global devices listener turn on");
            return true;
        } catch (NativeHookException e) {
            logger.debug("Global devices listener crashed: {}", e);
            return false;
        }
    }

    /**
     * Creates and subscribe flowable that received items emitted by startCountFlowable and stopCountFlowable.
     * The flowable controls start and stop of counting downtime, and time until starts count it.
     */
    private void createDowntimeControlFlowable() {
        eventObjectsDisposable = Flowable.merge(createStartCountFlowable(), createStopCountFlowable())
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
    private Flowable<String> createStartCountFlowable() {
        return Flowable.create(this::createStartCountFlowableEmitter, BackpressureStrategy.LATEST)
                .debounce(1000, TimeUnit.MILLISECONDS)
                .map(this::startCount)
                .subscribeOn(Schedulers.io());
    }

    /**
     * Set flowable emitter into own emitter for sending events to flowable.
     * Emits one event at the begin for start count time until downtime.
     * @param e emitter of StartCountFlowable
     */
    private void createStartCountFlowableEmitter(FlowableEmitter<EventObject> e) {
        startCountFlowableEmitter = e;
        startCountFlowableEmitter.onNext(new EventObject(this));
    }

    /**
     * Sets isCounting status true.
     * @param eventObject event of devices
     * @return START_COUNT_POINT point of starting count time until downtime
     */
    private String startCount(EventObject eventObject) {
        isCounting = true;
        return START_COUNT_POINT;
    }

    /**
     * Creates flowable that emits points of stopping count time until count downtime.
     * A stop count point emits only if isCounting status is true.
     * @return stopCountFlowable
     */
    private Flowable<String> createStopCountFlowable() {
        return Flowable.create(this::createStopCountFlowableEmitter, BackpressureStrategy.DROP)
                .filter(eventObject -> isCounting)
                .map(this::stopCount)
                .subscribeOn(Schedulers.io());
    }

    /**
     * Set flowable emitter into own emitter for sending events to flowable.
     * @param e emitter of StopCountFlowable
     */
    private void createStopCountFlowableEmitter(FlowableEmitter<EventObject> e) {
        stopCountFlowableEmitter = e;
    }

    /**
     * Sets isCounting status false.
     * @param eventObject event of devices
     * @return STOP_COUNT_POINT point of starting count time until downtime
     */
    private String stopCount(EventObject eventObject) {
        isCounting = false;
        return STOP_COUNT_POINT;
    }

    /**
     * Stops downtime counting if isDowntime true
     * @param stopPoint point of stop counting downtime
     * @return STOP_COUNT_POINT point of starting count time until downtime
     */
    private String stopDowntime(String stopPoint) {
        if (isDowntime) {
            //TODO: send to timetracker command END_DOWNTIME
            logger.debug("Down time is stopped!");
            isDowntime = false;
        }
        return stopPoint;
    }


    private void startDowntime(String point){
        if (START_COUNT_POINT.equals(point)) {
            //TODO: send to timetracker command START_DOWNTIME
            logger.debug("Down time is started!");
            isDowntime = true;
        }
    }

    private void receiveEvent(EventObject eventObject) {
        startCountFlowableEmitter.onNext(eventObject);
        stopCountFlowableEmitter.onNext(eventObject);
    }

    public boolean turnOff() {
        try {
            eventObjectsDisposable.dispose();
            GlobalScreen.unregisterNativeHook();
            logger.debug("Global devices listener turn off");
            return true;
        } catch (NativeHookException e) {
            logger.debug("Global devices listener crashed: {}", e);
            e.printStackTrace();
            return false;
        }
    }

    private class GlobalMouseListener implements NativeMouseInputListener {
        public void nativeMousePressed(NativeMouseEvent e) {
            receiveEvent(e);
        }

        public void nativeMouseMoved(NativeMouseEvent e) {
            receiveEvent(e);
        }

        public void nativeMouseClicked(NativeMouseEvent e) {}

        public void nativeMouseReleased(NativeMouseEvent e) {}

        public void nativeMouseDragged(NativeMouseEvent e) {}
    }

    private class GlobalKeyListener implements NativeKeyListener {
        public void nativeKeyPressed(NativeKeyEvent e) {
            receiveEvent(e);
        }

        public void nativeKeyReleased(NativeKeyEvent e) {}

        public void nativeKeyTyped(NativeKeyEvent e) {}
    }
}
