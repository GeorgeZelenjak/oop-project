package nl.tudelft.oopp.livechat.businesslogic;

import nl.tudelft.oopp.livechat.controllers.AlertController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public abstract class InputValidator {
    private static Set<String> badWords = new HashSet<>(List.of("fuck", "nigga", "nigger", "bitch", "ass", "arse",
            "bastard", "bollocks", "shit"));

    private InputValidator() {

    }


    /**
     * Validates length of user String input.
     * @param input      the user input
     * @param maxLength the max length
     * @return int
     *       0 - Everything is correct
     *      -1 - Sting is empty
     *      -2 - String is too long
     */
    public static int validateLength(String input, int maxLength) {
        if (input.length() == 0) {
            return -1;
        } else if (input.length() > maxLength) {
            return -2;
        }
        return 0;
    }

    /**
     * Validates user minute input.
     * @param input the user input
     * @return int
     *       0 - Everything is correct
     *      -1 - Not an integer
     *      -2 - Integer not a valid minute
     */
    public static int validateMinute(String input) {
        int minute;
        try {
            minute = Integer.parseInt(input);
        } catch (Exception e) {
            return -1;
        }
        if (minute >= 0 && minute < 60) {
            return 0;
        }
        return -2;
    }

    /**
     * Validates the frequency of asking questions.
     * @param num String representing frequency
     * @return true if the frequency is a valid non-negative number
     */
    public static int validateFrequency(String num) {
        try {
            int frequency = Integer.parseInt(num);
            return (frequency >= 0) ? frequency : -1;

        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Validates user hour input.
     * @param input the user input
     * @return int
     *       0 - Everything is correct
     *      -1 - Not an integer
     *      -2 - Integer not a valid hour
     */
    public static int validateHour(String input) {
        int hour;
        try {
            hour = Integer.parseInt(input);
        } catch (Exception e) {
            return -1;
        }
        if (hour >= 0 && hour <= 23) {
            return 0;
        }
        return -2;
    }

    /**
     * Checks the text for curse words.
     * @param text the text to be checked
     * @return true if no curse words, false otherwise
     */
    public static boolean checkCurseWords(String text) {
        String t = text.toLowerCase();
        for (String word : badWords) {
            if (t.contains(word)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Validates UUID.
     * @param uuidString a String with uuid
     * @return true if the uuid is valid, false otherwise
     */
    public static boolean validateUUID(String uuidString) {
        try {
            UUID uuid = UUID.fromString(uuidString);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
