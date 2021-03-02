package nl.tudelft.oopp.demo.data;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Lecture class
 */
public class Lecture {


    private String uuid;

    private String modkey;

    private String name;

    private String creatorName;

    private int fasterCount;

    private int slowerCount;

    private int frequency;

    private Timestamp startTime;




    /**
     * Instantiates a new Lecture .
     * @param uuid        the lecture's id
     * @param modkey      the moderator key
     * @param name        the name
     * @param creatorName the creator name
     */
    public Lecture(String uuid, String modkey, String name, String creatorName) {
        this.uuid = uuid;
        this.modkey = modkey;
        this.name = name;
        this.creatorName = creatorName;
        this.fasterCount = 0;
        this.slowerCount = 0;
        this.frequency = 60;
        this.startTime = new Timestamp(System.currentTimeMillis());
    }
    public Lecture(){

    }

    public String getUuid() {
        return uuid;
    }

    public String getModkey() {
        return modkey;
    }

    public String getName() {
        return name;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public int getFasterCount() {
        return fasterCount;
    }

    public int getSlowerCount() {
        return slowerCount;
    }

    public int getFrequency() {
        return frequency;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    //public void setStartTime(LocalDateTime startTime) {
    //    this.startTime = startTime;
    //}

    @Override
    public String toString() {
        return "You created the lecture " + name + " by " + creatorName;
    }



}
