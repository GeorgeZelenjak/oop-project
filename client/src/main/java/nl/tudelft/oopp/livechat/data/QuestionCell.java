package nl.tudelft.oopp.livechat.data;

import javafx.scene.control.ListCell;

public class QuestionCell extends ListCell<Question> {

    @Override
    public void updateItem(Question question, boolean empty) {
        super.updateItem(question,empty);
        if (question != null && !empty) {

            CellData data = new CellData();
            data.setQuestion(question);

            data.setInfo(question.getText());
            data.setOwnerName("Anonymous"); //Will be changed when we implement authorization

            setGraphic(data.getBox());
            data.setUpvoteButton();
        }
        else
            setGraphic(null);
    }
}