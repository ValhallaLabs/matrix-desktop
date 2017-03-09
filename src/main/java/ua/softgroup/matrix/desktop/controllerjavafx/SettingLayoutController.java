package ua.softgroup.matrix.desktop.controllerjavafx;

import javafx.event.ActionEvent;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.softgroup.matrix.desktop.utils.ConfigManager;
import ua.softgroup.matrix.desktop.utils.SocketProvider;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.CREATE_NEW;

/**
 * @author Andrii Bei <sg.andriy2@gmail.com>
 */
public class SettingLayoutController {
    public static final Logger logger = LoggerFactory.getLogger(SettingLayoutController.class);

    /**
     * TODO:
     * 1) Provide method for set a host and port to view. To get values use methods {@link ConfigManager#getHost()}
     * and {@link ConfigManager#getPort()}.
     * 2) Provide listener for set configs to default . Use method {@link ConfigManager#setConfigToDefault()}
     * 3) Provide listener for saving new configs. Use method {@link ConfigManager#saveNewConfig(String, String)}
     */
}
