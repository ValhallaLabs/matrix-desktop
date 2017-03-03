package ua.softgroup.matrix.desktop.spykit.interfaces;


import static ua.softgroup.matrix.desktop.spykit.interfaces.SpyKitToolStatus.NOT_USED;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
public abstract class SpyKitTool {
    protected SpyKitToolStatus status = NOT_USED;

    /**
     * Turns on spy kit tool
     * @return boolean result is listener was turned on
     */
    public abstract void turnOn() throws Exception;

    /**
     * Turns off spy kit tool
     * @return boolean result is listener was turned off
     */
    public abstract void turnOff() throws Exception;
}
