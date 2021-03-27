package nl.tudelft.oopp.livechat.uielements;

import javafx.scene.control.ListCell;
import nl.tudelft.oopp.livechat.controllers.cellcontrollers.CellPollOptionController;
import nl.tudelft.oopp.livechat.data.PollOption;

public class PollOptionCell extends ListCell<PollOption> {

    /**
     * Customizes the question cell for the lecturer.
     * @param option the option
     * @param empty set to empty
     */
    @Override
    public void updateItem(PollOption option, boolean empty) {
        super.updateItem(option, empty);

        if (option != null && !empty) {
            CellPollOptionController data = new CellPollOptionController();
            data.setPollOption(option);
            data.changePollOptionCellIsCorrectCheckBox();

            data.setOptionText(option.getOptionText());
            data.setPollOption(option);
            data.setPollOptionCellIsCorrectCheckBox();
            data.setPollType();
            setGraphic(data.getBox());
            data.setListener();
            data.setDeleteButton();

        } else setGraphic(null);
    }
}
