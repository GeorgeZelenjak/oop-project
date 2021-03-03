package nl.tudelft.oopp.livechat.data;

import java.sql.Timestamp;

/**
 * Lecture class.
 */
public class Lecture {


    private String uuid;

    private String modkey;

    private String name;

    private String creatorName;

    private int fasterCount;

    private int slowerCount;

    private int frequency;

    private Timestamp startTime;

    private boolean open = true;


    /**
     * Instantiates a new Lecture.
     *
     * @param uuid        the lecture's id
     * @param modkey      the moderator key
     * @param name        the name
     * @param creatorName the creator name
     */
    public Lecture(String uuid, String modkey, String name, String creatorName) {
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
    public String getUuid() {
        return uuid;
    }

    /**
     * Gets modkey.
     *
     * @return the modkey
     */
    public String getModkey() {
        return modkey;
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
     * Close.
     */
    public void close() {
        this.open = false;
    }

    /**
     * Re open.
     */
    public void reOpen() {
        this.open = true;
    }

    @Override
    public String toString() {
        return "You created the lecture " + name + " by " + creatorName;
    }



}
