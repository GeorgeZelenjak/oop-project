package nl.tudelft.oopp.livechat.controllers.popupcontrollers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.util.Callback;
import nl.tudelft.oopp.livechat.controllers.gui.AlertController;
import nl.tudelft.oopp.livechat.data.*;
import nl.tudelft.oopp.livechat.servercommunication.PollCommunication;
import nl.tudelft.oopp.livechat.uielements.PollOptionCell;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;


public class PollingManagementPopupController implements Initializable {

    @FXML
    private ListView<PollOption> pollOptionsListView;

    @FXML
    private TextArea questionTextTextArea;

    @FXML
    private Text isOptionCorrectText;

    @FXML
    private Button newPoll;

    @FXML
    private Button resetVotes;

    @FXML
    private Button endVoting;

    @FXML
    private CheckBox isPollCheckbox;

    private static List<PollOption> inEditingOptions;

    private static Poll inEditingPoll;


    private static boolean allCorrect;

    private List<PollOption> fetchedOptions;
    private Poll fetchedPoll;


    private UUID lectureId;

    private UUID modkey;

    @FXML
    ObservableList<PollOption> observableList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        questionTextTextArea.focusedProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0,
                                Boolean oldPropertyValue, Boolean newPropertyValue) {
                inEditingPoll.setQuestionText(questionTextTextArea.getText());
            }
        });

        lectureId = Lecture.getCurrent().getUuid();
        modkey = Lecture.getCurrent().getModkey();
        setAsInEditing();
    }


    /**
     * Adds a poll option cell to inEditingOptions.
     */
    public void addPollOptionCell() {
        if (!newPoll.isVisible())newPoll.setVisible(true);
        if (!isPollCheckbox.isSelected() && !isOptionCorrectText.isVisible())
            isOptionCorrectText.setVisible(true);
        pollOptionsListView.getItems().add(new PollOption());
        inEditingOptions = pollOptionsListView.getItems();
    }

    /**
     * Opens a poll.
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
        resetVotes.setVisible(true);
        endVoting.setVisible(true);
    }

    /**
     * Closes the poll.
     */
    public void closePoll() {
        if (fetchedPoll != null && fetchedPoll.isOpen()) {
            PollCommunication.toggle(fetchedPoll.getId(), modkey);
            fetchedPoll.setOpen(false);
        }
        inEditingPoll.setOpen(false);
        resetVotes.setVisible(false);
        endVoting.setVisible(false);
    }

    /**
     * Restarts the poll.
     */
    public void restartPoll() {
        PollCommunication.resetVotes(inEditingPoll.getId(), modkey);
    }

    /**
     * Gets inEditingOptions.
     * @return the list inEditingOptions
     */
    public static List<PollOption> getInEditingOptions() {
        return inEditingOptions;
    }

    /**
     * Sets inEditingOptions list.
     * @param inEditingOptions inEditingOptions list
     */
    public static void setInEditingOptions(List<PollOption> inEditingOptions) {
        PollingManagementPopupController.inEditingOptions = inEditingOptions;
    }

    /**
     * Gets inEditingPoll.
     * @return inEditingPoll
     */
    public static Poll getInEditingPoll() {
        return inEditingPoll;
    }

    /**
     * Sets inEditingPoll.
     * @param inEditingPoll inEditingPoll
     */
    public static void setInEditingPoll(Poll inEditingPoll) {
        PollingManagementPopupController.inEditingPoll = inEditingPoll;
    }

    /**
     * Checks if all options are correct.
     * @return true if all options are correct
     */
    public static boolean isAllCorrect() {
        return allCorrect;
    }

    /**
     * Sets all options as correct.
     * @param allCorrect true if all options as correct
     */
    public static void setAllCorrect(boolean allCorrect) {
        PollingManagementPopupController.allCorrect = allCorrect;
    }

    /**
     * Checks if all options are correct.
     * @return true if all options are correct
     */
    public static boolean getAllCorrect() {
        return allCorrect;
    }

    /**
     * Creates a new poll.
     */
    public void newPoll() {
        try {
            closePoll();
        } catch (Exception e) {
            return;
        }

        inEditingPoll = new Poll();
        inEditingOptions = new ArrayList<>();
        newPoll.setVisible(false);
        isPollCheckbox.setSelected(false);
        isOptionCorrectText.setVisible(false);

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

        if (inEditingOptions != null && inEditingOptions.size() != 0) {
            newPoll.setVisible(true);
        } else newPoll.setVisible(false);

        if (isPollCheckbox.isSelected()) isOptionCorrectText.setVisible(false);
        if (!isPollCheckbox.isSelected()
                && inEditingOptions != null && inEditingOptions.size() != 0) {
            isOptionCorrectText.setVisible(true);
        } else isOptionCorrectText.setVisible(false);

        if (inEditingPoll.isOpen()) {
            resetVotes.setVisible(true);
            endVoting.setVisible(true);
        } else {
            resetVotes.setVisible(false);
            endVoting.setVisible(false);
        }
    }

    /**
     * Sets the list view as in editing.
     */
    private void setListViewAsInEditing() {
        observableList.setAll(inEditingOptions);
        pollOptionsListView.setItems(observableList);

        pollOptionsListView.setCellFactory(
                new Callback<>() {
                    @Override
                    public ListCell<PollOption> call(ListView<PollOption> listView) {
                        return new PollOptionCell();
                    }
                });
        pollOptionsListView.getItems().clear();
        pollOptionsListView.getItems().addAll(inEditingOptions);
    }

    /**
     * Forces update fetch.
     */
    private void forceUpdateFetch() {
        fetchedPoll = inEditingPoll;
        fetchedOptions = inEditingOptions;
    }

    /**
     * Publishes poll results.
     */
    private void publishPoll() {
        if (!checkIfNotEmpty()) {
            AlertController.alertWarning("Empty fields", "You have empty fields");
            return;
        }
        //Sends request for poll
        Poll sentPoll = PollCommunication.createPoll(
                lectureId, modkey, questionTextTextArea.getText());
        AlertController.alertConfirmation("Poll creation", "Poll was published");
        if (sentPoll != null) {
            List<PollOption> sentPollOptions = new ArrayList<>();

            //Sends requests for every pollOption
            for (PollOption pollOption : inEditingOptions) {
                sentPollOptions.add(PollCommunication.addOption(sentPoll.getId(),
                        modkey, pollOption.isCorrect(), pollOption.getOptionText()));
            }
            PollCommunication.toggle(sentPoll.getId(), modkey);
            sentPoll.setOpen(true);

            //Set in editing variables to match the server responses
            inEditingOptions = sentPollOptions;
            inEditingPoll = sentPoll;
            forceUpdateFetch();
            resetVotes.setVisible(true);
            endVoting.setVisible(true);
        }
    }

    /**
     * Checks if the text is not empty.
     * @return true if the text is not empty
     */
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

    /**
     * Toggles the poll.
     */
    public void togglePoll() {
        if (isPollCheckbox.isSelected()) isOptionCorrectText.setVisible(false);
        if (!isPollCheckbox.isSelected()
                && inEditingOptions != null && inEditingOptions.size() != 0) {
            isOptionCorrectText.setVisible(true);
        }
        allCorrect = !allCorrect;
        if (allCorrect) {
            assert inEditingOptions != null;
            for (PollOption pollOption: inEditingOptions) {
                pollOption.setCorrect(true);
            }
        }
    }
}
