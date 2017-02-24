package ua.softgroup.matrix.desktop.spykit.interfaces;


/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
public abstract class SpyKitTool {
    protected static final int NOT_USED = 0, IS_USED = 1,WAS_USED = -1;
    protected int status;

    /**
     * Turns on global device listener
     * @return boolean result is listener was turned on
     */
    public abstract void turnOn() throws Exception;

    /**
     * Turns off global device listener
     * @return boolean result is listener was turned off
     */
    public abstract void turnOff() throws Exception;
}
