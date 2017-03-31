package ua.softgroup.matrix.desktop.spykit.listeners.activewindowistener;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.softgroup.matrix.api.model.datamodels.ActiveWindowModel;
import ua.softgroup.matrix.desktop.spykit.interfaces.SpyKitTool;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static ua.softgroup.matrix.desktop.spykit.interfaces.SpyKitToolStatus.*;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
public abstract class ActiveWindowListener extends SpyKitTool {
    private static final Logger logger = LoggerFactory.getLogger(ActiveWindowListener.class);
    private int time;
    private String currentTitle = "";
    private Disposable titleReaderDisposable;
    private CountDownLatch countDownLatch;
    private List<ActiveWindowModel> activeWindows = new ArrayList<>();

    /**
     * Tries to turn on ActiveWindowListener
     */
    @Override
    public void turnOn() throws InterruptedException {
        if (status == NOT_USED) {
            startTitleReader();
            status = IS_USED;
            logger.info("ActiveWindowListener is turned on successfully");
            (countDownLatch = new CountDownLatch(1)).await();
            return;
        }
        logger.warn("ActiveWindowListener was turned on already");
    }

    /**
     * Gets first active window title as prevTittle for correct first comparison.
     * Creates titleReader disposable.
     */
    private void startTitleReader() {
        addFirstWindowToTimeMap();
        titleReaderDisposable = Observable.interval(4, TimeUnit.SECONDS)
                .map(number -> getProcessTitle())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .filter(this::compareToCurrentTitle)
                .subscribe(this::receiveTitle, Throwable::printStackTrace);
    }

    /**
     * Adds first active window to time map
     */
    private void addFirstWindowToTimeMap() {
        currentTitle = getProcessTitle();
        activeWindows.add(new ActiveWindowModel(currentTitle, LocalDateTime.now(), 0));
        logger.debug("Adding first title: {}", currentTitle);
    }

    /**
     * Abstract method, which every OS overrides to get title of active window.
     * @return newTitle
     */
    protected abstract String getProcessTitle();

    /**
     * Compares new title to current title
     * @return result of comparison.
     */
    private boolean compareToCurrentTitle(String newTitle) {
        if(currentTitle.equals(newTitle)) {
            time += 4;
            return false;
        }
        return true;
    }

    /**
     * Calls method for adding current title to window time map.
     * Resets time, ands set new title as current title.
     * @param newTitle new title
     */
    private void receiveTitle(String newTitle) {
        activeWindows.get(activeWindows.size()-1).setWorkingPeriodSeconds(time);
        activeWindows.add(new ActiveWindowModel(newTitle, LocalDateTime.now(), 0));
        time = 0;
        currentTitle = newTitle;
    }

    /**
     * Turns off ActiveWindowListener.
     */
    @Override
    public void turnOff() {
        if (status == IS_USED) {
            countDownLatch.countDown();
            if (titleReaderDisposable != null && !titleReaderDisposable.isDisposed()) {
                titleReaderDisposable.dispose();
            }
            status = WAS_USED;
            logger.info("ActiveWindow listener is turned off");
            return;
        }
        logger.warn("ActiveWindow listener was turned off already");
    }

    /**
     * Returns activeWindowsModel with titles of active windows.
     * @return activeWindowsModel
     */
    public synchronized List<ActiveWindowModel> getActiveWindows(){
        activeWindows.get(activeWindows.size()-1).setWorkingPeriodSeconds(time);
        activeWindows.add(new ActiveWindowModel(currentTitle, LocalDateTime.now(), 0));
        time = 0;
        List<ActiveWindowModel> windowTimeMap = this.activeWindows;
        this.activeWindows = new ArrayList<>();
        return windowTimeMap;
    }
}
