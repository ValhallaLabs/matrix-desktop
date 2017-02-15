package ua.softgroup.matrix.desktop.globaldevicelistener;

import io.reactivex.Emitter;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.slf4j.LoggerFactory;
import ua.softgroup.matrix.desktop.sessionmanagers.AuthenticationServerSessionManager;
import ua.softgroup.matrix.server.desktop.model.UserPassword;

import java.util.EventObject;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
public class GlobalDevicesListener {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(GlobalDevicesListener.class);
    private static GlobalDevicesListener globalDevicesListener;
    private Emitter<EventObject> eventObjectEmitter;
    private Disposable downTimeDisposable;


    //TODO: transform global device listener into singleton
    public GlobalDevicesListener getInstance() {
        if (globalDevicesListener == null) {
            globalDevicesListener = new GlobalDevicesListener();
        }
        return globalDevicesListener;
    }

    private GlobalDevicesListener() {
        offGlobalScreenLogger();
    }

    private void offGlobalScreenLogger() {
        java.util.logging.Logger logger = java.util.logging.Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
    }



    public void turnOn() throws NativeHookException {
        logger.debug("Global devices listener turn on");
        downTimeDisposable = createDownTimeObservable();
        addListenersToGlobalListener();
        GlobalScreen.registerNativeHook();
    }

    private void addListenersToGlobalListener(){
        GlobalScreen.addNativeMouseWheelListener(new GlobalMouseWheelListener(eventObjectEmitter));
        GlobalMouseListener example = new GlobalMouseListener(eventObjectEmitter);
        GlobalScreen.addNativeMouseListener(example);
        GlobalScreen.addNativeMouseMotionListener(example);
        GlobalScreen.addNativeKeyListener(new GlobalKeyListener(eventObjectEmitter));
    }
    private Disposable createDownTimeObservable() {
        return Observable.create(this::createObservableEmitter)
                .debounce(1000, TimeUnit.MILLISECONDS)
                .subscribe(eventObject -> {
                    System.out.println("Start downtime");
                });
    }

    private void createObservableEmitter(ObservableEmitter<EventObject> e) {
        eventObjectEmitter = e;
        eventObjectEmitter.onNext(new EventObject(this));
    }



    //TODO: find out how correctly turn off listener. Realise resources of small listeners.
    public void turnOff() throws NativeHookException {
        GlobalScreen.unregisterNativeHook();
    }

    //TODO: Temporary, delete after merging
    public static void main(String[] args) {
        GlobalDevicesListener globalDevicesListener = new GlobalDevicesListener();
        try {
            globalDevicesListener.turnOn();
        } catch (NativeHookException e) {
            e.printStackTrace();
        }
    }
}
