package nl.tudelft.oopp.livechat.entities;

import com.sun.xml.bind.v2.model.core.ID;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

/**
 * The type User lecture speed table.
 */

@Table(name = "UserLectureSpeedTable")
@Entity
@IdClass(UserLectureSpeedPairId.class)
public class UserLectureSpeedTable {

    @Id
    @Column(name = "userId")
    private long userId;

    @Id
    @Column(name = "lectureId")
    private UUID lectureId;


    @Column(name = "voteOnLectureSpeed")
    private String voteOnLectureSpeed;

    /**
     * Creates a new UserLectureSpeedTable object.
     */
    public UserLectureSpeedTable() {
    }

    /**
     * Creates a new UserLectureSpeedTable object.
     * @param userId the user id
     * @param lectureId the lecture id
     * @param voteOnLectureSpeed the vote on lecture speed
     */
    public UserLectureSpeedTable(long userId, UUID lectureId, String voteOnLectureSpeed) {
        this.userId = userId;
        this.lectureId = lectureId;
        this.voteOnLectureSpeed = voteOnLectureSpeed;
    }

    /**
     * Gets the user id.
     * @return the user id
     */
    public long getUserId() {
        return this.userId;
    }

    /**
     * Sets the user id.
     * @param userId the user id
     */
    public void setUserId(long userId) {
        this.userId = userId;
    }

    /**
     * Gets the lecture id.
     * @return the lecture id
     */
    public UUID getLectureId() {
        return this.lectureId;
    }

    /**
     * Sets the lecture id.
     * @param lectureId the lecture id
     */
    public void setLectureId(UUID lectureId) {
        this.lectureId = lectureId;
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
            return this.userId == u.userId && lectureId.equals(u.lectureId);
        }
        return false;
    }

    /**
     * Generates and gets the hash code of the object.
     * @return the hash code of the object
     */
    @Override
    public int hashCode() {
        return Objects.hash(userId, lectureId);
    }
}
