package ua.softgroup.matrix.desktop.controllerjavafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.softgroup.matrix.desktop.utils.ConfigManager;

/**
 * @author Andrii Bei <sg.andriy2@gmail.com>
 */
public class SettingLayoutController {
    public static final Logger logger = LoggerFactory.getLogger(SettingLayoutController.class);
    private LoginLayoutController loginLayoutController;

    @FXML
    public TextField labelHost;
    @FXML
    public TextField labelPort;

    @FXML
    public void initialize() {
        getPortAndHostFromConfigManager();
    }

    private void getPortAndHostFromConfigManager() {
        labelHost.setText(ConfigManager.getHost());
        labelPort.setText(ConfigManager.getPort());
    }

    public void saveSettings(ActionEvent actionEvent) {
        ConfigManager.saveNewConfig(labelHost.getText(), labelPort.getText());
        labelHost.getScene().getWindow().hide();
        loginLayoutController.initializeAuthenticationManager();
    }

    public void cancelSettings(ActionEvent actionEvent) {
        loginLayoutController.initializeAuthenticationManager();
        labelHost.getScene().getWindow().hide();
    }

    public void resetToDefaultSettings(ActionEvent actionEvent) {
        ConfigManager.setConfigToDefault();
        getPortAndHostFromConfigManager();
    }

     void setLoginLayoutController(LoginLayoutController loginLayoutController) {
        this.loginLayoutController = loginLayoutController;
    }
}
