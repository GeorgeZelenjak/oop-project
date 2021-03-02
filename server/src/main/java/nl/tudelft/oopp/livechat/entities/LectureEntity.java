package nl.tudelft.oopp.livechat.entities;

import java.time.LocalDateTime;
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

    @Column(name = "creator_name")
    private String creator_name;

    @Column(name = "faster_count")
    private int faster_count;

    @Column(name = "slower_count")
    private int slower_count;

    @Column(name = "frequency")
    private int frequency;

    @Column(name = "start_time")
    private LocalDateTime start_time;

    public LectureEntity() {
        this.uuid = generateUUID();
        this.modkey = generateUUID();
    }


    /**
     * Instantiates a new Lecture entity.
     *
     * @param name        the name
     * @param creatorName the creator name
     * @param startTime   the start time
     */
    public LectureEntity(String name, String creatorName, LocalDateTime startTime) {
        this.uuid = generateUUID();
        this.modkey = generateUUID();
        this.name = name;
        this.creator_name = creatorName;
        this.faster_count = 0;
        this.slower_count = 0;
        this.frequency = 60;
        this.start_time = startTime;
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
    public String getCreator_name() {
        return creator_name;
    }

    /**
     * Gets faster count.
     *
     * @return the faster count
     */
    public int getFaster_count() {
        return faster_count;
    }

    /**
     * Gets slower count.
     *
     * @return the slower count
     */
    public int getSlower_count() {
        return slower_count;
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
     * Increase faster count.
     */
    public void increaseFaster_count() {
        this.faster_count++;
    }

    /**
     * Increase slower count.
     */
    public void increaseSlower_count() {
        this.slower_count++;
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
        this.faster_count = 0;
        this.slower_count = 0;
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
}
