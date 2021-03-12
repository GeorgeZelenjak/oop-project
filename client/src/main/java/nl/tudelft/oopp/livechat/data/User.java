package nl.tudelft.oopp.livechat.data;


public class User {


    private static long uid;

    public static void setUid() { uid = (long) (Math.random()*10000); }

    public static long getUid() { return uid; }
}
