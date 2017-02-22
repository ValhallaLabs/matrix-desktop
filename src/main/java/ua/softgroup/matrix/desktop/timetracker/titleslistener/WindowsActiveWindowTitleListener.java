package ua.softgroup.matrix.desktop.timetracker.titleslistener;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
public class WindowsActiveWindowTitleListener extends ActiveWindowTitleListener {
    private static final int MAX_TITLE_LENGTH = 1024;

    @Override
    protected String getProcessTitle() {
        char[] buffer = new char[MAX_TITLE_LENGTH * 2];
        WinDef.HWND hwnd = User32.INSTANCE.GetForegroundWindow();
        User32.INSTANCE.GetWindowText(hwnd, buffer, MAX_TITLE_LENGTH);
        return Native.toString(buffer);
    }

    public static void main(String[] args) {
        WindowsActiveWindowTitleListener windowsActiveWindowTitleListener = new WindowsActiveWindowTitleListener();
        windowsActiveWindowTitleListener.turnOn();
    }
}
