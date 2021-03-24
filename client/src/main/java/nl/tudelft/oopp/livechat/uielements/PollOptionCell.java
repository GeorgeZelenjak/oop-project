package nl.tudelft.oopp.livechat.uielements;

import javafx.scene.control.ListCell;
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
            CellDataPollOption data = new CellDataPollOption();
            data.setPollOption(option);

            data.setOptionText(option.getOptionText());
            data.setPollOption(option);
            data.setPollOptionCellIsCorrectCheckBox();
            setGraphic(data.getBox());

        } else setGraphic(null);
    }
}
