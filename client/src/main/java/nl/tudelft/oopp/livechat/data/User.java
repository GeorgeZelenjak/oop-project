package nl.tudelft.oopp.livechat.data;


public class User {
    private static String userName;

    private static long uid;

    public static void setUid() {
        uid = (long) (Math.random() * 10000);
    }

    public static long getUid() {
        return uid;
    }

    public static void setUserName(String name) {
        userName = name;
    }

    public static String getUserName() {
        return userName;
    }
}
