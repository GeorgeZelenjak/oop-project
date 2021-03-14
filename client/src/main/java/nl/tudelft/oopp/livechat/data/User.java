package nl.tudelft.oopp.livechat.data;

/**
 * User class.
 */
public class User {
    private static String userName;

    private static long uid;

    /**
     * Sets uid.
     */
    public static void setUid() {
        uid = (long) (Math.random() * 10000);
    }

    /**
     * Gets uid.
     *
     * @return the uid
     */
    public static long getUid() {
        return uid;
    }

    /**
     * Sets user name.
     *
     * @param name the name
     */
    public static void setUserName(String name) {
        userName = name;
    }

    /**
     * Gets user name.
     *
     * @return the user name
     */
    public static String getUserName() {
        return userName;
    }
}
