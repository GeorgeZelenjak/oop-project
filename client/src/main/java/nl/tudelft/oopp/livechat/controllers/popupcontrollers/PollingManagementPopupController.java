package nl.tudelft.oopp.livechat.controllers.popupcontrollers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.util.Callback;
import nl.tudelft.oopp.livechat.data.*;
import nl.tudelft.oopp.livechat.uielements.PollOptionCell;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * The type Polling management popup controller.
 */
public class PollingManagementPopupController implements Initializable {

    @FXML
    private ListView<PollOption> popupOptionsListView;

    @FXML
    private TextArea questionTextTextArea;

    private List<PollOption> options;

    private Poll poll;

    /**
     * The Observable list.
     */
    @FXML
    ObservableList<PollOption> observableList = FXCollections.observableArrayList();

    /**
     * Fetch poll options.
     */

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        resetListView();
        addPollOptionCell();
        addPollOptionCell();
        addPollOptionCell();
        addPollOptionCell();

    }


    /**
     * Fetch poll options.
     */
    public void initializeList() {

        observableList.setAll(options);
        popupOptionsListView.setItems(observableList);

        popupOptionsListView.setCellFactory(
                new Callback<ListView<PollOption>, ListCell<PollOption>>() {
                    @Override
                    public ListCell<PollOption> call(ListView<PollOption> listView) {
                        return new PollOptionCell();
                    }
                });
        popupOptionsListView.getItems().addAll(options);
    }

    /**
     * Add poll option cell.
     */
    public void addPollOptionCell() {
        popupOptionsListView.getItems().add(new PollOption());
        options = popupOptionsListView.getItems();
    }

    /**
     * Open polling.
     */
    public void openPolling() {
        if (!PollAndOptions.getCurrentPollAndOptions().equals(
                PollAndOptions.getInEditingPollAndOptions())) {
            //PollCommunication.createPoll();
            for (PollOption pollOption : PollAndOptions.getInEditingPollAndOptions().getOptions()) {
            //     PollCommunication.addOption();
            }
            //  PollCommunication.toggle();
        }
    }

    /**
     * Close poll.
     */
    public void closePoll() {
        if (PollAndOptions.getCurrentPollAndOptions().getPoll().isOpen()) {
            PollAndOptions.getCurrentPollAndOptions().getPoll().setOpen(false);
            // PollCommunication.toggle();
        }
    }

    /**
     * Restart poll.
     */
    public void restartPoll() {
        //PollCommunication.resetVotes();
    }

    /**
     * New poll.
     */
    public void newPoll() {
        PollAndOptions.setInEditingPollAndOptions(
                new PollAndOptions(new Poll(), new ArrayList<PollOption>()));
        resetListView();
    }

    private void resetListView() {
        poll = PollAndOptions.getInEditingPollAndOptions().getPoll();
        options = PollAndOptions.getInEditingPollAndOptions().getOptions();
        questionTextTextArea.setText(poll.getQuestionText());
        initializeList();
    }
}
