package nl.tudelft.oopp.livechat.businesslogic;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class InputValidatorTest {

    private static String emptyText;
    private static String longText;
    private static String correctText;

    /**
     * Setup for the test class.
     */
    @BeforeAll
    public static void setup() {
        emptyText = "";
        StringBuilder stringBuilder = new StringBuilder("Ha");
        stringBuilder.append(stringBuilder.toString().repeat(26));
        longText = stringBuilder.toString();
        System.out.println(longText.length());
        correctText = "This is a correct string!";
    }

    @Test
    public void validateLengthIsZeroTest() {
        assertEquals(-1, InputValidator.validateLength(emptyText,25));
    }

    @Test
    public void validateLengthIsTooLongTest() {
        assertEquals(-2, InputValidator.validateLength(longText,25));
    }

    @Test
    public void validateLengthIsCorrectLengthTest() {
        assertEquals(0,InputValidator.validateLength(correctText,25));
    }

    @Test
    public void validateMinuteNotIntTest() {
        assertEquals(-1, InputValidator.validateMinute("fortytwo"));
    }

    @Test
    public void validateMinuteNegativeTest() {
        assertEquals(-2, InputValidator.validateMinute("-1"));
    }

    @Test
    public void validateMinuteTooLargeTest() {
        assertEquals(-2, InputValidator.validateMinute("66"));
    }

    @Test
    public void validateMinuteSuccessfulTest() {
        assertEquals(0, InputValidator.validateMinute("42"));
    }

    @Test
    public void validateHourNotIntTest() {
        assertEquals(-1, InputValidator.validateHour("twelve"));
    }

    @Test
    public void validateHourNegativeTest() {
        assertEquals(-2, InputValidator.validateHour("-3"));
    }

    @Test
    public void validateHourTooLargeTest() {
        assertEquals(-2, InputValidator.validateHour("24"));
    }

    @Test
    public void validateHourSuccessfulTest() {
        assertEquals(0, InputValidator.validateHour("0"));
    }


}
