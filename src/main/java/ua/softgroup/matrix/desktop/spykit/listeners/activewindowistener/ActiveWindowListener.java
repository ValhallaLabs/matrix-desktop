package ua.softgroup.matrix.desktop.spykit.listeners.activewindowistener;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.softgroup.matrix.desktop.spykit.interfaces.SpyKitTool;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static ua.softgroup.matrix.desktop.spykit.interfaces.SpyKitToolStatus.IS_USED;
import static ua.softgroup.matrix.desktop.spykit.interfaces.SpyKitToolStatus.NOT_USED;
import static ua.softgroup.matrix.desktop.spykit.interfaces.SpyKitToolStatus.WAS_USED;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
public abstract class ActiveWindowListener extends SpyKitTool {
    private static final Logger logger = LoggerFactory.getLogger(ActiveWindowListener.class);
    private long time;
    private String currentTitle = "";
    private Disposable titleReaderDisposable;
    private CountDownLatch countDownLatch;
    private Map<String, Long> windowTimeMap = new LinkedHashMap<>();
    /**
     * Tries to turn on ActiveWindowListener
     */
    @Override
    public void turnOn() throws InterruptedException {
        if (status == NOT_USED) {
            startTitleReader();
            status = IS_USED;
            logger.debug("ActiveWindowListener is turned on successfully");
            (countDownLatch = new CountDownLatch(1)).await();
            return;
        }
        logger.debug("ActiveWindowListener was turned on already");
    }

    /**
     * Gets first active window title as prevTittle for correct first comparison.
     * Creates titleReader disposable.
     */
    private void startTitleReader() {
        addFirstWindowToTimeMap();
        titleReaderDisposable = Observable.interval(1, TimeUnit.SECONDS)
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
        }
        return true;
    }

    /**
     * Calls method for adding current title to window time map.
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
        if (windowTimeMap.get(currentTitle) != null) {
            long prevTimeValue = windowTimeMap.get(currentTitle);
            windowTimeMap.put(currentTitle, prevTimeValue + time);
            return;
        }
        windowTimeMap.put(currentTitle, time);
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
            return;
        }
        logger.debug("ActiveWindow listener was turned off already");
    }

    /**
     * Returns activeWindowsModel with titles of active windows.
     * @return activeWindowsModel
     */
    public synchronized Map<String, Long> getWindowTimeMap(){
        addTittleToActiveWindowModelTimeMap();
        time = 0;
        return windowTimeMap;
    }
}
