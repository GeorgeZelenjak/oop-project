package nl.tudelft.oopp.livechat.controllers.popupcontrollers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import nl.tudelft.oopp.livechat.data.*;
import nl.tudelft.oopp.livechat.uielements.PollOptionCell;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PollingManagementPopupController implements Initializable {

    @FXML
    private ListView<PollOption> popupOptionsListView;

    private List<PollOption> options;

    @FXML
    ObservableList<PollOption> observableList = FXCollections.observableArrayList();

    /**
     * Fetch poll options.
     */

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fetchPollOptions();
    }


    /**
     * Fetch poll options.
     */
    public void fetchPollOptions() {

        List<PollOption> list = new ArrayList<>();
        PollOption option1 = new PollOption();
        PollOption option2 = new PollOption();
        list.add(option1);
        list.add(option2);

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
