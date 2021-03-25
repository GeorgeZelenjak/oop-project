package nl.tudelft.oopp.livechat.uielements;

import javafx.scene.control.ListCell;
import nl.tudelft.oopp.livechat.controllers.cellcontrollers.CellLecturerController;
import nl.tudelft.oopp.livechat.data.Question;

/**
 * Class that customizes the question cell for the lecturer.
 */
public class QuestionCellLecturer extends ListCell<Question> {

    /**
     * Customizes the question cell for the lecturer.
     * @param question the question
     * @param empty set to empty
     */
    @Override
    public void updateItem(Question question, boolean empty) {
        super.updateItem(question, empty);

        if (question != null && !empty) {
            CellLecturerController data = new CellLecturerController();
            data.setQuestion(question);

            //set information
            data.setTimestamp(question.getTime());
            if (question.isEdited()) {
                data.setOwnerName(question.getOwnerName() + " (edited)");
            } else {
                data.setOwnerName(question.getOwnerName());
            }
            data.setContent(question.getText());
            data.markAnswered();
            data.setNumberOfUpvotes(question.getVotes());

            //set graphic and buttons
            setGraphic(data.getBox());
            data.setEditButton();
            data.setAnsweredQuestion();
            data.setDeleteQuestion();
            data.replyAnswer();
            data.setBanUser();
            data.disableMarkedAsAnswered();

            //set answered text if the question has been answered
            if (question.getAnswerText() != null && !question.getAnswerText().equals(" ")) {
                data.setAnswerText(question.getAnswerText());
            }

        } else setGraphic(null);
    }
}