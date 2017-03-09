package ua.softgroup.matrix.desktop.utils;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.CREATE_NEW;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
public class ConfigManager {
    public static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);

    /**
     * Method reads host and port values from config file and sets it to SocketProvider.
     * In case of an error, sets it to default.
     */
    public static void readConfig() {
        try {
            CompositeConfiguration config = getConfig();
            SocketProvider.setHostName(config.getString("host"));
            SocketProvider.setPortNumber(config.getInt("port"));
            logger.debug("Server IP: {}:{}", SocketProvider.getHostName(), SocketProvider.getPortNumber());
        } catch (ConfigurationException | IOException e) {
            logger.debug("Config is not found. Try to set to default.");
            setConfigToDefault();
        }
    }

    /**
     * Method returns a {@link CompositeConfiguration} object with current configs
     * @return configs
     * @throws IOException
     * @throws ConfigurationException
     */
    private static CompositeConfiguration getConfig() throws IOException, ConfigurationException {
        checkConfigFileExistence();
        CompositeConfiguration config = new CompositeConfiguration();
        config.addConfiguration(new PropertiesConfiguration("config.properties"));
        return config;
    }

    /**
     * Method checks existence of config file. In negative case, it creates new config file with default values.
     * @throws IOException
     */
    private static void checkConfigFileExistence() throws IOException {
        Path path = Paths.get("config.properties");
        byte defaultConfig[] = "host=192.168.11.84\nport=6666".getBytes();
        try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(path, CREATE_NEW))) {
            out.write(defaultConfig, 0, defaultConfig.length);
            logger.debug("Config file was removed. New config file is created.");
        } catch (IOException x) {
            logger.debug("Config file is already exist");
        }
    }

    /**
     * Method tries to set config to default values. In case of an exception, it will do nothing.
     */
    public static void setConfigToDefault() {
        Path path = Paths.get("config.properties");
        byte defaultConfig[] = "host=192.168.11.84\nport=6666".getBytes();
        try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(path, CREATE))) {
            out.write(defaultConfig, 0, defaultConfig.length);
            logger.debug("Config file was set to default");
        } catch (IOException x) {
            logger.debug("Something went wrong with setting default values");
        }
    }

    /**
     * Method read and returns current host value from config.
     * @return host's value or empty string in case of exception.
     */
    public static String getHost(){
        try {
            return getConfig().getString("host");
        } catch (IOException | ConfigurationException e) {
            logger.debug("Something went wrong with config. Host not found", e);
        }
        return "";
    }

    /**
     * Method reads and returns current port value from config.
     * @return port's value or empty string in case of exception.
     */
    public static String getPort() {
        try {
            return getConfig().getString("port");
        } catch (IOException | ConfigurationException e) {
            logger.debug("Something went wrong with config. Port not found", e);
        }
        return "";
    }

    /**
     * Method tries to save new config properties.
     * @param host a String object with new host value
     * @param port a String object with new port value
     */
    public static void saveNewConfig(String host, String port) {
        try {
            CompositeConfiguration config = getConfig();
            config.setProperty("host", host);
            config.setProperty("port", port);
            logger.debug("New config was saved");
        } catch (IOException | ConfigurationException e) {
            logger.debug("New config wasn't saved", e);
        }
    }
}
