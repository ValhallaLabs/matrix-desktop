package ua.softgroup.matrix.desktop.spykit.listeners.activewindowistener;

import com.sun.jna.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
public class ActiveWindowListenerFactory {
    private static final Logger logger = LoggerFactory.getLogger(ActiveWindowListenerFactory.class);

    /**
     * Returns window listener for detected platform or null if platform is unknown.
     * @return window listener
     */
    public static ActiveWindowListener getListener() throws XdotoolException {
        if (Platform.isWindows()) {
            logger.info("Platform is Windows");
            return new WindowsActiveWindowListener();
        }
        if (Platform.isLinux()) {
            logger.info("Platform is Linux");
            return getLinuxActiveWindowListener();
        }
        if (Platform.isMac()) {
            logger.info("Platform is Mac");
            return new MacOsActiveWindowListener();
        }
        logger.info("Platform is not detected");
        return null;
    }

    private static ActiveWindowListener getLinuxActiveWindowListener() throws XdotoolException {
        try {
            Process p = Runtime.getRuntime().exec("xdotool getwindowfocus getwindowname");
            p.waitFor();
            p.destroy();
            return new LinuxActiveWindowListener();
        } catch (InterruptedException | IOException e) {
            throw new XdotoolException();
        }
    }

    public static class XdotoolException extends Exception {
        public XdotoolException() { super(); }
        public XdotoolException(String message) { super(message); }
        public XdotoolException(String message, Throwable cause) { super(message, cause); }
        public XdotoolException(Throwable cause) { super(cause); }
    }
}
