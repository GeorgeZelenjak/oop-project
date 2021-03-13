package nl.tudelft.oopp.livechat.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.*;

/**
 * The class that represents a lecture entity.
 */
@Entity
@Table(name = "lectures")
public class LectureEntity {


    @Id
    @Column(name = "uuid")
    private final UUID uuid;

    @Column(name = "modkey")
    private UUID modkey;

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
    private Timestamp startTime = new Timestamp(System.currentTimeMillis() / 1000 * 1000);

    @Column(name = "open")
    private boolean open = true;

    /**
     * Empty constructor to create a lecture entity.
     */
    public LectureEntity() {
        this.uuid = UUID.randomUUID();
        this.modkey = UUID.randomUUID();
    }

    /**
     * Constructor to create a lecture entity.
     * @param name the name of the lecture
     * @param creatorName the name of the creator of the lecture
     */
    public LectureEntity(String name, String creatorName) {
        this.uuid = UUID.randomUUID();
        this.modkey = UUID.randomUUID();
        this.name = name;
        this.creatorName = creatorName;
        this.fasterCount = 0;
        this.slowerCount = 0;
        this.frequency = 60;
    }

    /**
     * Static constructor to create a lecture entity.
     * @param name the name of the lecture
     * @param creatorName the name of the creator of the lecture
     * @return a new lecture entity
     */
    public static LectureEntity create(String name, String creatorName) {
        LectureEntity l = new LectureEntity();
        l.name = name;
        l.creatorName = creatorName;
        l.fasterCount = 0;
        l.slowerCount = 0;
        l.frequency = 60;
        l.startTime = new Timestamp(System.currentTimeMillis());
        return l;
    }

    /**
     * Gets the uuid of the lecture.
     * @return the uuid of the lecture
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Gets modkey of the lecture.
     * @return the modkey of the lecture
     */
    public UUID getModkey() {
        return modkey;
    }

    /**
     * Set modkey to null.
     * this is done only on in-memory objects and not in database
     */
    public void setModkey(UUID modkey) {
        this.modkey = modkey;
    }

    /**
     * Gets name of the lecture.
     * @return the name of the lecture
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the name of the creator of the lecture.
     * @return the name of the creator of the lecture
     */
    public String getCreatorName() {
        return creatorName;
    }

    /**
     * Gets "faster" count.
     * @return the "faster" count
     */
    public int getFasterCount() {
        return fasterCount;
    }

    /**
     * Gets "slower" count.
     * @return the "slower" count
     */
    public int getSlowerCount() {
        return slowerCount;
    }

    /**
     * Gets the start time of the lecture.
     * @return the start time of the lecture
     */
    public Timestamp getStartTime() {
        return startTime;
    }

    /**
     * Gets the frequency of asking questions.
     * @return the frequency of asking questions
     */
    public int getFrequency() {
        return frequency;
    }

    /**
     * Sets the name of the lecture.
     * @param name the name of the lecture
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Increases the "faster" count by 1.
     */
    public void incrementFasterCount() {
        ++this.fasterCount;
    }

    /**
     * Increases the "slower" count by 1.
     */
    public void incrementSlowerCount() {
        ++this.slowerCount;
    }

    /**
     * Sets the frequency of asking questions.
     * @param frequency the frequency of asking questions
     */
    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    /**
     * Resets speed counts.
     */
    public void resetSpeedCounts() {
        this.fasterCount = 0;
        this.slowerCount = 0;
    }

    /**
     * Closes the lecture.
     */
    @SuppressWarnings("unused")
    public void close() {
        this.open = false;
    }

    /**
     * Re-opens the lecture.
     */
    @SuppressWarnings("unused")
    public void reOpen() {
        this.open = true;
    }

    /**
     * Checks whether the lecture is open.
     * @return whether the lecture is open
     */
    @SuppressWarnings("unused")
    public boolean isOpen() {
        return this.open;
    }

    /**
     * Compares the lecture to another object.
     * @param o object to compare to
     * @return true iff the other object is also a Lecture and has the same id. False otherwise
     */
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

    /**
     * The hash code of the Lecture object.
     * @return the hash code of the Lecture object
     */
    @Override
    public int hashCode() {
        return Objects.hash(uuid, modkey, name, creatorName, startTime);
    }
}
