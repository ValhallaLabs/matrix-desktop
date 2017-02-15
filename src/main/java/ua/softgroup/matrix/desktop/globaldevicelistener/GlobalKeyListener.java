package ua.softgroup.matrix.desktop.globaldevicelistener;

import io.reactivex.Emitter;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.util.EventObject;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
class GlobalKeyListener implements NativeKeyListener {
    private Emitter<EventObject> eventObjectEmitter;

    public GlobalKeyListener(Emitter<EventObject> eventObjectEmitter) {
        this.eventObjectEmitter = eventObjectEmitter;
    }

    public void nativeKeyPressed(NativeKeyEvent e) {
        eventObjectEmitter.onNext(e);
//        System.out.println("Key Pressed: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
    }

    public void nativeKeyReleased(NativeKeyEvent e) {
        eventObjectEmitter.onNext(e);
//        System.out.println("Key Released: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
    }

    public void nativeKeyTyped(NativeKeyEvent e) {
        eventObjectEmitter.onNext(e);
//        System.out.println("Key Typed: " + e.getKeyText(e.getKeyCode()));
    }
}
