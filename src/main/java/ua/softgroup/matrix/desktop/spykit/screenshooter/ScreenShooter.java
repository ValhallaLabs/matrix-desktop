package ua.softgroup.matrix.desktop.spykit.screenshooter;

import com.sun.istack.internal.Nullable;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.softgroup.matrix.desktop.currentsessioninfo.CurrentSessionInfo;
import ua.softgroup.matrix.server.desktop.model.ClientSettingsModel;
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

    /**
     * Tries to make screenshot
     * @return screenshotModel model with screenshot
     */
    @Nullable
    private ScreenshotModel makeScreenshot(long projectId) {
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
}
