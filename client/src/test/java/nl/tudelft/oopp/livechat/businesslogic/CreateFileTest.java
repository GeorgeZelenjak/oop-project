package nl.tudelft.oopp.livechat.businesslogic;

import nl.tudelft.oopp.livechat.data.Lecture;
import nl.tudelft.oopp.livechat.data.Question;
import org.apache.commons.io.FileDeleteStrategy;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class CreateFileTest {
    private static final UUID lectureId = UUID.randomUUID();
    private static final UUID modkey = UUID.randomUUID();

    /**
     * A helper method to delete files and directory after each test to make them independent.
     * @throws IOException if something goes wrong
     */
    private void cleanup() throws IOException {
        File fin = new File("exportedQuestions/");
        for (File file : fin.listFiles()) {
            FileDeleteStrategy.FORCE.delete(file);
        }
        Files.deleteIfExists(Path.of("exportedQuestions/"));
    }

    @BeforeAll
    public static void setUp() {
        Lecture.setCurrentLecture(new Lecture(lectureId, modkey, "Linux","root"));
    }

    @Test
    public void constructorNoFile() throws IOException {
        Path path = Path.of("exportedQuestions/");

        //check if the directory exists
        new CreateFile();
        assertTrue(Files.exists(path));

        //check if the file is present
        Stream<Path> entries = Files.list(path);
        assertTrue(entries.findFirst().isPresent());

        cleanup();
    }

    @Test
    public void constructorNoFileNoLecture() throws IOException {
        Lecture.setCurrentLecture(null);
        Path path = Path.of("exportedQuestions/");

        //check if the directory exists
        new CreateFile();
        assertTrue(Files.exists(path));

        //check if the file is present
        Stream<Path> entries = Files.list(path);
        assertTrue(entries.findFirst().isPresent());

        cleanup();
        Lecture.setCurrentLecture(new Lecture(lectureId, modkey, "Linux","root"));
    }

    @Test
    public void testHeader() throws IOException {
        Path path = Path.of("exportedQuestions/");
        CreateFile createFile = new CreateFile();

        List<Question> qs = List.of(new Question(lectureId, "First", 42),
                new Question(lectureId, "Second", 42));
        createFile.writeToFile(qs);

        Stream<Path> entries = Files.list(path);
        Path p = entries.findFirst().orElse(null);
        if (p == null) {
            fail();
        }

        try (BufferedReader bufferedReader = new BufferedReader(
                    new FileReader(p.toString()))) {
            String line = bufferedReader.readLine();
            assertEquals("Lecture Name: \"" + Lecture.getCurrentLecture().getName() + "\"", line);
            line = bufferedReader.readLine();
            assertEquals("Responsible Lecturer: " + Lecture.getCurrentLecture().getCreatorName(), line);

            //skip some data
            assertNotNull(bufferedReader.readLine());
            assertNotNull(bufferedReader.readLine());
            assertNotNull(bufferedReader.readLine());

            assertEquals("Number of questions: 2", bufferedReader.readLine());
            assertNotNull(bufferedReader.readLine());

            String expected = "Q: \"" + qs.get(0).getText() + "\" asked on " + qs.get(0).getTime();
            assertEquals(expected, bufferedReader.readLine());
            expected = "A: -> No answer available";
            assertEquals(expected, bufferedReader.readLine());
        } catch (IOException e) {
            fail();
        }

        cleanup();
    }

    @Test
    public void testNoAnswer() {

    }

    @Test
    public void testWithAnswer() {

    }

    @Test
    public void testMultipleAnswer() {

    }

}
