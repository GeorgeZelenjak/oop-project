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
import nl.tudelft.oopp.livechat.data.PollOptionResult;
import nl.tudelft.oopp.livechat.uielements.PollResultCell;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PollResultPopupController implements Initializable {

    @FXML
    private ListView<PollOptionResult> resultsListView;

    @FXML
    private Text questionText;

    @FXML
    ObservableList<PollOptionResult> observableList = FXCollections.observableArrayList();

    private List<PollOptionResult> list;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        questionText.setText(PollAndOptions.getCurrent().getPoll().getQuestionText());
        list = new ArrayList<>();
        for (PollOption option: PollAndOptions.getCurrent().getOptions()) {
            list.add(new PollOptionResult(option));
        }
        setListViewAsInEditing();

    }

    /**
     * Sets list view as in editing.
     */
    private void setListViewAsInEditing() {
        observableList.setAll(list);
        resultsListView.setItems(observableList);

        resultsListView.setCellFactory(
                new Callback<ListView<PollOptionResult>, ListCell<PollOptionResult>>() {
                    @Override
                    public ListCell<PollOptionResult> call(ListView<PollOptionResult> listView) {
                        return new PollResultCell();
                    }
                });
        resultsListView.getItems().clear();
        resultsListView.getItems().addAll(list);
    }
}
