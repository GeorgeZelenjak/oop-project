package nl.tudelft.oopp.livechat.controllers.popupcontrollers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import javafx.util.Callback;
import nl.tudelft.oopp.livechat.data.PollAndOptions;
import nl.tudelft.oopp.livechat.data.PollOption;
import nl.tudelft.oopp.livechat.data.PollOptionVote;
import nl.tudelft.oopp.livechat.uielements.PollVoteCell;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PollVotingPopupController implements Initializable {

    @FXML
    private ListView<PollOptionVote> votingListView;

    @FXML
    private Text questionText;

    @FXML
    ObservableList<PollOptionVote> observableList = FXCollections.observableArrayList();

    private List<PollOptionVote> list;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        questionText.setText(PollAndOptions.getCurrent().getPoll().getQuestionText());
        list = new ArrayList<>();
        for (PollOption option: PollAndOptions.getCurrent().getOptions()) {
            list.add(new PollOptionVote(option));
        }
        setListViewAsInEditing();

    }

    /**
     * Sets list view as in editing.
     */
    private void setListViewAsInEditing() {
        observableList.setAll(list);
        votingListView.setItems(observableList);

        votingListView.setCellFactory(
                new Callback<ListView<PollOptionVote>, ListCell<PollOptionVote>>() {
                    @Override
                    public ListCell<PollOptionVote> call(ListView<PollOptionVote> listView) {
                        return new PollVoteCell();
                    }
                });
        votingListView.getItems().clear();
        votingListView.getItems().addAll(list);
    }
}
