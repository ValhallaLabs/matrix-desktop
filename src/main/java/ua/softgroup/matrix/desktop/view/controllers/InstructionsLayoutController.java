package ua.softgroup.matrix.desktop.view.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import ua.softgroup.matrix.api.model.datamodels.InstructionsModel;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Andrii Bei <sg.andriy2@gmail.com>
 */
public class InstructionsLayoutController {
    private List<InstructionsModel> listInstructionsModel = new ArrayList<>();
    private ObservableList<InstructionsModel> content = FXCollections.observableArrayList();
    @FXML
    public Label labelInstructions;
    @FXML
    public ListView<InstructionsModel> lvInstructions;

    /**
     * After Load/Parsing fxml call this method
     */
    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            initArray();
            setFocusOnListView();
        });
    }

    /**
     * Hears when user click on listView select item ,get data from ObservableList and set in label
     *
     * @param event callback click on list item
     */
    public void clickOnCurrentInstruction(Event event) {
        chooseCurrentInstruction();
    }

    void getUpStage(Scene scene) {
        scene.setOnKeyPressed(event -> {
                    if (event.getCode() == KeyCode.ESCAPE) {
                        scene.getWindow().hide();
                    }
                }
        );
    }

    private void setFocusOnListView() {
        lvInstructions.requestFocus();
        lvInstructions.getSelectionModel().select(0);
        lvInstructions.getFocusModel().focus(0);
        chooseCurrentInstruction();
    }

    private void chooseCurrentInstruction() {
        if (lvInstructions.getSelectionModel().getSelectedItem() != null) {
            InstructionsModel selectProject = lvInstructions.getSelectionModel().getSelectedItem();
            labelInstructions.setText(selectProject.getContent());
        }
    }

    /**
     * Init {@link InstructionsModel} data into arrayList then set this data in Observable list,
     * and display in ListView
     */
    private void initArray() {
        listInstructionsModel.add(new InstructionsModel("Загальна інформація", "Використовуючи дану " +
                "програму, ви даєте згоду ТОВ \"Софт групп\" на використання інформації отриманою з вашого комп’ютера " +
                "шляхом моніторінгу.\n" +
                "А саме: \n" +
                "1) Скріншоти робочого вікна кожні декілька хвилин.\n" +
                "2) Логування клавіатури. \n" +
                "3) Запис заголовків активних вікон."));
        listInstructionsModel.add(new InstructionsModel("У випадку помилок робити", "Якщо під час " +
                "використання програми виникли непередбачені помилки(програма не відповідає, зависає тощо), відправте " +
                "повідомлення з описом ситуації та прикріпленим файлом логів за поточний день, який можна знайти e папці logs каталогу програми, на softgrouptempmail@gmail.com . "));
        for (InstructionsModel model : listInstructionsModel) {
            content.addAll(model);
        }
        lvInstructions.setItems(content);
    }

}
