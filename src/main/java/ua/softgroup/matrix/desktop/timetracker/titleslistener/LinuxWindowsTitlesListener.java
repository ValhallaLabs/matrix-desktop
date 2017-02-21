package ua.softgroup.matrix.desktop.timetracker.titleslistener;

import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.platform.unix.X11;
import com.sun.jna.platform.unix.X11.*;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ua.softgroup.matrix.server.desktop.model.ActiveWindowsModel;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
public class LinuxWindowsTitlesListener {
    private ActiveWindowsModel activeWindowsModel = new ActiveWindowsModel();
    private long time = 0;
    private String prevTitle = "";
    private final X11 x11;
    private final XLib xlib;
    private Display display;
    private CountDownLatch countDownLatch;
    private Disposable titleReaderDisposable;

    public LinuxWindowsTitlesListener() {
        x11 = X11.INSTANCE;
        xlib = XLib.INSTANCE;
        display = x11.XOpenDisplay(null);
    }

    public boolean turnOn() {
        countDownLatch = new CountDownLatch(1);
        try {
            turnOnTitleReader();
            countDownLatch.await();
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void turnOnTitleReader() {
        prevTitle = getProcessTitle();
        titleReaderDisposable = Observable.interval(1000, TimeUnit.MILLISECONDS)
                .map(number -> getProcessTitle())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .filter(this::compareToPreviousTitle)
                .subscribe(this::setNewTitle, Throwable::printStackTrace);
    }

    private boolean compareToPreviousTitle(String title) {
        if(prevTitle.equals(title)) {
            time++;
            return false;
        } else {
            return true;
        }
    }

    private void setNewTitle(String title) {
        addTittleToActiveWindowModelTimeMap();
        System.out.println(activeWindowsModel.getWindowTimeMap());
        time = 0;
        prevTitle = title;
    }

    private void addTittleToActiveWindowModelTimeMap() {
        if (activeWindowsModel.getWindowTimeMap().get(prevTitle) != null) {
            long prevTimeValue = activeWindowsModel.getWindowTimeMap().get(prevTitle);
            activeWindowsModel.getWindowTimeMap().put(prevTitle, prevTimeValue + time);
        } else {
            activeWindowsModel.getWindowTimeMap().put(prevTitle, time);
        }
    }

    private String getProcessTitle() {
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

    public void turnOff() {
        countDownLatch.countDown();
        if(titleReaderDisposable != null && !titleReaderDisposable.isDisposed()) {
            titleReaderDisposable.dispose();
        }
    }

    private interface XLib extends X11 {
        XLib INSTANCE = (XLib) Native.loadLibrary("X11", XLib.class);
        void XGetInputFocus(Display display, WindowByReference focus_return, Pointer revert_to_return);
    }
}
