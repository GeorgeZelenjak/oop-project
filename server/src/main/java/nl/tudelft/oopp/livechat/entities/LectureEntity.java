package nl.tudelft.oopp.livechat.entities;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "lectures")
public class LectureEntity {


    @Id
    @Column(name = "uuid")
    private final String uuid;

    @Column(name = "modkey")
    private final String modkey;

    public LectureEntity() {
        this.uuid = generateUUID();
        this.modkey = generateUUID();
    }

    public static String generateUUID() {
        UUID generated = UUID.randomUUID();
        return generated.toString();
    }

    public String getUuid() {
        return uuid;
    }

    public String getModkey() {
        return modkey;
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
