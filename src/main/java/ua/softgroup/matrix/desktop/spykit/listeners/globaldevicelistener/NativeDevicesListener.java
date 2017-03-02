package ua.softgroup.matrix.desktop.spykit.listeners.globaldevicelistener;

import io.reactivex.*;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.softgroup.matrix.desktop.spykit.interfaces.SpyKitTool;
import ua.softgroup.matrix.desktop.spykit.timetracker.TimeTracker;
import java.awt.*;
import java.util.EventListener;
import java.util.EventObject;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import static java.util.logging.Logger.getLogger;
import static ua.softgroup.matrix.desktop.spykit.interfaces.SpyKitToolStatus.IS_USED;
import static ua.softgroup.matrix.desktop.spykit.interfaces.SpyKitToolStatus.NOT_USED;
import static ua.softgroup.matrix.desktop.spykit.interfaces.SpyKitToolStatus.WAS_USED;
import static ua.softgroup.matrix.desktop.spykit.listeners.globaldevicelistener.CountUntilIdlePoint.START_COUNT_UNTIL_IDLE;
import static ua.softgroup.matrix.desktop.spykit.listeners.globaldevicelistener.CountUntilIdlePoint.STOP_COUNT_UNTIL_IDLE;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
public class NativeDevicesListener extends SpyKitTool {
    private static final Logger logger = LoggerFactory.getLogger(NativeDevicesListener.class);
    private TimeTracker timeTracker;
    private Disposable idleControlDisposable;
    private FlowableEmitter<EventObject> startCountUntilIdleEmitter, stopCountUntilIdleEmitter;
    private Boolean isCountingUntilIdle = false, isIdle = false;
    private StringBuilder keyboardLogs;
    private double mouseFootage;
    private Point prevMousePosition;
    private EventListener globalMouseWheelListener, globalMouseListener, globalKeyListener;

    public NativeDevicesListener(TimeTracker timeTracker) {
        this.timeTracker = timeTracker;
        keyboardLogs = new StringBuilder("");
        prevMousePosition = MouseInfo.getPointerInfo().getLocation();
        getLogger(GlobalScreen.class.getPackage().getName()).setLevel(Level.OFF);
    }

    /**
     * Tries to turn on GlobalListener.
     */
    @Override
    public void turnOn() throws NativeHookException {
        if (status == NOT_USED) {
            createIdleControlFlowable();
            addListenersToGlobalListener();
            GlobalScreen.registerNativeHook();
            status = IS_USED;
            logger.debug("Native devices listener is turned on");
            return;
        }
        logger.debug("Native devices listener was turned on already");
    }

