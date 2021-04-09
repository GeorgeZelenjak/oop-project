package nl.tudelft.oopp.livechat.businesslogic;

import java.util.*;

public abstract class InputValidator {
    /**
     * Although it could be argued that it is unacceptable to write curse and/or offensive words
     *   in the code, since this application is developed for the university, we think it is
     *   important to ensure that nobody could be offended or discriminated (we also take into
     *   account that there might be very sensitive students, maybe even suffering from depression).
     *   Even though there is going to be moderation during the lectures, and students are assumed
     *   to be clever enough, and even if a text containing offensive language is deleted,
     *   the target can still see it, which in the aforementioned case can even lead to a suicide.
     * This list is pretty small, but we think that even the list like that can potentially solve
     *   some problems.
     */
    private static final Set<String> badWords = new HashSet<>(List.of("fuck", "nigga", "nigger",
            "bitch", " ass ", "asshole", " arse ", "bastard", "bollocks", "shit", "faggot"));

    private InputValidator() {

    }


    /**
     * Validates the length of the input string.
     * @param input the input String
     * @param maxLength the maximum allowed length
     * @return 0 if the length is valid, -1 if the string is empty, -2 if the string is too long
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
     * Validates the input for a minute.
     * @param input the input for a minute
     * @return the number of minutes if the number is valid, -1 if not a number,
     *         -2 if not a valid minute number
     */
    public static int validateMinute(String input) {
        int minute;
        try {
            minute = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
        if (minute >= 0 && minute < 60) {
            return minute;
        }
        return -2;
    }

    /**
     * Validates the input for an hour.
     * @param input the input for an hour
     * @return the number of hours if it is valid, -1 if not a number, -2 if not a valid hour number
     */
    public static int validateHour(String input) {
        int hour;
        try {
            hour = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
        if (hour >= 0 && hour <= 23) {
            return hour;
        }
        return -2;
    }

    /**
     * Validates the frequency of asking questions.
     * @param num string representing frequency
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
     * Checks if the name does not contain "Hitler" and "Mussolini".
     * @param name the name to be checked
     * @return true if the name does not contain "Hitler" and "Mussolini", false otherwise
     */
    public static boolean checkName(String name) {
        String n = name.toLowerCase();
        return !n.contains("hitler") && !n.contains("mussolini");
    }

    /**
     * Checks the text for some curse and offensive words.
     * @param text the text to be checked
     * @return true if no curse or offensive words, false otherwise
     */
    public static boolean checkBadWords(String text) {
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
