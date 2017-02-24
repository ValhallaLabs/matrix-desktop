package ua.softgroup.matrix.desktop.spykit.listeners.activewindowistener;

import com.sun.istack.internal.Nullable;
import com.sun.jna.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
public class ActiveWindowListenerFactory {
    private static final Logger logger = LoggerFactory.getLogger(ActiveWindowListenerFactory.class);

    /**
     * Returns window listener for detected platform or null if platform is unknown.
     * @return window listener
     */
    @Nullable
    public static ActiveWindowListener getListener(long projectId) {
        if (Platform.isWindows()) {
            logger.debug("Platform is Windows");
            return new WindowsActiveWindowListener(projectId);
        }
        if (Platform.isLinux()) {
            logger.debug("Platform is Linux");
            return new LinuxActiveWindowListener(projectId);
        }
        if (Platform.isMac()) {
            logger.debug("Platform is Mac");
            return new MacOsActiveWindowListener(projectId);
        }
        logger.debug("Platform is not detected");
        return null;
    }
}
