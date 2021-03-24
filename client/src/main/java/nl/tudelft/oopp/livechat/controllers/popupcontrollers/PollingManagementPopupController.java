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
import nl.tudelft.oopp.livechat.servercommunication.PollCommunication;
import nl.tudelft.oopp.livechat.uielements.PollOptionCell;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

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

    private UUID lectureId;

    private UUID modkey;

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
        lectureId = Lecture.getCurrentLecture().getUuid();
        modkey = Lecture.getCurrentLecture().getModkey();

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
        PollAndOptions current = PollAndOptions.getCurrentPollAndOptions();
        if (current == null || !poll.equals(current.getPoll())) {

            PollAndOptions.setCurrentPollAndOptions(
                    new PollAndOptions(new Poll(), new ArrayList<PollOption>()));

            poll = PollCommunication.createPoll(lectureId, modkey, questionTextTextArea.getText());
            PollAndOptions.getCurrentPollAndOptions().setPoll(poll);

            for (PollOption pollOption : options) {
                System.out.println(pollOption.getOptionText());
                PollAndOptions.getCurrentPollAndOptions().getOptions().add(
                    PollCommunication.addOption(
                    poll.getId(), modkey, pollOption.isCorrect(), pollOption.getOptionText()));
            }
            PollCommunication.toggle(poll.getId(), modkey);
            PollAndOptions.getCurrentPollAndOptions().getPoll().setOpen(true);

        } else if (!current.getPoll().isOpen()) {
            PollCommunication.toggle(current.getPoll().getId(), modkey);
            current.getPoll().setOpen(true);
        }
    }

    /**
     * Close poll.
     */
    public void closePoll() {
        PollAndOptions current = PollAndOptions.getCurrentPollAndOptions();
        if (current != null && current.getPoll().isOpen()) {
            PollCommunication.toggle(poll.getId(), modkey);
            current.getPoll().setOpen(false);
        }
    }

    /**
     * Restart poll.
     */
    public void restartPoll() {
        PollAndOptions current = PollAndOptions.getCurrentPollAndOptions();
        PollCommunication.resetVotes(current.getPoll().getId(), modkey);
    }

    /**
     * New poll.
     */
    public void newPoll() {
        try {
            closePoll();
        } catch (Exception e) {
            System.out.println("Current poll is null");
        }

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
