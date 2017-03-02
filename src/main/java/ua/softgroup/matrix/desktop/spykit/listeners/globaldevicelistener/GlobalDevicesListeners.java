package ua.softgroup.matrix.desktop.spykit.listeners.globaldevicelistener;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.softgroup.matrix.desktop.spykit.interfaces.SpyKitTool;

import java.awt.*;
import java.util.EventListener;
import java.util.logging.Level;

import static java.util.logging.Logger.getLogger;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
class GlobalDevicesListeners extends SpyKitTool {
    private static final Logger logger = LoggerFactory.getLogger(GlobalDevicesListeners.class);
    private IdleListener idleListener;
    private StringBuilder keyboardLogs;
    private double mouseFootage;
    private Point prevMousePosition;
    private EventListener globalMouseWheelListener;
    private EventListener globalMouseListener;
    private EventListener globalKeyListener;

    GlobalDevicesListeners(IdleListener idleListener) {
        this.idleListener = idleListener;
        keyboardLogs = new StringBuilder("");
        prevMousePosition = MouseInfo.getPointerInfo().getLocation();
        getLogger(GlobalScreen.class.getPackage().getName()).setLevel(Level.OFF);
    }

    @Override
    public void turnOn() throws NativeHookException {
        addListenersToGlobalListener();
        GlobalScreen.registerNativeHook();
        logger.debug("Native devices listener is turned on");
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

    @Override
    public void turnOff() throws NativeHookException {
        removeListenersFromGlobalListener();
        GlobalScreen.unregisterNativeHook();
        logger.debug("Native devices listener is turned off");
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
    synchronized String getKeyboardLogs() {
        String keyboardLogs = this.keyboardLogs.toString();
        this.keyboardLogs = new StringBuilder("");
        return keyboardLogs;
    }

    /**
     * Returns a double primitive with mouse's footage. Reset mouse's footage.
     * @return mouseFootage
     */
    synchronized double getMouseFootage() {
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
            idleListener.receiveEvent(e);
        }
    }

    private class GlobalMouseListener implements NativeMouseInputListener {
        private final static double PIXELS_PER_METER = 3779.5275;

        /**
         * Send event of mouse buttons into emitters.
         * @param e native key event
         */
        public void nativeMousePressed(NativeMouseEvent e) {
            idleListener.receiveEvent(e);
        }

        /**
         * Send event of mouse moves into emitters and calculate mouse footage in meters.
         * @param e native key event
         */
        public void nativeMouseMoved(NativeMouseEvent e) {
            mouseFootage += (Math.sqrt(Math.pow(e.getX() - prevMousePosition.getX(), 2) +
                    Math.pow(e.getY() - prevMousePosition.getY(), 2)))/PIXELS_PER_METER;
            prevMousePosition.setLocation(e.getX(), e.getY());
            idleListener.receiveEvent(e);
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
            idleListener.receiveEvent(e);
            if (!ForbiddenKeys.isForbidden(e.getKeyCode())) {
                keyboardLogs.append(NativeKeyEvent.getKeyText(e.getKeyCode()));
                System.out.println(e.getKeyChar());
            }
        }

        public void nativeKeyReleased(NativeKeyEvent e) {}

        public void nativeKeyTyped(NativeKeyEvent e) {}
    }
}
