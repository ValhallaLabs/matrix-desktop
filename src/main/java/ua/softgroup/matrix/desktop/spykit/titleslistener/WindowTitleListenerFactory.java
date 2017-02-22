package ua.softgroup.matrix.desktop.spykit.titleslistener;

import com.sun.jna.Platform;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
public class WindowTitleListenerFactory {

    public static ActiveWindowTitleListener getListener() {
        if (Platform.isWindows()) {
            return new WindowsActiveWindowTitleListener();
        }
        if (Platform.isLinux()) {
            return new LinuxActiveWindowTitleListener();
        }
        if (Platform.isMac()) {
            return new MacOsActiveWindowTitleListener();
        }
        return null;
    }
}
