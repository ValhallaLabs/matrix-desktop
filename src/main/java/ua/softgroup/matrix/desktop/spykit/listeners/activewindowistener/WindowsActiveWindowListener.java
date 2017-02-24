package ua.softgroup.matrix.desktop.spykit.listeners.activewindowistener;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
class WindowsActiveWindowListener extends ActiveWindowListener {
    private static final int MAX_TITLE_LENGTH = 1024;

    WindowsActiveWindowListener(long projectId) {
        super(projectId);
    }

    @Override
    protected String getProcessTitle() {
        char[] buffer = new char[MAX_TITLE_LENGTH * 2];
        WinDef.HWND hwnd = User32.INSTANCE.GetForegroundWindow();
        User32.INSTANCE.GetWindowText(hwnd, buffer, MAX_TITLE_LENGTH);
        return Native.toString(buffer);
    }
}
