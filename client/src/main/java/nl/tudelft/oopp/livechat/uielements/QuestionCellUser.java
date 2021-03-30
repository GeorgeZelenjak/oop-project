package nl.tudelft.oopp.livechat.uielements;

import javafx.scene.control.ListCell;
import nl.tudelft.oopp.livechat.controllers.cellcontrollers.CellUserController;
import nl.tudelft.oopp.livechat.data.Question;

/**
 * Class that customizes the question cell for the user.
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
            CellUserController data = new CellUserController();
            data.setQuestion(question);

            //set information
            data.setTimestamp(question.getTime());
            if (question.isEdited()) {
                data.setOwnerName(question.getOwnerName() + " (edited)");
            } else {
                data.setOwnerName(question.getOwnerName());
            }
            data.markAnswered();
            data.setContent(question.getText());
            data.setNumberOfUpvotes(question.getVotes());

            //set graphic and buttons
            setGraphic(data.getBox());
            data.setUpvoteButton();
            data.setDeleteButton();
            data.setAnswerText();
            data.colorUpvoteButton();
        } else {
            setGraphic(null);
        }
    }
}