package ua.softgroup.matrix.desktop.spykit.listeners.activewindowistener;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.softgroup.matrix.desktop.currentsessioninfo.CurrentSessionInfo;
import ua.softgroup.matrix.desktop.spykit.interfaces.SpyKitListener;
import ua.softgroup.matrix.server.desktop.model.ActiveWindowsModel;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
public abstract class ActiveWindowListener extends SpyKitListener {
    private static final Logger logger = LoggerFactory.getLogger(ActiveWindowListener.class);
    private long time;
    private String currentTitle = "";
    private ActiveWindowsModel activeWindowsModel;
    private Disposable titleReaderDisposable;
    private CountDownLatch countDownLatch;

    public ActiveWindowListener(long projectId) {
        activeWindowsModel = new ActiveWindowsModel(CurrentSessionInfo.getTokenModel().getToken(), projectId);
    }

    /**
     * Tries to turn on ActiveWindowListener
     */
    @Override
    public void turnOn() throws InterruptedException {
        if (status == NOT_USED){
            startTitleReader();
            logger.debug("ActiveWindowListener is turned on successfully");
            status = IS_USED;
            (countDownLatch = new CountDownLatch(1)).await();
        } else {
            logger.debug("ActiveWindowListener was turned on already");
        }
    }

    /**
     * Gets first active window title as prevTittle for correct first comparison.
     * Creates titleReader disposable.
     */
    private void startTitleReader() {
        addFirstWindowToTimeMap();
        titleReaderDisposable = Observable.interval(1000, TimeUnit.MILLISECONDS)
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
        addTittleToActiveWindowModelTimeMap();
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
            time++;
            return false;
        } else {
            return true;
        }
    }

    /**
     * Calls method for adding current title to {@link ActiveWindowsModel}.
     * Resets time, ands set new title as current title.
     * @param newTitle new title
     */
    private void receiveTitle(String newTitle) {
        addTittleToActiveWindowModelTimeMap();
        time = 0;
        currentTitle = newTitle;
    }

    /**
     * Checks if current title is exist in window time map.
     * If exist, it adds time to existed.
     * If not exist, it adds new title with time to window time map.
     */
    private synchronized void addTittleToActiveWindowModelTimeMap() {
        if (activeWindowsModel.getWindowTimeMap().get(currentTitle) != null) {
            long prevTimeValue = activeWindowsModel.getWindowTimeMap().get(currentTitle);
            activeWindowsModel.getWindowTimeMap().put(currentTitle, prevTimeValue + time);
        } else {
            activeWindowsModel.getWindowTimeMap().put(currentTitle, time);
        }
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
            logger.debug("ActiveWindow listener is turned off");
        } else {
            logger.debug("ActiveWindow listener was turned off already");
        }
    }

    /**
     * Returns activeWindowsModel with titles of active windows.
     * @return activeWindowsModel
     */
    @Override
    public synchronized ActiveWindowsModel getLogs(){
        addTittleToActiveWindowModelTimeMap();
        time = 0;
        return activeWindowsModel;
    }
}
