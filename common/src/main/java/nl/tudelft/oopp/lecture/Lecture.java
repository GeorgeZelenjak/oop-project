package nl.tudelft.oopp.lecture;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;

/**
 * The type Lecture.
 */
public class Lecture {

    private final String uuid;

    private final String modkey;

    private String name;

    private String creatorName;

    private int fasterCount;

    private int slowerCount;

    private int frequency;

    private Timestamp startTime;

    private boolean open = true;

    /**
     * Instantiates a new Lecture entity.
     */
    public Lecture() {
        this.uuid = generateUUID();
        this.modkey = generateUUID();
    }


    /**
     * Instantiates a new Lecture.
     *
     * @param name        the name
     * @param creatorName the creator name
     */
    public Lecture(String name, String creatorName) {
        this.uuid = generateUUID();
        this.modkey = generateUUID();
        this.name = name;
        this.creatorName = creatorName;
        this.fasterCount = 0;
        this.slowerCount = 0;
        this.frequency = 60;
        this.startTime = new Timestamp(System.currentTimeMillis());
    }

    /**
     * Generate uuid string.
     *
     * @return the string
     */
    public static String generateUUID() {
        UUID generated = UUID.randomUUID();
        return generated.toString();
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
     * Gets start time.
     *
     * @return the start time
     */
    public Timestamp getStartTime() {
        return startTime;
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
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * Sets frequency.
     *
     * @param frequency the frequency
     */
    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    /**
     * Reset speed counts.
     */
    public void resetSpeedCounts() {
        this.fasterCount = 0;
        this.slowerCount = 0;
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

    /**
     * Is open .
     *
     * @return boolean
     */
    public boolean isOpen() {
        return this.open;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Lecture) {
            Lecture l = (Lecture) o;
            return uuid.equals(l.uuid);
        }
        return false;
    }


    @Override
    public int hashCode() {
        return Objects.hash(uuid, modkey, name, creatorName);
    }
}
