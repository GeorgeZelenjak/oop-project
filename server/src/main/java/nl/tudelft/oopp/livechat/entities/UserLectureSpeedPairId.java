package nl.tudelft.oopp.livechat.entities;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class UserLectureSpeedPairId implements Serializable {

    private UUID lectureId;
    private long userId;

    /**
     * Creates a new UserLectureSpeedPairId object.
     */
    public UserLectureSpeedPairId() {
    }

    /**
     * Creates a new UserLectureSpeedPairId object with the specified parameters.
     * @param lectureId the id of the lecture
     * @param userId the id of the user
     */
    public UserLectureSpeedPairId(UUID lectureId, long userId) {
        this.lectureId = lectureId;
        this.userId = userId;
    }


    /**
     * Gets the id of the user.
     * @return the id of the user
     */
    public long getUserId() {
        return this.userId;
    }

    /**
     * Sets the id of the user.
     * @param userId the id of the user
     */
    public void setUserId(long userId) {
        this.userId = userId;
    }

    /**
     * Gets the id of the lecture.
     * @return the id of the lecture
     */
    public UUID getLectureId() {
        return this.lectureId;
    }

    /**
     * Sets the id of the lecture.
     * @param lectureId the the id of the lecture
     */
    public void setLectureId(UUID lectureId) {
        this.lectureId = lectureId;
    }

    /**
     * Compares the UserLectureSpeedPairId to another object.
     * @param o the object to compare to
     * @return true iff another object is also UserLectureSpeedPairId
     *          and has the same user id and lecture id.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof UserLectureSpeedPairId) {
            UserLectureSpeedPairId that = (UserLectureSpeedPairId) o;
            return this.userId == that.userId && this.lectureId == that.lectureId;
        }
        return false;
    }

    /**
     * Creates the hash code for the object.
     * @return the created hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(lectureId, userId);
    }
}
