package ua.softgroup.matrix.desktop.timetracker;

import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.platform.unix.X11;
import com.sun.jna.platform.win32.Psapi;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.org.apache.xpath.internal.SourceTree;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
public class WindowsTitleHoooker {
    private static final int MAX_TITLE_LENGTH = 1024;

    public static void main(String[] args) throws Exception {
        if (Platform.isWindows()) {
            char[] buffer = new char[MAX_TITLE_LENGTH * 2];
            WinDef.HWND hwnd = User32.INSTANCE.GetForegroundWindow();
            User32.INSTANCE.GetWindowText(hwnd, buffer, MAX_TITLE_LENGTH);
            System.out.println("Active window title: " + Native.toString(buffer));
        }
        if (Platform.isLinux()) {

            System.err.println("Linux platform");

            final X11 x11 = X11.INSTANCE;
            final XLib xlib = XLib.INSTANCE;

            X11.Display display = x11.XOpenDisplay(null);

            //X11.Window window = new X11.Window();
            X11.WindowByReference winRef = new X11.WindowByReference();

            IntByReference intByReference = new IntByReference();

            xlib.XGetInputFocus(display, winRef, intByReference.getPointer());

            X11.XTextProperty name = new X11.XTextProperty();
            x11.XGetWMName(display, winRef.getValue(), name);
            System.out.println(name.toString());
        }
//        if(Platform.isMac()) {
//            final String script="tell application \"System Events\"\n" +
//                    "\tname of application processes whose frontmost is tru\n" +
//                    "end";
//            ScriptEngine appleScript=new ScriptEngineManager().getEngineByName("AppleScript");
//            String result=(String)appleScript.eval(script);
//            System.out.println(result);
//        }



    }

    public interface XLib extends X11 {
        XLib INSTANCE = (XLib) Native.loadLibrary("X11", XLib.class);
        //void XGetInputFocus(X11.Display display, X11.Window focus_return, Pointer revert_to_return);
        void XGetInputFocus(X11.Display display, X11.WindowByReference focus_return, Pointer revert_to_return);
    }

}






