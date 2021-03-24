package nl.tudelft.oopp.livechat.controllers.popupcontrollers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import javafx.util.Duration;
import nl.tudelft.oopp.livechat.businesslogic.QuestionManager;
import nl.tudelft.oopp.livechat.data.*;
import nl.tudelft.oopp.livechat.servercommunication.QuestionCommunication;
import nl.tudelft.oopp.livechat.uielements.PollOptionCell;
import nl.tudelft.oopp.livechat.uielements.QuestionCellLecturer;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PollingManagementPopupController {

    @FXML
    private ListView<PollOption> popupOptionsListView;

    private List<PollOption> options;

    @FXML
    ObservableList<PollOption> observableList = FXCollections.observableArrayList();

    /**
     * Fetch poll options.
     */
    public void fetchPollOptions() {

        Poll poll = new Poll();
        List<PollOption> list = new ArrayList<>();
        PollOption option1 = new PollOption();

        if (list == null) {
            return;
        }
        if (list.size() == 0) {
            System.out.println("There are no poll options");
        }
        PollOption.setCurrentPollOptions(list);

        options = PollOption.getCurrentPollOptions();

        observableList.setAll(options);
        popupOptionsListView.setItems(observableList);

        popupOptionsListView.setCellFactory(
                new Callback<ListView<PollOption>, ListCell<PollOption>>() {
                    @Override
                    public ListCell<PollOption> call(ListView<PollOption> listView) {
                        return new PollOptionCell();
                    }
                });
        popupOptionsListView.getItems().clear();
        popupOptionsListView.getItems().addAll(options);
    }
}
