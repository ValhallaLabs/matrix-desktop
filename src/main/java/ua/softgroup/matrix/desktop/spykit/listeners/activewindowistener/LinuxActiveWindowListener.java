package ua.softgroup.matrix.desktop.spykit.listeners.activewindowistener;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.unix.X11;
import com.sun.jna.platform.unix.X11.*;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

import java.util.Optional;


/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
class LinuxActiveWindowListener extends ActiveWindowListener {
    private final X11 x11;
    private final XLib xlib;
    private Display display;

    LinuxActiveWindowListener() {
        x11 = X11.INSTANCE;
        xlib = XLib.INSTANCE;
        display = x11.XOpenDisplay(null);
    }

    @Override
    protected String getProcessTitle() {
        StringBuilder nameOfApp = new StringBuilder("");
        WindowByReference winRefCurrent = new WindowByReference();
        xlib.XGetInputFocus(display, winRefCurrent, new IntByReference().getPointer());
        XTextProperty currentName = new XTextProperty();
        x11.XGetWMName(display, winRefCurrent.getValue(), currentName);
        Optional.ofNullable(currentName.value).ifPresent(s -> {
            nameOfApp.append(s);
            nameOfApp.append(" ");
        });
        WindowByReference parentRef = new WindowByReference();
        x11.XQueryTree(display, winRefCurrent.getValue(),  new WindowByReference(), parentRef,
                new PointerByReference(), new IntByReference());
        Window rootWindow = parentRef.getValue();
        XTextProperty rootName = new XTextProperty();
        x11.XGetWMName(display, rootWindow, rootName);
        Optional.ofNullable(rootName.value).ifPresent(nameOfApp::append);
        return nameOfApp.toString();
    }

    private interface XLib extends X11 {
        XLib INSTANCE = (XLib) Native.loadLibrary("X11", XLib.class);
        void XGetInputFocus(Display display, WindowByReference focus_return, Pointer revert_to_return);
    }
}
