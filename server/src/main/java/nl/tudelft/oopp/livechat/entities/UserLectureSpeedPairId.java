package nl.tudelft.oopp.livechat.entities;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class UserLectureSpeedPairId implements Serializable {

    private UUID uuid;
    private long uid;

    public UserLectureSpeedPairId() {
    }

    public UserLectureSpeedPairId(UUID uuid, long uid) {
        this.uuid = uuid;
        this.uid = uid;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserLectureSpeedPairId that = (UserLectureSpeedPairId) o;
        return uid == that.uid && uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, uid);
    }
}
