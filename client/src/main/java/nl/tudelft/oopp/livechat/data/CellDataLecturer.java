package nl.tudelft.oopp.livechat.data;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.control.Button;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import nl.tudelft.oopp.livechat.servercommunication.QuestionCommunication;

public class CellDataLecturer {

    @FXML
    private Text questionText;

    @FXML
    private Button isAnsweredButton;

    @FXML
    private AnchorPane questionBoxAnchorPane;

    @FXML
    private Text questionOwner;

    @FXML
    private Text numberOfUpvotes;

    @FXML
    private Text dateStamp;

    private Question question;


    /**
     * Instantiates a new Cell data.
     */
    public CellDataLecturer() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/fxml/questionCellLecturer.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setInfo(String string) {
        questionText.setText(string);
    }

    public void setOwnerName(String owner) {
        questionOwner.setText(owner);
    }

    public AnchorPane getBox() {
        return questionBoxAnchorPane;
    }

    public void setQuestion(Question question) {

        this.question = question;
    }

    public void setTimestamp(Timestamp timestamp) {
        dateStamp.setText(timestamp.toLocalDateTime().toString());
        dateStamp.setText(timestamp.toLocalDateTime()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    /**
     * Sets question as answered.
     */
    public void setAnsweredQuestion() {
        isAnsweredButton.setOnAction((
                ActionEvent event) -> {
            QuestionCommunication
                    .markedAsAnswered(question.getId(), Lecture.getCurrentLecture().getModkey());

            System.out.println(question.getVotes());
        });
    }
}
