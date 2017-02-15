package ua.softgroup.matrix.desktop.globaldevicelistener;

import io.reactivex.Emitter;
import org.jnativehook.mouse.NativeMouseWheelEvent;
import org.jnativehook.mouse.NativeMouseWheelListener;

import java.util.EventObject;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
class GlobalMouseWheelListener implements NativeMouseWheelListener {
    private Emitter<EventObject> eventObjectEmitter;

    public GlobalMouseWheelListener(Emitter<EventObject> eventObjectEmitter) {
        this.eventObjectEmitter = eventObjectEmitter;
    }

    public void nativeMouseWheelMoved(NativeMouseWheelEvent e) {
        eventObjectEmitter.onNext(e);
//        System.out.println("Mosue Wheel Moved: " + e.getWheelRotation());
    }
}
