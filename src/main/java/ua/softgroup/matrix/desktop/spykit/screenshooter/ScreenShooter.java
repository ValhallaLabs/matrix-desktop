package ua.softgroup.matrix.desktop.spykit.screenshooter;

import com.sun.istack.internal.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.softgroup.matrix.desktop.currentsessioninfo.CurrentSessionInfo;
import ua.softgroup.matrix.server.desktop.model.ScreenshotModel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
public class ScreenShooter {
    private static final Logger logger = LoggerFactory.getLogger(ScreenShooter.class);
    private long projectId;

    public ScreenShooter(long projectId) {
        this.projectId = projectId;
    }

    /**
     * Tries to make screenshot
     * @return screenshotModel model with screenshot
     */
    @Nullable
    public ScreenshotModel makeScreenshot() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            File file = new File("scr.png");
            ImageIO.write(getScreenCapture(), "png", new File("matrix.png"));
            ImageIO.write(getScreenCapture(), "png", baos);
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();
            logger.debug("Screenshot was created successfully");
            return new ScreenshotModel(CurrentSessionInfo.getTokenModel().getToken(), imageInByte, projectId);
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
        return new Robot().createScreenCapture(MouseInfo.getPointerInfo().getDevice()
                .getDefaultConfiguration().getBounds());
    }

    //TODO: implement or remove this code
//    Snippet code for converting image into grey and black&white

//    master = ImageIO.read(new File("C:/Users/shane/Dropbox/pictures/439px-Join!_It's_your_duty!.jpg"));
//    grayScale = ImageIO.read(new File("C:/Users/shane/Dropbox/pictures/439px-Join!_It's_your_duty!.jpg"));
//    ColorConvertOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
//    op.filter(grayScale, grayScale);
//
//    blackWhite = new BufferedImage(master.getWidth(), master.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
//    Graphics2D g2d = blackWhite.createGraphics();
//    g2d.drawImage(master, 0, 0, this);
//    g2d.dispose();
}
