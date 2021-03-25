package nl.tudelft.oopp.livechat.entities.poll;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PollAndOptionsTest {
    private static PollEntity poll;
    private static PollOptionEntity option1;
    private static PollOptionEntity option2;
    private static PollAndOptions pollAndOptions;
    private static final UUID lid = UUID.randomUUID();
    private static final Timestamp time = new Timestamp(System.currentTimeMillis());

    @BeforeAll
    static void setUp() {
        poll = new PollEntity(lid, "What would you do if a pelican entered in your house?",
                time, 13, true);
        option1 = new PollOptionEntity(poll.getId(), "Kill it", 3, false);
        option2 = new PollOptionEntity(poll.getId(), "Eat it", 7, true);
        pollAndOptions = new PollAndOptions(poll, List.of(option1, option2));
    }

    @Test
    void emptyConstructorTest() {
        PollAndOptions p = new PollAndOptions();
        assertNotNull(p);
    }

    @Test
    void constructorTest() {
        assertNotNull(pollAndOptions);
    }

    @Test
    void getPollTest() {
        assertEquals(poll, pollAndOptions.getPoll());
    }

    @Test
    void setPollTest() {
        PollEntity p = new PollEntity(lid, "What would you do if a seagull entered in your house?",
                time, 12, false);
        pollAndOptions.setPoll(p);
        assertEquals(p, pollAndOptions.getPoll());

        pollAndOptions.setPoll(poll);
    }

    @Test
    void getOptionsTest() {
        assertEquals(List.of(option1, option2), pollAndOptions.getOptions());
    }

    @Test
    void setOptionsTest() {
        PollOptionEntity o1 = new PollOptionEntity(poll.getId(), "Fry it", 5, false);
        PollOptionEntity o2 = new PollOptionEntity(poll.getId(), "Bake it", 9, true);
        List<PollOptionEntity> options = List.of(o1, o2);
        pollAndOptions.setOptions(options);
        assertEquals(options, pollAndOptions.getOptions());

        pollAndOptions.setOptions(List.of(option1, option2));
    }

    @Test
    void equalsNullTest() {
        assertNotEquals(pollAndOptions, null);
    }

    @Test
    void equalsSameTest() {
        assertEquals(pollAndOptions, pollAndOptions);
    }

    @Test
    void equalsEqualTest() {
        PollAndOptions po = new PollAndOptions(poll, List.of(option1, option2));
        assertEquals(pollAndOptions, po);
    }

    @Test
    void equalsDifferentTest() {
        PollEntity p = new PollEntity(lid, "What would you do if a pelican entered in your house?",
                time, 13, true);
        PollOptionEntity o1 = new PollOptionEntity(p.getId(), "Kill it", 3, false);
        PollOptionEntity o2 = new PollOptionEntity(p.getId(), "Eat it", 7, true);
        PollAndOptions po = new PollAndOptions(p, List.of(o1, o2));
        assertNotEquals(pollAndOptions, po);
    }

    @Test
    void hashCodeTest() {
        int hash = Objects.hash(pollAndOptions.getPoll(), pollAndOptions.getOptions());
        assertEquals(hash, pollAndOptions.hashCode());
    }

    @Test
    void hashCodeDifferentTest() {
        PollEntity p = new PollEntity(lid, "What would you do if a pelican entered in your house?",
                time, 13, true);
        PollOptionEntity o1 = new PollOptionEntity(p.getId(), "Kill it", 3, false);
        PollOptionEntity o2 = new PollOptionEntity(p.getId(), "Eat it", 7, true);
        PollAndOptions po = new PollAndOptions(p, List.of(o1, o2));
        assertNotEquals(po.hashCode(), pollAndOptions.hashCode());
    }
}