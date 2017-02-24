package ua.softgroup.matrix.desktop.spykit.interfaces;

import org.jnativehook.NativeHookException;
import ua.softgroup.matrix.desktop.spykit.interfaces.SpyKitTool;
import ua.softgroup.matrix.server.desktop.model.TokenModel;
import ua.softgroup.matrix.server.desktop.model.WriteKeyboard;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
public abstract class SpyKitListener extends SpyKitTool {

    /**
     * Returns model with logs.
     * @param <T> models which contains logs and extends TokenModel
     * @return model with logs
     */
    public abstract <T extends TokenModel> T getLogs();
}