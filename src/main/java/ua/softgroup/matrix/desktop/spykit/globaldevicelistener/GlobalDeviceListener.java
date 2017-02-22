package ua.softgroup.matrix.desktop.spykit.globaldevicelistener;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
public interface GlobalDeviceListener {
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
