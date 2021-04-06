package nl.tudelft.oopp.livechat.uielements;

import javafx.scene.control.ListCell;
import nl.tudelft.oopp.livechat.controllers.cellcontrollers.PollVoteCellController;
import nl.tudelft.oopp.livechat.data.PollOptionVote;

public class PollVoteCell extends ListCell<PollOptionVote> {

    @Override
    public void updateItem(PollOptionVote pollOptionVote, boolean empty) {
        super.updateItem(pollOptionVote, empty);

        if (pollOptionVote != null && !empty) {
            PollVoteCellController data = new PollVoteCellController();
            data.setOption(pollOptionVote);
            data.setText();
            data.setVoteButton();
            setGraphic(data.getBox());

        } else setGraphic(null);
    }
}
