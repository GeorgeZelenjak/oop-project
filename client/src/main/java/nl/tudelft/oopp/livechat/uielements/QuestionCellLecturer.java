package nl.tudelft.oopp.livechat.uielements;

import javafx.scene.control.ListCell;
import nl.tudelft.oopp.livechat.data.Question;

/**
 * Customizes the question cell for the lecturer.
 */
public class QuestionCellLecturer extends ListCell<Question> {

    /**
     * Customizes the question cell for the lecturer.
     *
     * @param question the question
     * @param empty set to empty
     */
    @Override
    public void updateItem(Question question, boolean empty) {
        super.updateItem(question, empty);
        if (question != null && !empty) {

            CellDataLecturer data = new CellDataLecturer();
            data.setQuestion(question);
            data.setTimestamp(question.getTime());

            data.setInfo(question.getText());
            data.setOwnerName("Anonymous"); //Will be changed when we implement authorization
            data.markAnswered();
            data.setNumberOfUpvotes(question.getVotes());

            setGraphic(data.getBox());
            data.setAnsweredQuestion();
            data.setDeleteQuestion();
        } else setGraphic(null);
    }
}