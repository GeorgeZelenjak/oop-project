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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class CreateFileTest {
    private static final UUID lectureId = UUID.randomUUID();
    private static final UUID modkey = UUID.randomUUID();
    private static Question q1;
    private static Question q2;
    private static final String pathName = "questions/";
    private static CreateFile createFile;

    /**
     * A helper method to delete files and directory after each test to make them independent.
     * @throws IOException if something goes wrong
     */
    private static void cleanup() throws IOException {
        File fin = new File(pathName);
        File[] files = fin.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            FileDeleteStrategy.FORCE.delete(file);
        }
        Files.deleteIfExists(Path.of(pathName));
    }

    /**
     * Setup for the tests.
     */
    @BeforeAll
    public static void setUp() throws IOException {
        cleanup();
        createFile = new CreateFile();
        q1 = new Question(lectureId, "First", 42);
        q2 = new Question(lectureId, "Second", 69);
        Lecture.setCurrent(new Lecture(lectureId, modkey, "Linux","root"));
    }

    @Test
    public void invalidPathExceptionTest() {
        assertFalse(createFile.setPath("////\\\\\\ asdfs"));
    }

    @Test
    public void createFileNoFileTest() throws IOException {
        Path path = Path.of(pathName);
        assertTrue(createFile.setPath(pathName));
        assertTrue(createFile.createFile());

        //check if the directory exists
        assertTrue(Files.exists(path));

        //check if the file is present
        Stream<Path> entries = Files.list(path);
        assertTrue(entries.findFirst().isPresent());

        cleanup();
    }

    @Test
    public void createFileNoFileNoLectureTest() throws IOException {
        Lecture.setCurrent(null);
        Path path = Path.of(pathName);
        createFile.setPath(pathName);

        assertTrue(createFile.createFile());

        //check if the directory exists
        assertTrue(Files.exists(path));

        //check if the file is present
        Stream<Path> entries = Files.list(path);
        assertTrue(entries.findFirst().isPresent());

        cleanup();
        Lecture.setCurrent(new Lecture(lectureId, modkey, "Linux","root"));
    }

    @Test
    public void createFileDirectoryExistsTest() throws IOException {
        Path path = Path.of(pathName);
        Files.createDirectory(path);
        createFile.setPath(pathName);

        assertTrue(createFile.createFile());

        //check if the directory exists
        assertTrue(Files.exists(path));

        //check if the file is present
        Stream<Path> entries = Files.list(path);
        assertTrue(entries.findFirst().isPresent());

        cleanup();
    }

    @Test
    public void createFileNoParentDirectoryTest() throws IOException {
        createFile.setPath("blob/" + pathName);
        assertFalse(createFile.createFile());

        cleanup();
        createFile.setPath(pathName);
    }

    @Test
    public void createFileFileExistsTest() throws IOException {
        createFile.setPath(pathName);
        createFile.createFile();
        assertTrue(createFile.createFile());

        cleanup();
    }

    @Test
    public void createFileExceptionTest() throws IOException {
        createFile.setPath("");
        Lecture.setCurrent(new Lecture(UUID.randomUUID(), UUID.randomUUID(), "ёмаё.pdf", "папа"));
        assertFalse(createFile.createFile());

        cleanup();
        createFile.setPath(pathName);
        Lecture.setCurrent(null);
    }

    @Test
    public void writeToFileNoFileTest() throws IOException {
        createFile.setPath(pathName);
        createFile.createFile();

        cleanup();
        assertFalse(createFile.writeToFile(new ArrayList<>()));
    }

    @Test
    public void writeToFileNullFileTest() {
        CreateFile cf = new CreateFile();
        assertFalse(cf.writeToFile(new ArrayList<>()));
    }

    @Test
    public void writeToFileHeaderTest() throws IOException {
        Path path = Path.of(pathName);
        createFile.setPath(pathName);
        createFile.createFile();

        createFile.writeToFile(new ArrayList<>());

        Stream<Path> entries = Files.list(path);
        Path p = entries.findFirst().orElse(null);
        if (p == null) {
            fail();
        }

        try (BufferedReader bufferedReader = new BufferedReader(
                            new FileReader(p.toString()))) {
            assertEquals("Lecture Name: \"" + Lecture.getCurrent().getName() + "\"",
                    bufferedReader.readLine());
            assertEquals("Responsible Lecturer: " + Lecture.getCurrent().getCreatorName(),
                    bufferedReader.readLine());

            //skip some data
            assertNotNull(bufferedReader.readLine());
            assertNotNull(bufferedReader.readLine());
            assertNotNull(bufferedReader.readLine());

            assertEquals("Number of questions: 0", bufferedReader.readLine());
            assertNotNull(bufferedReader.readLine());
        } catch (IOException e) {
            fail();
        }

        cleanup();
    }

    @Test
    public void answerNullTest() throws IOException {
        Path path = Path.of(pathName);
        createFile.setPath(pathName);
        createFile.createFile();

        List<Question> qs = List.of(q1);
        createFile.writeToFile(qs);

        Stream<Path> entries = Files.list(path);
        Path p = entries.findFirst().orElse(null);
        if (p == null) {
            fail();
        }

        try (BufferedReader bufferedReader = new BufferedReader(
                        new FileReader(p.toString()))) {
            //skip some data
            for (int i = 0; i < 5; i++) {
                bufferedReader.readLine();
            }

            assertEquals("Number of questions: 1", bufferedReader.readLine());
            bufferedReader.readLine();

            String expected = "Q: \"" + q1.getText() + "\" asked on " + q1.getTime();
            assertEquals(expected, bufferedReader.readLine());
            expected = "A: -> No answer available";
            assertEquals(expected, bufferedReader.readLine());
        } catch (IOException e) {
            fail();
        }

        cleanup();
    }

    @Test
    public void answerEmptyStringTest() throws IOException {
        createFile.setPath(pathName);
        createFile.createFile();

        q1.setAnswerText(" ");
        List<Question> qs = List.of(q1);
        createFile.writeToFile(qs);

        Path path = Path.of(pathName);
        Stream<Path> entries = Files.list(path);
        Path p = entries.findFirst().orElse(null);
        if (p == null) {
            fail();
        }

        try (BufferedReader bufferedReader = new BufferedReader(
                new FileReader(p.toString()))) {
            //skip some data
            for (int i = 0; i < 5; i++) {
                bufferedReader.readLine();
            }

            assertEquals("Number of questions: 1", bufferedReader.readLine());
            bufferedReader.readLine();

            String expected = "Q: \"" + q1.getText() + "\" asked on " + q1.getTime();
            assertEquals(expected, bufferedReader.readLine());
            expected = "A: -> No answer available";
            assertEquals(expected, bufferedReader.readLine());
        } catch (IOException e) {
            fail();
        }

        q1.setAnswerText(null);
        cleanup();
    }

    @Test
    public void withAnswerStringTest() throws IOException {
        final Path path = Path.of(pathName);
        createFile.setPath(pathName);
        createFile.createFile();

        Timestamp time = new Timestamp(System.currentTimeMillis());
        q2.setAnswered(true);
        q2.setAnswerText("42");
        q2.setAnswerTime(time);

        List<Question> qs = List.of(q2);
        createFile.writeToFile(qs);

        Stream<Path> entries = Files.list(path);
        Path p = entries.findFirst().orElse(null);
        if (p == null) {
            fail();
        }

        try (BufferedReader bufferedReader = new BufferedReader(
                new FileReader(p.toString()))) {
            //skip some data
            for (int i = 0; i < 5; i++) {
                bufferedReader.readLine();
            }

            assertEquals("Number of questions: 1", bufferedReader.readLine());
            bufferedReader.readLine();

            String expected = "Q: \"" + q2.getText() + "\" asked on " + q2.getTime();
            assertEquals(expected, bufferedReader.readLine());
            expected = "A: -> \"" + q2.getAnswerText() + "\" answered on "
                    + q2.getAnswerTime();
            assertEquals(expected, bufferedReader.readLine());
        } catch (IOException e) {
            fail();
        }

        cleanup();
        q2 = new Question(lectureId, "Second", 69);
    }

    @Test
    public void twoQuestionsFirstAnsweredTest() throws IOException {
        createFile.setPath(pathName);
        createFile.createFile();

        q1.setAnswered(true);
        List<Question> qs = List.of(q2, q1);
        createFile.writeToFile(qs);

        Stream<Path> entries = Files.list(Path.of(pathName));
        Path p = entries.findFirst().orElse(null);
        if (p == null) {
            fail();
        }

        try (BufferedReader bufferedReader = new BufferedReader(
                new FileReader(p.toString()))) {
            //skip some data
            for (int i = 0; i < 5; i++) {
                bufferedReader.readLine();
            }

            assertEquals("Number of questions: 2", bufferedReader.readLine());
            bufferedReader.readLine();

            String expected = "Q: \"" + q1.getText() + "\" asked on " + q1.getTime();
            assertEquals(expected, bufferedReader.readLine());
            bufferedReader.readLine(); //skip the answer
            assertEquals("--------------------------------------", bufferedReader.readLine());

            expected = "Q: \"" + q2.getText() + "\" asked on " + q2.getTime();
            assertEquals(expected, bufferedReader.readLine());
        } catch (IOException e) {
            fail();
        }

        q1.setAnswered(false);
        cleanup();
    }

    @Test
    public void twoQuestionsSecondAnsweredTest() throws IOException {
        createFile.setPath(pathName);
        createFile.createFile();

        q2.setAnswered(true);
        List<Question> qs = Arrays.asList(q2, q1);
        createFile.writeToFile(qs);

        Stream<Path> entries = Files.list(Path.of(pathName));
        Path p = entries.findFirst().orElse(null);
        if (p == null) {
            fail();
        }

        try (BufferedReader bufferedReader = new BufferedReader(
                new FileReader(p.toString()))) {
            //skip some data
            for (int i = 0; i < 5; i++) {
                bufferedReader.readLine();
            }

            assertEquals("Number of questions: 2", bufferedReader.readLine());
            bufferedReader.readLine();

            String expected = "Q: \"" + q2.getText() + "\" asked on " + q2.getTime();
            assertEquals(expected, bufferedReader.readLine());
            bufferedReader.readLine(); //skip the answer
            assertEquals("--------------------------------------", bufferedReader.readLine());

            expected = "Q: \"" + q1.getText() + "\" asked on " + q1.getTime();
            assertEquals(expected, bufferedReader.readLine());
        } catch (IOException e) {
            fail();
        }

        cleanup();
        q2.setAnswered(false);
    }

    @Test
    public void twoQuestionsBothNotAnsweredOneMoreVotesTest() throws IOException {
        createFile.setPath(pathName);
        createFile.createFile();

        q1.setVotes(42);
        List<Question> qs = List.of(q1, q2);
        createFile.writeToFile(qs);

        Path path = Path.of(pathName);
        Stream<Path> entries = Files.list(path);
        Path p = entries.findFirst().orElse(null);
        if (p == null) {
            fail();
        }

        try (BufferedReader bufferedReader = new BufferedReader(
                new FileReader(p.toString()))) {
            //skip some data
            for (int i = 0; i < 5; i++) {
                bufferedReader.readLine();
            }

            assertEquals("Number of questions: 2", bufferedReader.readLine());
            bufferedReader.readLine();

            String expected = "Q: \"" + q1.getText() + "\" asked on " + q1.getTime();
            assertEquals(expected, bufferedReader.readLine());
            bufferedReader.readLine(); //skip the answer
            assertEquals("--------------------------------------", bufferedReader.readLine());

            expected = "Q: \"" + q2.getText() + "\" asked on " + q2.getTime();
            assertEquals(expected, bufferedReader.readLine());
        } catch (IOException e) {
            fail();
        }
        q1.setVotes(0);
        cleanup();
    }

    @Test
    public void twoQuestionsBothAnsweredTest() throws IOException {
        createFile.setPath(pathName);
        createFile.createFile();

        q1.setAnswered(true);
        q2.setAnswered(true);
        List<Question> qs = List.of(q2, q1);
        createFile.writeToFile(qs);

        Path path = Path.of(pathName);
        Stream<Path> entries = Files.list(path);
        Path p = entries.findFirst().orElse(null);
        if (p == null) {
            fail();
        }

        try (BufferedReader bufferedReader = new BufferedReader(
                new FileReader(p.toString()))) {
            //skip some data
            for (int i = 0; i < 5; i++) {
                bufferedReader.readLine();
            }

            assertEquals("Number of questions: 2", bufferedReader.readLine());
            bufferedReader.readLine();

            String expected = "Q: \"" + q2.getText() + "\" asked on " + q2.getTime();
            assertEquals(expected, bufferedReader.readLine());
            bufferedReader.readLine(); //skip the answer
            assertEquals("--------------------------------------", bufferedReader.readLine());

            expected = "Q: \"" + q1.getText() + "\" asked on " + q1.getTime();
            assertEquals(expected, bufferedReader.readLine());
        } catch (IOException e) {
            fail();
        }
        q1.setAnswered(false);
        q2.setAnswered(false);
        cleanup();
    }


}
