package ua.softgroup.matrix.desktop.spykit.listeners.globaldevicelistener;

import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.mouse.NativeMouseEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;


/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
class ForbiddenKeys {
    private static int[] forbiddenKeysArray = {
            NativeKeyEvent.VC_CONTROL, NativeKeyEvent.VC_ALT, NativeKeyEvent.VC_SHIFT, NativeKeyEvent.VC_CAPS_LOCK,
            NativeKeyEvent.VC_TAB, NativeKeyEvent.VC_BACKSPACE, NativeKeyEvent.VC_ENTER,
            NativeKeyEvent.VC_LEFT, NativeKeyEvent.VC_UP, NativeKeyEvent.VC_DOWN, NativeKeyEvent.VC_RIGHT,
            NativeKeyEvent.VC_INSERT, NativeKeyEvent.VC_HOME, NativeKeyEvent.VC_PAGE_UP, NativeKeyEvent.VC_PAGE_DOWN,
            NativeKeyEvent.VC_DELETE, NativeKeyEvent.VC_END, NativeKeyEvent.VC_PRINTSCREEN, NativeKeyEvent.VC_SCROLL_LOCK,
            NativeKeyEvent.VC_PAUSE, NativeKeyEvent.VC_NUM_LOCK,
            NativeKeyEvent.VC_F1, NativeKeyEvent.VC_F2, NativeKeyEvent.VC_F3, NativeKeyEvent.VC_F4, NativeKeyEvent.VC_F5,
            NativeKeyEvent.VC_F6, NativeKeyEvent.VC_F7, NativeKeyEvent.VC_F8, NativeKeyEvent.VC_F9, NativeKeyEvent.VC_F10,
            NativeKeyEvent.VC_F11, NativeKeyEvent.VC_F12};

    static boolean isForbidden(int keyCode) {
        return IntStream.of(forbiddenKeysArray).anyMatch(x -> x == keyCode);
    }
}
