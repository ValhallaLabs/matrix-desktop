package ua.softgroup.matrix.desktop.spykit.titleslistener;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.softgroup.matrix.desktop.currentsessioninfo.CurrentSessionInfo;
import ua.softgroup.matrix.server.desktop.model.ActiveWindowsModel;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
public abstract class ActiveWindowTitleListener {
    protected static final Logger logger = LoggerFactory.getLogger(ActiveWindowTitleListener.class);
    private ActiveWindowsModel activeWindowsModel = new ActiveWindowsModel();
    private CountDownLatch countDownLatch;
    private long time = 0;
    private String prevTitle = "";
    private Disposable titleReaderDisposable;


    //TODO: if we have frequence, it will work independently. If not, we will connect it to time tracker.

    public boolean turnOn() {
        countDownLatch = new CountDownLatch(1);
        try {
            startTitleReader();
            countDownLatch.await();
            logger.debug("ActiveWindowTitleListener is turned on successfully");
            return true;
        } catch (InterruptedException e) {
            logger.debug("ActiveWindowTitleListener is turned on unsuccessfully:", e);
            return false;
        }
    }

    private void startTitleReader() {
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

    protected abstract String getProcessTitle();

    public void turnOff() {
        countDownLatch.countDown();
        if(titleReaderDisposable != null && !titleReaderDisposable.isDisposed()) {
            titleReaderDisposable.dispose();
        }
    }
}
