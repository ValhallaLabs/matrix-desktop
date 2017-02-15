package ua.softgroup.matrix.desktop.globaldevicelistener;


import io.reactivex.Emitter;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;

import java.util.EventObject;


/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
class GlobalMouseListener implements NativeMouseInputListener {
    private Emitter<EventObject> eventObjectEmitter;

    public GlobalMouseListener(Emitter<EventObject> eventObjectEmitter) {
        this.eventObjectEmitter = eventObjectEmitter;
    }

    public void nativeMouseClicked(NativeMouseEvent e) {
        eventObjectEmitter.onNext(e);
//        System.out.println("Mouse Clicked: " + e.getClickCount());
    }

    public void nativeMousePressed(NativeMouseEvent e) {
        eventObjectEmitter.onNext(e);
//        System.out.println("Mouse Pressed: " + e.getButton());
    }

    public void nativeMouseReleased(NativeMouseEvent e) {
        eventObjectEmitter.onNext(e);
//        System.out.println("Mouse Released: " + e.getButton());
    }

    public void nativeMouseMoved(NativeMouseEvent e) {
        eventObjectEmitter.onNext(e);
//        System.out.println("Mouse Moved: " + e.getX() + ", " + e.getY());
    }

    public void nativeMouseDragged(NativeMouseEvent e) {
        eventObjectEmitter.onNext(e);
//        System.out.println("Mouse Dragged: " + e.getX() + ", " + e.getY());
    }
}
