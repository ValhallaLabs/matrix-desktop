package ua.softgroup.matrix.desktop.controllerjavafx;

import javafx.collections.FXCollections;

import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import ua.softgroup.matrix.server.desktop.model.datamodels.InstructionsModel;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Andrii Bei <sg.andriy2@gmail.com>
 */
public class InstructionsLayoutController {

    @FXML
    public Label labelInstructions;
    @FXML
    public ListView<InstructionsModel> lvInstructions;
    private List<InstructionsModel> listInstructionsModel = new ArrayList<>();

    /**
     * After Load/Parsing fxml call this method
     */
    @FXML
    public void initialize() {
        initArray();
    }

    /**
     * Hears when user click on listView select item ,get data from ObservableList and set in label
     *
     * @param event callback click on list item
     */
    public void chooseCurentInstructuon(Event event) {
        InstructionsModel selectProject = lvInstructions.getSelectionModel().getSelectedItem();
        labelInstructions.setText(selectProject.getContent());
    }

    /**
     * Init {@link InstructionsModel} data into arrayList then set this data in Observable list,
     * and display in ListView
     */
    private void initArray() {
        listInstructionsModel.add(new InstructionsModel("first", "\n" +
                "Все работы и проекты не обсуждаются с людьми, не имеющими к ним отношения, т.е НЕ сотрудниками\n" +
                "\n" +
                "В частности с друзьями и знакомыми.\n" +
                "Это обычная политика конфиденциальности почти в любой организации\n" +
                "\n" +
                "Утечки информации ни к чему, это может навредить. И тем более в контексте разного рода проверок.\n" +
                "\n" +
                "Считайте что работаете на военном обьекте, где всё засекречено :)\n" +
                "\n" +
                "Рассчитываю на ваше понимание и поддержку, сенкс."));
        listInstructionsModel.add(new InstructionsModel("second", "\n" +
                "1. Двери всегда закрыты, у всех есть свой ключь\n" +
                "\n" +
                "2. Стучать теоретически может только :\n" +
                "a) Василичь\n" +
                "b) Тарас\n" +
                "c) и кто то из сотрудников из другого кабинета\n" +
                "однако со след недели все должны знать, что попасть в кабинет по стуку нельзя , можно только позвонив или написав предварительно в icq.\n" +
                "\n" +
                "3.\n" +
                "Если кто то долго стучит , можно позвонить любому сотруднику из другого кабинета ,\n" +
                "или же Тарасу 0979042504  или в Аркаду 0362 262961\n" +
                "или Васильевичу 0964453303\n" +
                "что б подошли посмотрели , кто стучит там.\n" +
                "в крайнем случае мой моб 0503392452 перезвонить мне\n" +
                "\n" +
                "4. итого : Открывать по стуку нельзя. Вообще. Ходят проверки.\n" +
                "\n" +
                "П.С. Открывать можно только по кодовому стуку."));
        listInstructionsModel.add(new InstructionsModel("three", "\n" +
                "Все работы и проекты (или часть работ или проектов )не обсуждаются с сотрудниками , кроме обсуждения( при надобности) с теми ,  которые явно указаны в задаче в программе по данному отрезку работ\n" +
                "\n" +
                "Когда каждый сотрудник знает обо всех проектах - это может навредить всему колллективу.\n" +
                "\n" +
                "при проверке, при желании , один сотрудник сможет рассказать обо всех проектах , как проверкам разного рода , так и потенциальным конкурентам. Тем более, в случае прекращения сотрудничества.\n" +
                "\n" +
                "Итого :\n" +
                "\n" +
                "Это четкая инструкция. Инфа по проектам не обсуждается в кругу сотрудников.\n" +
                "\n" +
                "Нарушение этого пункта - это тот редкий случай, который может повлечь вынужденное прекращение дальнейшего сотрудничества - т.е увольнение\n" +
                "\n" +
                "Данные шаги сделаны в целях безопастности.\n" +
                "\n" +
                "Рассчитываю на ваше понимание и поддержку, сенкс."));
        ObservableList<InstructionsModel> content = FXCollections.observableArrayList(listInstructionsModel);
        lvInstructions.setItems(content);
    }

}
