package nl.tudelft.oopp.livechat.entities;

//import com.sun.xml.bind.v2.model.core.ID;

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
     * Creates a new UserLectureSpeedTable object.
     */
    public UserLectureSpeedTable() {
    }

    /**
     * Creates a new UserLectureSpeedTable object.
     * @param uid the user id
     * @param uuid the lecture id
     * @param voteOnLectureSpeed the vote on lecture speed
     */
    public UserLectureSpeedTable(long uid, UUID uuid, String voteOnLectureSpeed) {
        this.uid = uid;
        this.uuid = uuid;
        this.voteOnLectureSpeed = voteOnLectureSpeed;
    }

    /**
     * Gets the user id.
     * @return the user id
     */
    public long getUid() {
        return this.uid;
    }

    /**
     * Sets the user id.
     * @param uid the user id
     */
    public void setUid(long uid) {
        this.uid = uid;
    }

    /**
     * Gets the lecture id.
     * @return the lecture id
     */
    public UUID getUuid() {
        return this.uuid;
    }

    /**
     * Sets the lecture id.
     * @param uuid the lecture id
     */
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * Gets the vote on the lecture speed.
     * @return the vote on the lecture speed
     */
    public String getVoteOnLectureSpeed() {
        return this.voteOnLectureSpeed;
    }

    /**
     * Sets the vote on the lecture speed.
     * @param voteOnLectureSpeed the vote on the lecture speed
     */
    public void setVoteOnLectureSpeed(String voteOnLectureSpeed) {
        this.voteOnLectureSpeed = voteOnLectureSpeed;
    }

    /**
     * Compares the UserLectureSpeedTable to another object.
     * @param obj the object to compare to
     * @return true iff the object is also UserLectureSpeedTable
     *      and has the same user id and lecture id. Otherwise false
     */
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

    /**
     * Generates and gets the hash code of the object.
     * @return the hash code of the object
     */
    @Override
    public int hashCode() {
        return Objects.hash(uid, uuid, voteOnLectureSpeed);
    }
}
