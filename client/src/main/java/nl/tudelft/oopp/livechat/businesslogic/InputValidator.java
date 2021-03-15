package nl.tudelft.oopp.livechat.businesslogic;

public class InputValidator {
    /**
     * Validates user name.
     *
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
}
