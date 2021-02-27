package nl.tudelft.oopp.livechat.entities;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "lectures")
public class LectureEntity {


    @Id
    @Column(name = "uuid")
    private String uuid;

    @Column(name = "modkey")
    private String modkey;

    public LectureEntity() {
        this.uuid = generateUUID();
        this.modkey = generateUUID();
    }


    public static String generateUUID(){
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
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LectureEntity l = (LectureEntity) o;

        return uuid.equals(l.uuid);
    }
}
