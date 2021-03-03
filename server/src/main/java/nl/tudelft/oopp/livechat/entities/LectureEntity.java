package nl.tudelft.oopp.livechat.entities;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.*;


/**
 * The type Lecture entity.
 */
@Entity
@Table(name = "lectures")
public class LectureEntity {


    @Id
    @Column(name = "uuid")
    private final String uuid;

    @Column(name = "modkey")
    private final String modkey;

    @Column(name = "name")
    private String name;

    @Column(name = "creatorName")
    private String creatorName;

    @Column(name = "fasterCount")
    private int fasterCount;

    @Column(name = "slowerCount")
    private int slowerCount;

    @Column(name = "frequency")
    private int frequency;

    @Column(name = "startTime")
    private Timestamp startTime;

    @Column(name = "open")
    private boolean open = true;

    /**
     * Instantiates a new Lecture entity.
     */
    public LectureEntity() {
        this.uuid = generateUUID();
        this.modkey = generateUUID();
    }


    /**
     * Instantiates a new Lecture entity.
     *
     * @param name        the name
     * @param creatorName the creator name
     */
    public LectureEntity(String name, String creatorName) {
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
        if (o instanceof LectureEntity) {
            LectureEntity l = (LectureEntity) o;
            return uuid.equals(l.uuid);
        }
        return false;
    }


    @Override
    public int hashCode() {
        return Objects.hash(uuid, modkey, name, creatorName);
    }
}
