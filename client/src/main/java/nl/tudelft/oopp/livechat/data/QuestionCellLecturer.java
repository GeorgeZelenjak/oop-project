package nl.tudelft.oopp.livechat.data;

import javafx.scene.control.ListCell;

public class QuestionCellLecturer extends ListCell<Question> {

    @Override
    public void updateItem(Question question, boolean empty) {
        super.updateItem(question,empty);
        if (question != null && !empty) {

            CellDataLecturer data = new CellDataLecturer();
            data.setQuestion(question);
            data.setTimestamp(question.getTime());

            data.setInfo(question.getText());
            data.setOwnerName("Anonymous"); //Will be changed when we implement authorization

            setGraphic(data.getBox());
        } else setGraphic(null);
    }
}