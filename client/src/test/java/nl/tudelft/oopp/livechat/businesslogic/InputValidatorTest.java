package nl.tudelft.oopp.livechat.businesslogic;

import org.junit.jupiter.api.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class InputValidatorTest {
    private static String correctText;

    /**
     * Setup for the test class.
     */
    @BeforeAll
    public static void setup() {
        correctText = "This is a correct string!";
    }

    @Test
    public void validateLengthIsZeroTest() {
        assertEquals(-1, InputValidator.validateLength("",25));
    }

    @Test
    public void validateLengthIsTooLongTest() {
        StringBuilder stringBuilder = new StringBuilder("Ha");
        stringBuilder.append(stringBuilder.toString().repeat(26));
        String longText = stringBuilder.toString();
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
        assertEquals(42, InputValidator.validateMinute("42"));
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
        assertEquals(0, InputValidator.validateHour("00"));
    }


    @Test
    public void validateFrequencyNegativeTest() {
        assertEquals(-1, InputValidator.validateFrequency("-3"));
    }

    @Test
    public void validateFrequencyNotIntTest() {
        assertEquals(-1, InputValidator.validateFrequency("two"));
    }

    @Test
    public void validateFrequencySuccessfulTest() {
        assertEquals(0, InputValidator.validateFrequency("0"));
    }


    @Test
    public void checkNameValidTest() {
        assertTrue(InputValidator.checkName("Stalin"));
    }

    @Test
    public void checkNameInvalidTest() {
        assertFalse(InputValidator.checkName("mussolini jr"));
        assertFalse(InputValidator.checkName("MRMUSSOLINI"));
        assertFalse(InputValidator.checkName("UNCLEADOLFHITLER"));
        assertFalse(InputValidator.checkName("hitlerkaput"));

    }

    @Test
    public void checkBadWordsNoBadWords() {
        assertTrue(InputValidator.checkBadWords("this text contains no bad words"));
    }

    @Test
    public void checkBadWordsContainsBadWords() {
        //copied from InputValidator class
        Set<String> badWords = new HashSet<>(List.of("fuck", "nigga", "nigger", "bitch",
                " ass ", "asshole", " arse ", "bastard", "bollocks", "shit", "faggot"));
        String lower = "Do you know what the %s does this text contain?";
        String upper = lower.toUpperCase();
        for (String badWord : badWords) {
            String l = String.format(lower, badWord);
            if (InputValidator.checkBadWords(l)) fail();
            String u = String.format(upper, badWord.toUpperCase());
            if (InputValidator.checkBadWords(u)) fail();
        }
        assertTrue(true);
    }

    @Test
    public void validateUUIDInvalidTest() {
        assertFalse(InputValidator.validateUUID("This is the most valid UUID"));
    }

    @Test
    public void validateUUIDValidTest() {
        assertTrue(InputValidator.validateUUID("9c339e35-3ded-46d8-ad88-893bce4f7577"));
    }
}
