package nl.tudelft.oopp.lecture;

import java.util.UUID;

public class Lecture {

    private String uuid;

    private String modkey;

    public Lecture() {
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
        if (o instanceof Lecture) {
            Lecture l = (Lecture) o;
            return uuid.equals(l.uuid);
        }
        return false;
    }
}
