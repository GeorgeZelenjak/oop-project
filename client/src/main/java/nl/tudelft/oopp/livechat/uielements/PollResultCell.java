package nl.tudelft.oopp.livechat.uielements;

import javafx.scene.control.ListCell;
import nl.tudelft.oopp.livechat.controllers.cellcontrollers.CellLecturerController;
import nl.tudelft.oopp.livechat.controllers.cellcontrollers.PollResultCellController;
import nl.tudelft.oopp.livechat.data.PollOptionResult;
import nl.tudelft.oopp.livechat.data.Question;

public class PollResultCell extends ListCell<PollOptionResult> {

    @Override
    public void updateItem(PollOptionResult pollOptionResult, boolean empty) {
        super.updateItem(pollOptionResult, empty);

        if (pollOptionResult != null && !empty) {
            PollResultCellController data = new PollResultCellController();
            data.setOption(pollOptionResult);
            data.setText();
            setGraphic(data.getBox());

        } else setGraphic(null);
    }
}
