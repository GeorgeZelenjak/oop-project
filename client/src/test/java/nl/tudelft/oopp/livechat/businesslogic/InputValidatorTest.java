package nl.tudelft.oopp.livechat.businesslogic;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class InputValidatorTest {

    private static String emptyText;
    private static String longText;
    private static String correctText;

    @BeforeAll
    private static void setup() {
        emptyText = "";
        StringBuilder stringBuilder = new StringBuilder("Ha");
        for (int i = 0; i < 11; i++) {
            stringBuilder.append(stringBuilder.toString());
        }
        longText = stringBuilder.toString();
        System.out.println(longText.length());
        correctText = "This is a correct string!";
    }

    @Test
    public void validateLengthIsZeroTest() {
        assertEquals(-1,InputValidator.validateLength(emptyText,25));
    }

    @Test
    public void validateLengthIsTooLongTest() {
        assertEquals(-2,InputValidator.validateLength(longText,25));
    }

    @Test
    public void validateLengthIsCorrectLengthTest() {
        assertEquals(0,InputValidator.validateLength(correctText,25));
    }



}
