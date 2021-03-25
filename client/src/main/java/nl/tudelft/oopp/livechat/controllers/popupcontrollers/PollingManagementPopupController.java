package nl.tudelft.oopp.livechat.controllers.popupcontrollers;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import nl.tudelft.oopp.livechat.controllers.AlertController;
import nl.tudelft.oopp.livechat.controllers.NavigationController;
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

    public static List<PollOption> getInEditingOptions() {
        return inEditingOptions;
    }

    public static void setInEditingOptions(List<PollOption> inEditingOptions) {
        PollingManagementPopupController.inEditingOptions = inEditingOptions;
    }

    public static Poll getInEditingPoll() {
        return inEditingPoll;
    }

    public static void setInEditingPoll(Poll inEditingPoll) {
        PollingManagementPopupController.inEditingPoll = inEditingPoll;
    }

    //Setup for ease of access
    private static List<PollOption> inEditingOptions;
    private static Poll inEditingPoll;


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

        questionTextTextArea.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0,
                                Boolean oldPropertyValue, Boolean newPropertyValue) {
                inEditingPoll.setQuestionText(questionTextTextArea.getText());
            }
        });

        //Set for ease of access
        lectureId = Lecture.getCurrentLecture().getUuid();
        modkey = Lecture.getCurrentLecture().getModkey();

        //Sets page to match whats was in editing
        setAsInEditing();
    }


    /**
     * Add poll option cell to inEditingOptions.
     */
    public void addPollOptionCell() {
        pollOptionsListView.getItems().add(new PollOption());
        inEditingOptions = pollOptionsListView.getItems();
    }

    /**
     * Open polling.
     */
    public void openPolling() {
        if (!inEditingPoll.equals(fetchedPoll)) {
            publishPoll();
            return;
        }

        if (!inEditingPoll.isOpen()) {
            PollCommunication.toggle(inEditingPoll.getId(), modkey);
            inEditingPoll.setOpen(true);
        }
    }

    /**
     * Close poll.
     */
    public void closePoll() {
        if (fetchedPoll != null && fetchedPoll.isOpen()) {
            PollCommunication.toggle(fetchedPoll.getId(), modkey);
            fetchedPoll.setOpen(false);
        }
    }

    /**
     * Restart poll.
     */
    public void restartPoll() {
        PollCommunication.resetVotes(inEditingPoll.getId(), modkey);
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
        pollOptionsListView.getItems().clear();
        pollOptionsListView.getItems().addAll(inEditingOptions);
    }

    private void forceUpdateFetch() {
        fetchedPoll = inEditingPoll;
        fetchedOptions = inEditingOptions;
    }

    private void publishPoll() {

        if (!checkIfNotEmpty()) {
            AlertController.alertWarning("Emty fields", "You have empty fields");
            return;
        }
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
        }
    }

    private boolean checkIfNotEmpty() {
        if (inEditingPoll.getQuestionText() == null || inEditingPoll.getQuestionText().equals("")) {
            return false;
        }
        for (PollOption pollOption : inEditingOptions) {
            if (pollOption.getOptionText() == null || pollOption.getOptionText().equals("")) {
                return false;
            }
        }
        return true;
    }

}
