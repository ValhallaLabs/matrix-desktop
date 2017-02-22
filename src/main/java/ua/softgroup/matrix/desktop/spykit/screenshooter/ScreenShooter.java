package ua.softgroup.matrix.desktop.spykit.screenshooter;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.softgroup.matrix.desktop.currentsessioninfo.CurrentSessionInfo;
import ua.softgroup.matrix.server.desktop.model.ScreenshotModel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
public class ScreenShooter {
    private static final Logger logger = LoggerFactory.getLogger(ScreenShooter.class);
    private int screenshotUpdateFrequently;
    private CountDownLatch countDownLatch;
    private Disposable shooterDisposable;
    private long projectId;

    public ScreenShooter(long projectId) {
        this.projectId = projectId;
        checkClientSettings();
    }

    /**
     * Checks client settings from {@link CurrentSessionInfo} for screenshotUpdateFrequently.
     * If screenshotUpdateFrequently is zero, it sets default value in 60 minutes
     */
    private void checkClientSettings() {
        if (CurrentSessionInfo.getClientSettingsModel().getScreenshotUpdateFrequently() == 0) {
            screenshotUpdateFrequently = 60;
        } else {
            screenshotUpdateFrequently = CurrentSessionInfo.getClientSettingsModel().getScreenshotUpdateFrequently();
        }
    }

    /**
     * Tries to turn on shooter.
     * @return turnOnResult is ScreenShooter turned on
     */
    public boolean turnOn() {
        countDownLatch = new CountDownLatch(1);
        try {
            turnOnShooter();
            countDownLatch.await();
            logger.debug("ScreenShooter is turned on successfully");
            return true;
        } catch (InterruptedException e) {
            logger.debug("ScreenShooter is turned on unsuccessfully:", e);
            return false;
        }
    }

    /**
     * Creates shooter disposable
     */
    private void turnOnShooter() {
        shooterDisposable = Observable.interval(screenshotUpdateFrequently, TimeUnit.MINUTES)
                .map(number -> makeScreenshot())
                .filter(this::isScreenshotWasMade)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(this::sendScreenShotToServer, this::handleException);
    }

    /**
     * Tries to make screenshot
     * @return screenshotModel model with screenshot
     */
    private ScreenshotModel makeScreenshot() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(new Robot().createScreenCapture(getVirtualBound()), "png", baos);
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();
            logger.debug("Screenshot was created successfully");
            return new ScreenshotModel(imageInByte, projectId);
        } catch (AWTException | IOException e) {
            logger.debug("Screenshot was created unsuccessfully", e);
        }
        return null;
    }

    /**
     * Gets bounds of all screens for screenshot
     * @return rectangle area for screenshot
     */
    private Rectangle getVirtualBound() {
        Rectangle virtualBounds = new Rectangle();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();
        for (GraphicsDevice gd : gs) {
            GraphicsConfiguration[] gc = gd.getConfigurations();
            for (GraphicsConfiguration aGc : gc) {
                virtualBounds = virtualBounds.union(aGc.getBounds());
            }
        }
        return virtualBounds;
    }

    /**
     * Checks was screenshot made correctly
     * @return result of check
     */
    private boolean isScreenshotWasMade(ScreenshotModel screenshotModel) {
        if(screenshotModel != null){
            return true;
        }
        return false;
    }

    /**
     * Send screenshot model to server
     * @param screenshotModel DTO with screenschot
     */
    private void sendScreenShotToServer(ScreenshotModel screenshotModel) {
        //TODO: send screenshot to server
    }

    /**
     * Prints exception what may happen
     * @param throwable throwable object
     */
    private void handleException(Throwable throwable) {
        //TODO: find out what to do with exceptions
        logger.debug("Something went wrong", throwable);
    }

    /**
     * Turns off screen shooter
     */
    public void turnOff() {
        countDownLatch.countDown();
        if(shooterDisposable != null && !shooterDisposable.isDisposed()) {
            shooterDisposable.dispose();
        }
    }
}
