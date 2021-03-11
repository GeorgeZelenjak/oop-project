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

    private UUID modkey;


    /**
     * Instantiates a new Lecture.
     *
     * @param uuid        the lecture's id
     * @param modkey      the moderator key
     * @param name        the name
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
     * Instantiates a new Lecture.
     */
    public Lecture() {

    }

    /**
     * Gets uuid.
     *
     * @return the uuid
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Gets modkey.
     *
     * @return the modkey
     */
    public UUID getModkey() {
        return this.modkey;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets creator name.
     *
     * @return the creator name
     */
    public String getCreatorName() {
        return creatorName;
    }

    /**
     * Gets faster count.
     *
     * @return the faster count
     */
    public int getFasterCount() {
        return fasterCount;
    }

    /**
     * Gets slower count.
     *
     * @return the slower count
     */
    public int getSlowerCount() {
        return slowerCount;
    }

    /**
     * Gets frequency.
     *
     * @return the frequency
     */
    public int getFrequency() {
        return frequency;
    }

    /**
     * Gets start time.
     *
     * @return the start time
     */
    public Timestamp getStartTime() {
        return startTime;
    }

    /**
     * Is open boolean.
     *
     * @return boolean
     */
    public boolean isOpen() {
        return this.open;
    }

    /**
     * Sets current lecture.
     *
     * @param lecture the lecture
     */
    public static void setCurrentLecture(Lecture lecture) {
        Lecture.currentLecture = lecture;
    }

    /**
     * Gets current lecture.
     *
     * @return the current lecture
     */
    public static Lecture getCurrentLecture() {
        return Lecture.currentLecture;
    }

    public void setModkey(UUID modkey) {
        this.modkey = modkey;
    }


    @Override
    public String toString() {
        return "You created the lecture " + name + " by " + creatorName
                + " with ID: " + uuid + ". Share this id with students,"
                + " so they could join your lecture, with modKey:" + getModkey();
    }



}
