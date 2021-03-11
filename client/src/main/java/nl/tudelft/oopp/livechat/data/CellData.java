package nl.tudelft.oopp.livechat.data;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.io.IOException;

public class CellData {

    @FXML
    private Text questionText;

    @FXML
    private HBox hBox;

    /**
     * Instantiates a new Cell data.
     */
    public CellData() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/fxml/questionCell.fxml"));
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

    public HBox getBox() {
        return hBox;
    }
}
