package ua.softgroup.matrix.desktop.spykit.listeners;

import ua.softgroup.matrix.server.desktop.model.WriteKeyboard;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
public interface SpyKitListener {
    /**
     * Turns on global device listener
     * @return boolean result is listener was turned on
     */
    boolean turnOn();

    /**
     * Turns off global device listener
     * @return boolean result is listener was turned off
     */
    boolean turnOff();
}