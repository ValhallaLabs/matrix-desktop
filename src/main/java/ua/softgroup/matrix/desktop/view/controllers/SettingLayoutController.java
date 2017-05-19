package ua.softgroup.matrix.desktop.view.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.softgroup.matrix.desktop.utils.ConfigManager;

import java.io.IOException;

import static ua.softgroup.matrix.desktop.Main.preferences;

/**
 * @author Andrii Bei <sg.andriy2@gmail.com>
 */
public class SettingLayoutController extends Controller {
    public static final Logger logger = LoggerFactory.getLogger(SettingLayoutController.class);
    private LoginLayoutController loginLayoutController;
    @FXML
    public TextField labelHost;
    @FXML
    public TextField labelPort;
    @FXML
    public ChoiceBox choiceBoxLanguage;
    public static String globalLanguage;

    /**
     * After Load/Parsing fxml call this method
     */
    @FXML
    public void initialize() {
        getPortAndHostFromConfigManager();
        getPreferencesLanguage();
        setLanguage();
    }

    public void getPreferencesLanguage() {
        if (preferences.get("language", "").equals("1")) {
            globalLanguage = "uk";
        } else globalLanguage = "en";
    }

    private void setLanguage() {
        ObservableList<String> language = FXCollections.observableArrayList("English", "Ukraine");
        choiceBoxLanguage.setItems(language);
        int defaultValue;
        if (globalLanguage == "en") {
            defaultValue = 0;
        } else defaultValue = 1;
        choiceBoxLanguage.setValue(language.get(defaultValue));
        choiceBoxLanguage.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> preferences.put("language", newValue.toString()));
        System.out.println(preferences.get("language", ""));
    }

    /**
     * Hears when user click on button and get data from port and host field also hide setting window
     *
     * @param actionEvent callback click on button
     */
    public void saveSettings(ActionEvent actionEvent) {
        try {
            ConfigManager.saveNewConfig(labelHost.getText(), labelPort.getText());
            labelHost.getScene().getWindow().hide();
            loginLayoutController.initializeAuthenticationManager();
        } catch (ConfigManager.ConfigCrashException e) {
            logger.error("Config wasn't set to default", e);
            super.tellUserAboutCrash();
        } catch (IOException | ConfigurationException e) {
            logger.error("New config wasn't saved", e);
        }
    }

    /**
     * Hears when user click on button and hide setting window
     *
     * @param actionEvent callback click on button
     */
    public void cancelSettings(ActionEvent actionEvent) {
        if (!loginLayoutController.getAuthenticationSessionManager().isConnectionOpened()) {
            loginLayoutController.stopProgressIndicator();
        }
        labelHost.getScene().getWindow().hide();
    }

    /**
     * Hears when user click on button and call default setting of host and port
     *
     * @param actionEvent callback click on button
     */
    public void resetToDefaultSettings(ActionEvent actionEvent) {
        try {
            ConfigManager.setConfigToDefault();
            getPortAndHostFromConfigManager();
        } catch (ConfigManager.ConfigCrashException e) {
            logger.error("Config wasn't set to default", e);
            super.tellUserAboutCrash();
        }
    }

    /**
     * Set {@link LoginLayoutController}
     *
     * @param loginLayoutController modality window of setting window get from login window
     */
    void setLoginLayoutController(LoginLayoutController loginLayoutController) {
        this.loginLayoutController = loginLayoutController;
    }

    /**
     * Set in label and port label data from config manager
     */
    private void getPortAndHostFromConfigManager() {
        labelHost.setText(ConfigManager.getHost());
        labelPort.setText(ConfigManager.getPort());
    }
}
