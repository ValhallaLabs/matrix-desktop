package ua.softgroup.matrix.desktop.spykit.listeners;

import org.jnativehook.NativeHookException;
import ua.softgroup.matrix.server.desktop.model.WriteKeyboard;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
public interface SpyKitListener {
    /**
     * Turns on global device listener
     * @return boolean result is listener was turned on
     */
    void turnOn() throws InterruptedException, NativeHookException;

    /**
     * Turns off global device listener
     * @return boolean result is listener was turned off
     */
    void turnOff() throws NativeHookException;
}