package nl.tudelft.oopp.livechat.uielements;

import javafx.scene.control.ListCell;
import nl.tudelft.oopp.livechat.data.Question;

/**
 * Customizes the question cell for the user.
 */
public class QuestionCellUser extends ListCell<Question> {

    /**
     * Customizes the question cell for the user.
     * @param question the question
     * @param empty set to empty
     */
    @Override
    public void updateItem(Question question, boolean empty) {
        super.updateItem(question,empty);
        if (question != null && !empty) {

            CellDataUser data = new CellDataUser();
            data.setQuestion(question);
            data.setTimestamp(question.getTime());

            data.setInfo(question.getText());
            data.setOwnerName(question.getOwnerName());
            data.markAnswered();
            data.setNumberOfUpvotes(question.getVotes());

            setGraphic(data.getBox());
            data.setUpvoteButton();
            data.setDeleteButton();
        } else {
            setGraphic(null);
        }
    }
}