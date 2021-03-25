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
    private ListView<PollOption> pollOptionsListView;

    @FXML
    private TextArea questionTextTextArea;

    //Setup for ease of access
    private List<PollOption> inEditingOptions;
    private Poll inEditingPoll;


    private List<PollOption> fetchedOptions;
    private Poll fetchedPoll;


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

        //Set for ease of access
        lectureId = Lecture.getCurrentLecture().getUuid();
        modkey = Lecture.getCurrentLecture().getModkey();
        inEditingOptions = PollAndOptions.getInEditingPollAndOptions().getOptions();
        inEditingPoll = PollAndOptions.getInEditingPollAndOptions().getPoll();

        //Sets page to match whats was in editing
        setAsInEditing();
    }


    /**
     * Add poll option cell to inEditingOptions.
     */
    public void addPollOptionCell() {
        pollOptionsListView.getItems().add(new PollOption());
        inEditingOptions = pollOptionsListView.getItems();
        saveEdited();
    }

    /**
     * Open polling.
     */
    public void openPolling() {
        if (!inEditingPoll.equals(fetchedPoll)) {
            publishPoll();
            saveEdited();
            return;
        }

        if (!inEditingPoll.isOpen()) {
            PollCommunication.toggle(inEditingPoll.getId(), modkey);
            inEditingPoll.setOpen(true);
            saveEdited();
        }
    }

    /**
     * Close poll.
     */
    public void closePoll() {
        if (fetchedPoll != null && fetchedPoll.isOpen()) {
            PollCommunication.toggle(fetchedPoll.getId(), modkey);
            fetchedPoll.setOpen(false);
            saveEdited();
        }
    }

    /**
     * Restart poll.
     */
    public void restartPoll() {
        PollAndOptions current = PollAndOptions.getCurrentPollAndOptions();
        PollCommunication.resetVotes(current.getPoll().getId(), modkey);
        saveEdited();
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

        inEditingPoll = new Poll();
        inEditingOptions = new ArrayList<PollOption>();
        setAsInEditing();
        saveEdited();
    }

    /**
     * Resets user input to match what is in inEditing variable.
     */
    private void setAsInEditing() {
        //Sets question text
        questionTextTextArea.setText(inEditingPoll.getQuestionText());

        //Sets options
        setListViewAsInEditing();
    }

    private void setListViewAsInEditing() {

        observableList.setAll(inEditingOptions);
        pollOptionsListView.setItems(observableList);

        //Setup cellFactory
        pollOptionsListView.setCellFactory(
                new Callback<ListView<PollOption>, ListCell<PollOption>>() {
                    @Override
                    public ListCell<PollOption> call(ListView<PollOption> listView) {
                        return new PollOptionCell();
                    }
                });
        pollOptionsListView.getItems().addAll(inEditingOptions);
    }

    private void forceUpdateFetch() {
        fetchedPoll = inEditingPoll;
        fetchedOptions = inEditingOptions;
    }

    private void publishPoll() {

        //Sends request for poll
        Poll sentPoll = PollCommunication.createPoll(
                lectureId, modkey, questionTextTextArea.getText());

        if (sentPoll != null) {
            List<PollOption> sentPollOptions = new ArrayList<PollOption>();

            //Sends requests for every pollOption
            for (PollOption pollOption : inEditingOptions) {
                System.out.println(pollOption.getOptionText());
                sentPollOptions.add(PollCommunication.addOption(sentPoll.getId(),
                        modkey, pollOption.isCorrect(), pollOption.getOptionText()));
            }
            PollCommunication.toggle(sentPoll.getId(), modkey);
            sentPoll.setOpen(true);

            //Set in editing variables to match the server responses
            inEditingOptions = sentPollOptions;
            inEditingPoll = sentPoll;
            forceUpdateFetch();
            saveEdited();
        }
    }

    private void saveEdited() {
        PollAndOptions.getInEditingPollAndOptions().setOptions(inEditingOptions);
        PollAndOptions.getInEditingPollAndOptions().setPoll(inEditingPoll);
    }
}
