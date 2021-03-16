package nl.tudelft.oopp.livechat.data;

import com.google.gson.annotations.Expose;

import java.sql.Timestamp;
import java.util.UUID;

/**
 * Lecture class.
 */
public class Lecture {

    @Expose(serialize = true, deserialize = true)
    private static Lecture currentLecture;

    @Expose(serialize = true, deserialize = true)
    private UUID uuid;

    @Expose(serialize = true, deserialize = true)
    private String name;

    @Expose(serialize = true, deserialize = true)
    private String creatorName;

    @Expose(serialize = true, deserialize = true)
    private int fasterCount;

    @Expose(serialize = true, deserialize = true)
    private int slowerCount;

    @Expose(serialize = true, deserialize = true)
    private int frequency;

    @Expose(serialize = true, deserialize = true)
    private Timestamp startTime;

    @Expose(serialize = true, deserialize = true)
    private boolean open = true;

    @Expose(serialize = true, deserialize = true)
    private UUID modkey;


    /**
     * Creates a new lecture.
     * @param uuid the id of the lecture
     * @param modkey the moderator key
     * @param name the name of the lecture
     * @param creatorName the creator name
     */
    public Lecture(UUID uuid, UUID modkey, String name, String creatorName) {
        this.uuid = uuid;
        this.modkey = modkey;
        this.name = name;
        this.creatorName = creatorName;
        this.fasterCount = 0;
        this.slowerCount = 0;
        this.frequency = 60;
        this.startTime = new Timestamp(System.currentTimeMillis());
    }

    /**
     * Creates a new lecture.
     */
    public Lecture() {
    }

    /**
     * Gets the lecture id.
     * @return the lecture id
     */
    public UUID getUuid() {
        return this.uuid;
    }

    /**
     * Gets the modkey.
     * @return the modkey
     */
    public UUID getModkey() {
        return this.modkey;
    }

    /**
     * Sets the modkey.
     * @param modkey the modkey
     */
    //TODO decide if we need it
    public void setModkey(UUID modkey) {
        this.modkey = modkey;
    }

    /**
     * Gets the name of the lecture.
     * @return the name of the lecture
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the creator name.
     * @return the creator name
     */
    public String getCreatorName() {
        return this.creatorName;
    }

    /**
     * Gets the faster count.
     * @return the faster count
     */
    public int getFasterCount() {
        return this.fasterCount;
    }

    /**
     * Sets the new faster count.
     * @param fasterCount the new faster count
     */
    public void setFasterCount(int fasterCount) {
        this.fasterCount = fasterCount;
    }

    /**
     * Gets the slower count.
     * @return the slower count
     */
    public int getSlowerCount() {
        return this.slowerCount;
    }

    /**
     * Sets the new slower count.
     * @param slowerCount the new slower count
     */
    public void setSlowerCount(int slowerCount) {
        this.slowerCount = slowerCount;
    }

    /**
     * Gets the frequency of asking questions.
     * @return the frequency of asking questions
     */
    public int getFrequency() {
        return this.frequency;
    }

    /**
     * Sets the new frequency of asking questions.
     * @param newFrequency the new frequency of asking questions
     */
    public void setFrequency(int newFrequency) {
        this.frequency = newFrequency;
    }

    /**
     * Gets the start time of the lecture.
     * @return the start time of the lecture
     */
    public Timestamp getStartTime() {
        return this.startTime;
    }

    /**
     * Checks if the lecture is open.
     * @return true is the lecture is open, false otherwise
     */
    public boolean isOpen() {
        return this.open;
    }

    /**
     * Closes the lecture.
     */
    public void close() {
        this.open = false;
    }

    /**
     * Converts the lecture to String format.
     * @return the lecture in String format
     */
    @Override
    public String toString() {
        return "Lecture " + name + " by " + creatorName
                + " with ID: " + uuid + ". Share this id with students,"
                + " so they could join your lecture, with modKey:" + getModkey();
    }

    /**
     * Sets current lecture.
     * @param lecture the lecture
     */
    public static void setCurrentLecture(Lecture lecture) {
        Lecture.currentLecture = lecture;
    }

    /**
     * Gets current lecture.
     * @return the current lecture
     */
    public static Lecture getCurrentLecture() {
        return Lecture.currentLecture;
    }

}
