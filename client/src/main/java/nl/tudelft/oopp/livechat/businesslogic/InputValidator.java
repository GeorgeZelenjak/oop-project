package nl.tudelft.oopp.livechat.businesslogic;

public abstract class InputValidator {

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
}
