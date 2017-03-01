package ua.softgroup.matrix.desktop.spykit.screenshooter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
public class ScreenShooter {
    private static final Logger logger = LoggerFactory.getLogger(ScreenShooter.class);

    /**
     * Tries to make screenshot
     * @return screenshotModel model with screenshot
     */
    public byte[] makeScreenshot() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(getScreenCapture(), "png", baos);
            ImageIO.write(getScreenCapture(), "png", new File("black.png"));
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();
            logger.debug("Screenshot was created successfully");
            return imageInByte;
        } catch (AWTException | IOException | NullPointerException e) {
            logger.debug("Screenshot was created unsuccessfully", e);
        }
        return null;
    }

    /**
     * Gets bounds of active monitor for screenshot
     * @return rectangle area for screenshot
     */
    private BufferedImage getScreenCapture() throws AWTException, NullPointerException {
        BufferedImage original = new Robot().createScreenCapture(MouseInfo.getPointerInfo().getDevice()
                .getDefaultConfiguration().getBounds());
        BufferedImage grayFiltered =  new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        grayFiltered.getGraphics().drawImage(original, 0, 0, null);
        return grayFiltered;
    }
}
