package nl.tudelft.oopp.livechat.servercommunication;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import nl.tudelft.oopp.livechat.data.Lecture;
import nl.tudelft.oopp.livechat.data.Question;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

public class QuestionCommunication {

    //Client object for sending requests
    private static final HttpClient client = HttpClient.newBuilder().build();
    /* Gson object for parsing Json
    set to parse fields according to annotations
    and with specified date format
     */
    private static final Gson gson = new GsonBuilder().setDateFormat(
            "EEE, dd MMM yyyy HH:mm:ss zzz").excludeFieldsWithoutExposeAnnotation().create();

    /**
     * Sends an HTTP request to ask
     * a question with the current LectureID.
     *
     * @param questionText the question text
     * @return integer representing servers response
     *      or the lack of it
     *       1 - Question has been successfully asked
     *      -1 - Current lecture not set
     *      -2 - No response from server
     *      -3 - Unexpected response from server
     */
    public static int askQuestion(String questionText) {

        //Checking if current lecture has been set
        if (Lecture.getCurrentLecture() == null) {
            System.out.println("You are not connected to a lecture!");
            return -1;
        }

        //Parameters for question
        UUID lectureId = Lecture.getCurrentLecture().getUuid();
        Question question = new Question(lectureId,questionText,  42);

        //Parameters for request
        String json = gson.toJson(question);
        HttpRequest.BodyPublisher req =  HttpRequest.BodyPublishers.ofString(json);
        String address = "http://localhost:8080/api/question/ask";
        String headerName = "Content-Type";
        String headerValue = "application/json";

        //Creating request and defining response
        HttpRequest request = HttpRequest.newBuilder().POST(req).uri(
                URI.create(address)).setHeader(headerName, headerValue).build();
        HttpResponse<String> response;

        //Catching error when communicating with server
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.out.println("There was an exception!");
            e.printStackTrace();
            return -2;
        }

        //Unexpected response
        if (response.statusCode() != 200) {
            System.out.println("Status: " + response.statusCode());
            return -3;
        }

        //Question has been asked successfully
        System.out.println("The question was asked successfully! " + response.body());
        return 1;
    }

    /**
     * Fetch questions list.
     *
     * @return the list of questions related to current lecture,
     *       null if error occurs or no current lecture is set
     */
    public static List<Question> fetchQuestions() {

        //Checking if current lecture has been set
        if (Lecture.getCurrentLecture() == null) {
            System.out.println("You are not connected to a lecture!");
            return null;
        }

        //Parameters for request
        String lectureId = URLEncoder.encode(
                Lecture.getCurrentLecture().getUuid().toString(), StandardCharsets.UTF_8);
        String address = "http://localhost:8080/api/question/fetch?lid=";

        //Creating request and defining response
        HttpRequest request = HttpRequest.newBuilder().GET().uri(
                URI.create(address + lectureId)).build();
        HttpResponse<String> response;

        //Catching error when communicating with server
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.out.println("There was an exception!");
            e.printStackTrace();
            return null;
        }
        if (response.statusCode() != 200) {
            System.out.println("Status: " + response.statusCode());
            return null;
        }

        //Printing the response body for testing
        System.out.println("The questions were retrieved successfully! " + response.body());

        //Return object from response
        Type listType = new TypeToken<List<Question>>(){}.getType();
        return gson.fromJson(response.body(), listType);


    }

    /** Method that sends a request to upvote a question to the server.
     * @param qid - the Question ID
     * @param uid - the User ID
     * @return - the "status code"
     *           1 if success
     *          -1 if current lecture does not exist
     *          -2 if an exception occurred when communicating with the server
     *          -3 if wasn't upvoted/downvoted (e.g wrong uid, wrong qid etc.)
     */
    public static int upvoteQuestion(long qid, long uid) {

        //Checking if current lecture has been set
        if (Lecture.getCurrentLecture() == null) {
            System.out.println("You are not connected to a lecture!");
            return -1;
        }
        //Parameters for request

        HttpRequest.BodyPublisher req =  HttpRequest.BodyPublishers.ofString("");
        String address = "http://localhost:8080/api/question/upvote";

        //Creating request and defining response
        HttpRequest request = HttpRequest.newBuilder().PUT(req).uri(
                URI.create(address + "?qid=" + qid + "&uid=" + uid)).build();

        HttpResponse<String> response;

        //Catching error when communicating with server
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.out.println("There was an exception!");
            e.printStackTrace();
            return -2;
        }

        //Unexpected response
        if (response.statusCode() != 200) {
            System.out.println("Status: " + response.statusCode());
            return -3;
        }

        //Question has been asked successfully
        System.out.println("The question was upvoted/downvoted successfully! " + response.body());
        return 1;
    }

    /** Method that sends a request to mark as answered a question to the server.
     * @param qid - the Question ID
     * @param modkey - the User ID
     * @return - the status code
     */
    public static int markedAsAnswered(long qid, UUID modkey) {

        //Checking if current lecture has been set
        if (Lecture.getCurrentLecture() == null) {
            System.out.println("You are not connected to a lecture!");
        }

        //Parameters for request
        HttpRequest.BodyPublisher req =  HttpRequest.BodyPublishers.ofString("placeholder");
        String address = "http://localhost:8080/api/question/answer/" + qid + "/" + modkey;
        String headerName = "Content-Type";
        String headerValue = "application/json";

        //Creating request and defining response
        HttpRequest request = HttpRequest.newBuilder().PUT(req)
                .uri(URI.create(address + "?qid=" + qid + "&uid=" + modkey))
                .setHeader(headerName, headerValue).build();

        HttpResponse<String> response;

        //Catching error when communicating with server
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.out.println("There was an exception!");
            e.printStackTrace();
            return -2;
        }

        //Unexpected response
        if (response.statusCode() != 200) {
            System.out.println("Status: " + response.statusCode());
            return -3;
        }

        if (!response.body().equals("0")) {
            return -4;
        }

        //Question has been marked as answered successfully
        System.out.println("The question was marked as answered successfully!" + response.body());
        return 0;
    }
}






