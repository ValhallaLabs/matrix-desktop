package ua.softgroup.matrix.desktop.spykit.titleslistener;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.unix.X11;
import com.sun.jna.platform.unix.X11.*;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;


/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
class LinuxActiveWindowTitleListener extends ActiveWindowTitleListener {
    private final X11 x11;
    private final XLib xlib;
    private Display display;

    public LinuxActiveWindowTitleListener() {
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
        if(currentName.value != null) {
            nameOfApp.append(currentName.value);
            nameOfApp.append(" ");
        }
        WindowByReference parentRef = new WindowByReference();
        x11.XQueryTree(display, winRefCurrent.getValue(),  new WindowByReference(), parentRef,
                new PointerByReference(), new IntByReference());
        Window rootWindow = parentRef.getValue();
        XTextProperty rootName = new XTextProperty();
        x11.XGetWMName(display, rootWindow, rootName);
        if(rootName.value != null) {
            nameOfApp.append(rootName.value);
        }
        return nameOfApp.toString();
    }

    private interface XLib extends X11 {
        XLib INSTANCE = (XLib) Native.loadLibrary("X11", XLib.class);
        void XGetInputFocus(Display display, WindowByReference focus_return, Pointer revert_to_return);
    }
}
