package nl.tudelft.oopp.livechat.entities;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class UserQuestionTableTest {
    private static UserQuestionTable table;

    /**
     * Set up for the test class.
     */
    @BeforeAll
    public static void setUp() {
        table = new UserQuestionTable(54323948572543L, 1024324452432L);
    }

    @Test
    public void constructorTest() {
        assertNotNull(table);
    }

    @Test
    public void getUserIdTest() {
        assertEquals(54323948572543L, table.getUserId());
    }

    @Test
    public void setUserIdTest() {
        table.setUserId(52345234523451346L);
        assertEquals(52345234523451346L, table.getUserId());

        //change back for other tests
        table.setUserId(54323948572543L);
    }

    @Test
    public void getQuestionIdTest() {
        assertEquals(1024324452432L, table.getQuestionId());
    }

    @Test
    public void setQuestionIdTest() {
        table.setQuestionId(1454645365562652342L);
        assertEquals(1454645365562652342L, table.getQuestionId());

        //change back for other tests
        table.setQuestionId(1024324452432L);
    }

    @Test
    public void equalsNullTest() {
        assertNotEquals(table, null);
    }

    @Test
    public void equalsSameTest() {
        assertEquals(table, table);
    }

    @Test
    public void equalsEqualTest() {
        UserQuestionTable table2 = new UserQuestionTable(54323948572543L, 1024324452432L);
        assertEquals(table, table2);
    }

    @Test
    public void equalsDifferentTest() {
        UserQuestionTable table2 = new UserQuestionTable();
        assertNotEquals(table, table2);
    }

    @Test
    public void equalsDifferentUserIdTest() {
        UserQuestionTable table2 = new UserQuestionTable(41345135325L, 1024324452432L);
        assertNotEquals(table, table2);
    }

    @Test
    public void equalsDifferentQuestionIdTest() {
        UserQuestionTable table2 = new UserQuestionTable(54323948572543L, 34513453425554L);
        assertNotEquals(table, table2);
    }

    @Test
    public void hashCodeTest() {
        int hash = Objects.hash(54323948572543L, 1024324452432L);
        assertEquals(hash, table.hashCode());
    }
}
