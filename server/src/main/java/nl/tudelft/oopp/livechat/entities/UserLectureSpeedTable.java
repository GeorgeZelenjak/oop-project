package nl.tudelft.oopp.livechat.entities;

import com.sun.xml.bind.v2.model.core.ID;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

/**
 * The type User lecture speed table.
 */

@Entity
@Table(name = "UserLectureSpeedTable")
@IdClass(UserLectureSpeedPairId.class)
public class UserLectureSpeedTable {

    @Id
    @Column(name = "User_id")
    private long uid;

    @Id
    @Column(name = "Uuid")
    private UUID uuid;


    @Column(name = "Vote")
    private String voteOnLectureSpeed;

    /**
     * Instantiates a new User lecture speed table.
     */
    public UserLectureSpeedTable() {
    }

    /**
     * Instantiates a new User lecture speed table.
     *
     * @param uid                the uid
     * @param uuid               the uuid
     * @param voteOnLectureSpeed the vote on lecture speed
     */
    public UserLectureSpeedTable(long uid, UUID uuid, String voteOnLectureSpeed) {
        this.uid = uid;
        this.uuid = uuid;
        this.voteOnLectureSpeed = voteOnLectureSpeed;
    }

    /**
     * Gets uid.
     *
     * @return the uid
     */
    public long getUid() {
        return uid;
    }

    /**
     * Sets uid.
     *
     * @param uid the uid
     */
    public void setUid(long uid) {
        this.uid = uid;
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
     * Sets uuid.
     *
     * @param uuid the uuid
     */
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * Gets vote on lecture speed.
     *
     * @return the vote on lecture speed
     */
    public String getVoteOnLectureSpeed() {
        return voteOnLectureSpeed;
    }

    /**
     * Sets vote on lecture speed.
     *
     * @param voteOnLectureSpeed the vote on lecture speed
     */
    public void setVoteOnLectureSpeed(String voteOnLectureSpeed) {
        this.voteOnLectureSpeed = voteOnLectureSpeed;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof UserLectureSpeedTable) {
            UserLectureSpeedTable u = (UserLectureSpeedTable) obj;
            return this.uid == u.uid && uuid.equals(u.uuid);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, uuid, voteOnLectureSpeed);
    }


}