    /**
     * Creates and subscribe flowable that received items emitted by startCountFlowable and stopCountFlowable.
     * The flowable controls start and stop of counting idle, and time until starts count it.
     */
    private void createIdleControlFlowable() {
        idleControlDisposable = Flowable.merge(createStartCountUntilIdleFlowable(), createStopCountUntilIdleFlowable())
                .doOnNext(s -> logger.debug("Count until down time: {}", s))
                .doOnNext(this::stopIdle)
                .debounce(5, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe(this::startIdle);
    }

    /**
     * Creates flowable that emits points of starting count time until count downtime.
     * A start count point emits after 1000 milliseconds after last event.
     * @return startCountFlowable
     */
    private Flowable<CountUntilIdlePoint> createStartCountUntilIdleFlowable() {
        return Flowable.create(this::createStartCountUntilIdleFlowableEmitter, BackpressureStrategy.LATEST)
                .debounce(1, TimeUnit.SECONDS)
                .map(this::startCountUntilIdle)
                .subscribeOn(Schedulers.io());
    }

    /**
     * Set flowable emitter into own emitter for sending events to flowable.
     * Emits one event at the begin for start count time until downtime.
     * @param e emitter of StartCountFlowable
     */
    private void createStartCountUntilIdleFlowableEmitter(FlowableEmitter<EventObject> e) {
        startCountUntilIdleEmitter = e;
        startCountUntilIdleEmitter.onNext(new EventObject(this));
    }

    /**
     * Sets isCountingUntilIdle status true.
     * @param eventObject event of devices
     * @return START_COUNT_UNTIL_IDLE_POINT point of starting count time until downtime
     */
    private CountUntilIdlePoint startCountUntilIdle(EventObject eventObject) {
        isCountingUntilIdle = true;
        return START_COUNT_UNTIL_IDLE;
    }

    /**
     * Creates flowable that emits points of stopping count time until count downtime.
     * A stop count point emits only if isCountingUntilIdle status is true.
     * @return stopCountFlowable
     */
    private Flowable<CountUntilIdlePoint> createStopCountUntilIdleFlowable() {
        return Flowable.create(this::createStopCountUntilIdleFlowableEmitter, BackpressureStrategy.DROP)
                .filter(eventObject -> isCountingUntilIdle)
                .map(this::stopCountUntilIdle)
                .subscribeOn(Schedulers.io());
    }

    /**
     * Set flowable emitter into own emitter for sending events to flowable.
     * @param e emitter of StopCountFlowable
     */
    private void createStopCountUntilIdleFlowableEmitter(FlowableEmitter<EventObject> e) {
        stopCountUntilIdleEmitter = e;
    }

    /**
     * Sets isCountingUntilIdle status false.
     * @param eventObject event of devices
     * @return STOP_COUNT_UNTIL_IDLE_POINT point of starting count time until downtime
     */
    private CountUntilIdlePoint stopCountUntilIdle(EventObject eventObject) {
        isCountingUntilIdle = false;
        return STOP_COUNT_UNTIL_IDLE;
    }

    /**
     * Stops downtime counting if isIdle true
     * @param stopPoint point of stop counting downtime
     * @return STOP_COUNT_UNTIL_IDLE_POINT point of starting count time until downtime
     */
    private CountUntilIdlePoint stopIdle(CountUntilIdlePoint stopPoint) {
        if (isIdle) {
            timeTracker.stopIdle();
            logger.debug("Idle is stopped!");
            isIdle = false;
        }
        return stopPoint;
    }

    /**
     * Starts downtime counting if method receive START_COUNT_UNTIL_IDLE_POINT
     * @param point can be point of start or stop counting downtime
     */
    private void startIdle(CountUntilIdlePoint point){
        if (START_COUNT_UNTIL_IDLE == point) {
            timeTracker.startIdle();
            logger.debug("Idle is started!");
            isIdle = true;
        }
    }

    /**
     * Adds listeners of keyboard and mouse to Global screen of JNativeHook library
     */
    private void addListenersToGlobalListener(){
        globalMouseWheelListener = new GlobalMouseWheelListener();
        GlobalScreen.addNativeMouseWheelListener((NativeMouseWheelListener) globalMouseWheelListener);
        globalMouseListener = new GlobalMouseListener();
        GlobalScreen.addNativeMouseListener((NativeMouseListener) globalMouseListener);
        GlobalScreen.addNativeMouseMotionListener((NativeMouseMotionListener) globalMouseListener);
        globalKeyListener = new GlobalKeyListener();
        GlobalScreen.addNativeKeyListener((NativeKeyListener) globalKeyListener);
    }

    /**
     * Receive events from keyboard and mouse and sending its to emitters.
     * @param eventObject event which was emitted by keyboard or mouse
     */
    private void receiveEvent(EventObject eventObject) {
        startCountUntilIdleEmitter.onNext(eventObject);
        stopCountUntilIdleEmitter.onNext(eventObject);
    }

    /**
     * Tries to turn off GlobalListener.
     */
    @Override
    public void turnOff() throws NativeHookException {
        if (status == IS_USED) {
            idleControlDisposable.dispose();
            removeListenersFromGlobalListener();
            GlobalScreen.unregisterNativeHook();
            status = WAS_USED;
            logger.debug("Native devices listener is turned off");
            return;
        }
        logger.debug("Native devices listener was turned off already");
    }

    /**
     * Removes listeners from {@link GlobalScreen}.
     */
    private void removeListenersFromGlobalListener(){
        GlobalScreen.removeNativeMouseWheelListener((NativeMouseWheelListener) globalMouseWheelListener);
        GlobalScreen.removeNativeMouseListener((NativeMouseListener) globalMouseListener);
        GlobalScreen.removeNativeMouseMotionListener((NativeMouseMotionListener) globalMouseListener);
        GlobalScreen.removeNativeKeyListener((NativeKeyListener) globalKeyListener);
    }

    /**
     * Returns a string object with logs of keyboard. Clears keyboardLogs string builder.
     * @return writeKeyboard model with keyboard logs
     */
    public synchronized String getKeyboardLogs() {
        String keyboardLogs = this.keyboardLogs.toString();
        this.keyboardLogs = new StringBuilder("");
        return keyboardLogs;
    }

    /**
     * Returns a double primitive with mouse's footage. Reset mouse's footage.
     * @return mouseFootage
     */
    public synchronized double getMouseFootage() {
        double mouseFootage = this.mouseFootage;
        this.mouseFootage = 0;
        return mouseFootage;
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
         * Send event of keyboard into emitters of listeners and logging key, if it's not in forbidden list.
         * @param e native key event
         */
        public synchronized void nativeKeyPressed(NativeKeyEvent e) {
            receiveEvent(e);
            if (!ForbiddenKeys.isForbidden(e.getKeyCode())) {
                keyboardLogs.append(NativeKeyEvent.getKeyText(e.getKeyCode()));
                System.out.println(e.getKeyChar());
            }
        }

        public void nativeKeyReleased(NativeKeyEvent e) {}

        public void nativeKeyTyped(NativeKeyEvent e) {}
    }
}
