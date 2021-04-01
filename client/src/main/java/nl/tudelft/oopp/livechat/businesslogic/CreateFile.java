package nl.tudelft.oopp.livechat.businesslogic;

import nl.tudelft.oopp.livechat.data.Lecture;
import nl.tudelft.oopp.livechat.data.Question;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

public class CreateFile {
    private File file;
    private final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    private Path path;

    /**
     * Text HighLighters.
     */
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[38;5;40m";
    private static final String separatorLine = "--------------------------------------";


    /**
     * Creates a new CreateFile object.
     */
    public CreateFile() {
    }

    /**
     * Sets path of the CreateFile object.
     * @param pathName the string representing a path
     * @return true iff successfully set, false otherwise
     */
    public boolean setPath(String pathName) {
        try {
            this.path = Path.of(pathName);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Creates a file in the set path.
     * @return true if successfully created, false otherwise
     */
    public boolean createFile() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
        String timeStamp = sdf.format(timestamp);

        try {
            if (Files.notExists(path)) {
                System.out.println(ANSI_RED + "Directory not found" + ANSI_RESET);
                Files.createDirectory(path);
                System.out.println(ANSI_GREEN + "Directory created" + ANSI_RESET);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }

        String fileName;
        if (Lecture.getCurrent() == null) {
            fileName = "unnamed"  + "_" + timeStamp;
        } else {
            fileName = Lecture.getCurrent().getName() + "_" + timeStamp;
        }

        fileName = this.sanitizeFilename(fileName);

        this.file = new File(path.toString() + "/" + fileName + ".txt");
        return this.createFileHelper();
    }

    /**
     * A helper method for creating file.
     */
    private boolean createFileHelper() {
        try {
            if (file.createNewFile()) {
                System.out.println(ANSI_GREEN + "File created successfully" + ANSI_RESET);
            } else {
                System.out.println(ANSI_RED + "File already exists!" + ANSI_RESET);
            }
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }


    /**
     * Main method of the class that writes all questions to file.
     * @param questions list of questions
     */
    public boolean writeToFile(List<Question> questions) {
        try {
            PrintWriter writer = new PrintWriter(file);

            List<Question> list = questions.stream().sorted((question1, question2) -> {
                if (question1.isAnswered() && !question2.isAnswered())
                    return -1;
                else if (!question1.isAnswered() && question2.isAnswered())
                        return 1;
                else {
                    return Integer.compare(question2.getVotes(), question1.getVotes());
                }
            }).collect(Collectors.toList());

            writer.println(this.headerBuilder(list.size()));

            for (int i = 0; i < list.size(); i++) {

                Question question = list.get(i);

                writer.println(this.stringHelper(question));
                if (i < list.size() - 1)
                writer.println(separatorLine);
            }

            writer.close();
            System.out.println(ANSI_GREEN
                    + "Questions written successfully to file" + ANSI_RESET);
        } catch (FileNotFoundException | NullPointerException e) {
            System.out.println(ANSI_RED + "File not found!" + ANSI_RESET);
            return false;
        }
        return true;
    }

    /**
     * Helper method that builds the String representation of a question.
     * @param question The Question Object
     * @return the string representation
     */
    private String stringHelper(Question question) {
        String result = "Q: \""
                + question.getText() + "\" asked on " + question.getTime();

        String answerText;
        if (question.getAnswerText() != null && !question.getAnswerText().equals(" ")) {
            answerText = "A: -> \"" + question.getAnswerText() + "\" answered on "
                    + question.getAnswerTime();

        } else answerText = "A: -> No answer available";

        result += "\n" + answerText;
        return result;
    }

    /**
     * Helper method that builds the header of the file.
     * @param listSize size of question size
     * @return the string representation
     */
    private String headerBuilder(int listSize) {

        Lecture lecture = Lecture.getCurrent();
        return "Lecture Name: \"" + lecture.getName() + "\""
                + "\nResponsible Lecturer: " + lecture.getCreatorName()
                + "\nCreation Date: " + lecture.getStartTime()
                + "\n\nExported at: " + timestamp
                + "\nNumber of questions: " + listSize
                + "\n";
    }

    /**
     * Method that sanitizes the filename.
     * @param inputName the filename
     * @return the sanitized filename
     */
    private String sanitizeFilename(String inputName) {
        return inputName.replaceAll("[^a-zA-Z0-9-]", "_");
    }

}
