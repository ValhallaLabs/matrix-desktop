package ua.softgroup.matrix.desktop.spykit.listeners.titleslistener;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.softgroup.matrix.desktop.spykit.listeners.SpyKitListener;
import ua.softgroup.matrix.server.desktop.model.ActiveWindowsModel;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
public abstract class ActiveWindowListener implements SpyKitListener {
    protected static final Logger logger = LoggerFactory.getLogger(ActiveWindowListener.class);
    private boolean isWorking = false;
    private long time;
    private String currentTitle = "";
    private ActiveWindowsModel activeWindowsModel;
    private Disposable titleReaderDisposable;
    private CountDownLatch countDownLatch;

    public ActiveWindowListener(long projectId) {
        activeWindowsModel = new ActiveWindowsModel(projectId);
    }

    /**
     * Tries to turn on ActiveWindowListener
     * @return result of turning of ActiveWindowListener
     */
    @Override
    public boolean turnOn() {
        if (!isWorking){
            countDownLatch = new CountDownLatch(1);
            try {
                startTitleReader();
                countDownLatch.await();
                logger.debug("ActiveWindowListener is turned on successfully");
                return true;
            } catch (InterruptedException e) {
                logger.debug("ActiveWindowListener is turned on unsuccessfully:", e);
                return false;
            }
        } else {
            logger.debug("ActiveWindowListener is turned on already");
            return false;
        }
    }

    /**
     * Gets first active window title as prevTittle for correct first comparison.
     * Creates titleReader disposable.
     */
    private void startTitleReader() {
        currentTitle = getProcessTitle();
        titleReaderDisposable = Observable.interval(1000, TimeUnit.MILLISECONDS)
                .map(number -> getProcessTitle())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .filter(this::compareToCurrentTitle)
                .subscribe(this::receiveTitle, Throwable::printStackTrace);
    }

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
     * Abstract method, which every OS overrides to get title of active window.
     * @return newTitle
     */
    protected abstract String getProcessTitle();

    /**
     * Turns off ActiveWindowListener.
     */
    @Override
    public boolean turnOff() {
        if (isWorking) {
            countDownLatch.countDown();
            if (titleReaderDisposable != null && !titleReaderDisposable.isDisposed()) {
                titleReaderDisposable.dispose();
            }
            logger.debug("ActiveWindow listener is turned off");
            return true;
        } else {
            logger.debug("ActiveWindow listener is turned off already");
            return false;
        }
    }

    /**
     * Returns activeWindowsModel with titles of active windows.
     * @return activeWindowsModel
     */
    public synchronized ActiveWindowsModel getActiveWindowsModel(){
        return activeWindowsModel;
    }
}
