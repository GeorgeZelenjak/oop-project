package nl.tudelft.oopp.livechat.entities;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * The type User entity.
 */
@Entity(name = "user")
@Table(name = "users")
@DynamicUpdate
public class UserEntity {

    /**
     * The Uid.
     */
    @Id
    @Column(name = "id")
    long uid;

    /**
     * The Username.
     */
    @Column(name = "userName")
    String userName;

    /**
     * The Last question timestamp.
     */
    @Column(name = "last_question")
    Timestamp lastQuestion;

    /**
     * Allowed to ask questions.
     */
    @Column(name = "allowed")
    boolean allowed = true;

    /**
     * The Ip.
     */
    @Column(name = "ip")
    String ip;

    /**
     * The Lecture id.
     */
    @Column(name = "lectureid")
    UUID lectureId;

    /**
     * The Banner id.
     */
    @Column(name = "bannerid")
    long bannerId;

    /**
     * Instantiates a new User entity.
     */
    public UserEntity() {
    }

    /**
     * Instantiates a new User entity.
     * @param uid          the user id
     * @param username     the username
     * @param lastQuestion the last question the user asked
     * @param allowed      if the user is banned
     * @param ip           the ip address of the user
     * @param lectureId    the lecture id the user is in
     */
    public UserEntity(long uid, String username, Timestamp lastQuestion,
                      boolean allowed, String ip, UUID lectureId) {
        this.uid = uid;
        this.userName = username;
        this.lastQuestion = lastQuestion;
        this.allowed = allowed;
        this.ip = ip;
        this.lectureId = lectureId;
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
     * Gets username.
     *
     * @return the username
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Gets last question the user asked.
     *
     * @return the last question the user asked
     */
    public Timestamp getLastQuestion() {
        return lastQuestion;
    }

    /**
     * Returns if the user is banned or not.
     *
     * @return true if the user is not banned, false if is
     */
    public boolean isAllowed() {
        return allowed;
    }

    /**
     * Sets the user id.
     *
     * @param uid the user id
     */
    public void setUid(long uid) {
        this.uid = uid;
    }

    /**
     * Sets the username.
     *
     * @param username the username to be set
     */
    public void setUserName(String username) {
        this.userName = username;
    }

    /**
     * Sets last question the user asked.
     *
     * @param lastQuestion the last question the user asked
     */
    public void setLastQuestion(Timestamp lastQuestion) {
        this.lastQuestion = lastQuestion;
    }

    /**
     * Sets if the user is banned or not.
     *
     * @param allowed true if the user is not banned, false if is
     */
    public void setAllowed(boolean allowed) {
        this.allowed = allowed;
    }

    /**
     * Gets the ip of the user.
     *
     * @return the ip of the user
     */
    public String getIp() {
        return ip;
    }

    /**
     * Sets the ip of the user.
     *
     * @param ip the ip of the user
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * Gets the lecture id the user is in.
     *
     * @return the lecture id the user is in
     */
    public UUID getLectureId() {
        return lectureId;
    }

    /**
     * Sets lecture id the user is in.
     *
     * @param lectureId the lecture id the user is in
     */
    public void setLectureId(UUID lectureId) {
        this.lectureId = lectureId;
    }


    /**
     * Gets the id of the moderator that has banned th user.
     * @return the id of the moderator that has banned th user, or 0 if the user is not banned
     */
    public long getBannerId() {
        return bannerId;
    }

    /**
     * Sets the banner the id of the moderator that has banned th user.
     *
     * @param bannerId the id of the moderator that has banned th user,
     *                 or 0 if the user is to be unbanned
     */
    public void setBannerId(long bannerId) {
        this.bannerId = bannerId;
    }

    /**
     * Gets the hash code of the object.
     * @return the hash code of the object.
     */
    @Override
    public int hashCode() {
        return (int) (uid >> 32) + (int) uid;
    }

    /**
     * A String representation of the user entity.
     * @return String representation of the user entity
     */
    @Override
    public String toString() {
        return "username: '" + this.userName + "', user id: "
                + this.uid + ", ip address: " + this.ip;
    }

    /**
     * A method to compare the user with another object.
     * @param obj the object to compare to
     * @return true iff the other object is also a UserEntity and has the same id
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof UserEntity) {
            UserEntity u = (UserEntity) obj;
            return this.uid == u.uid;
        }
        return false;
    }

}
