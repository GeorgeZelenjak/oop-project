package nl.tudelft.oopp.livechat.data;

import javafx.scene.control.ListCell;

public class QuestionCell extends ListCell<Question> {

    @Override
    public void updateItem(Question question, boolean empty) {
        super.updateItem(question,empty);
        if (question != null) {
            CellData data = new CellData();
            data.setInfo(question.getText());
            setGraphic(data.getBox());
        }
    }
}