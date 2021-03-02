package nl.tudelft.oopp.livechat.entities;

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
    private final UUID uuid;

    @Column(name = "modkey")
    private final UUID modkey;

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
    private LocalDateTime startTime;

    public LectureEntity() {
        this.uuid = UUID.randomUUID();
        this.modkey = UUID.randomUUID();
    }


    /**
     * Instantiates a new Lecture entity.
     *
     * @param name        the name
     * @param creatorName the creator name
     * @param startTime   the start time
     */
    public LectureEntity(String name, String creatorName, LocalDateTime startTime) {
        this.uuid = UUID.randomUUID();
        this.modkey = UUID.randomUUID();
        this.name = name;
        this.creatorName = creatorName;
        this.fasterCount = 0;
        this.slowerCount = 0;
        this.frequency = 60;
        this.startTime = startTime.withNano(0);
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

    public int getFasterCount() {
        return fasterCount;
    }

    public int getSlowerCount() {
        return slowerCount;
    }

    public LocalDateTime getStartTime() {
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
