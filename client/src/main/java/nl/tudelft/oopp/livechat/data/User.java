package nl.tudelft.oopp.livechat.data;

import com.google.gson.annotations.Expose;
import nl.tudelft.oopp.livechat.controllers.AlertController;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.HashSet;
import java.util.Set;

/**
 * User class.
 */
public class User {
    private static String userName;

    private static long uid;

    @Expose(serialize = false)
    private static Set<Long> askedQuestionIds = new HashSet<>();

    @Expose(serialize = false)
    private static Set<Long> upvotedQuestionIds = new HashSet<>();

    private User() {

    }

    /**
     * Gets upvoted question ids.
     * @return the upvoted question ids
     */
    public static Set<Long> getUpvotedQuestionIds() {
        return upvotedQuestionIds;
    }

    /**
     * Sets upvoted question ids.
     * @param ids the upvoted question ids
     */
    public static void setUpvotedQuestionIds(Set<Long> ids) {
        upvotedQuestionIds = ids;
    }

    /**
     * Sets uid.
     */
    public static void setUid() {
        byte[] hardwareAddress;
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            NetworkInterface ni = NetworkInterface.getByInetAddress(localHost);
            hardwareAddress = ni.getHardwareAddress();
        } catch (Exception e) {
            AlertController.alertError("Error in creating User ID",
                    "Couldn't create a valid user-id");
            return;
        }
        long uidTemp = 0;
        for (int i = 0;i < hardwareAddress.length;i++) {
            long unsigned = (long) hardwareAddress[i] & 0xFF;
            uidTemp += unsigned << (8 * i);
        }
        uid = uidTemp * 10 + getLuhnDigit(uidTemp);
    }

    /**
     * Gets luhn digit to make luhn checksum valid.
     *
     * @param n the number
     * @return the luhn digit
     */
    public static long getLuhnDigit(long n) {
        String number = Long.toString(n);
        long temp = 0;
        for (int i = number.length() - 1;i >= 0;i--) {
            int digit;
            if ((number.length() - i) % 2 == 1) {
                digit = Character.getNumericValue(number.charAt(i)) * 2;
                if (digit > 9) {
                    digit %= 9;
                    if (digit == 0) digit = 9;
                }
            } else {
                digit = Character.getNumericValue(number.charAt(i));
            }
            temp += digit;
        }
        return (10 - (temp % 10)) % 10;
    }


    /**
     * Gets uid.
     * @return the uid
     */
    public static long getUid() {
        return uid;
    }

    /**
     * Sets user name.
     * @param name the name
     */
    public static void setUserName(String name) {
        userName = name;
    }

    /**
     * Gets user name.
     * @return the user name
     */
    public static String getUserName() {
        return userName;
    }

    /**
     * Gets the set of asked question ids.
     * @return the set of asked question ids
     */
    public static Set<Long> getAskedQuestionIds() {
        return askedQuestionIds;
    }

    /**
     * Sets the set of asked question ids.
     * @param questionIds the set of asked question ids
     */
    @SuppressWarnings("unused")
    public static void setAskedQuestionsIds(Set<Long> questionIds) {
        askedQuestionIds = questionIds;
    }

    /**
     * Adds a question id to the set of asked question ids.
     * @param qid the question id to be added
     * @return true if added, false otherwise
     */
    public static boolean addQuestionId(Long qid) {
        return askedQuestionIds.add(qid);
    }

    /**
     * Deletes question id from the set of asked question ids.
     * @param questionId the question id to be deleted
     */
    public static void deleteQuestionId(Long questionId) {
        askedQuestionIds.remove(questionId);
    }


}
