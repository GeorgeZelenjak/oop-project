package nl.tudelft.oopp.livechat.servercommunication;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import nl.tudelft.oopp.livechat.businesslogic.CommonCommunication;
import nl.tudelft.oopp.livechat.data.Lecture;
import nl.tudelft.oopp.livechat.data.Question;
import nl.tudelft.oopp.livechat.data.User;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static nl.tudelft.oopp.livechat.businesslogic.CommonCommunication.handleResponse;
import static nl.tudelft.oopp.livechat.businesslogic.CommonCommunication.handleResponseNoAlerts;

/**
 * Class for Question server communication.
 */
public class QuestionCommunication {

    private QuestionCommunication() {

    }

    //Client object for sending requests
    private static final HttpClient client = HttpClient.newBuilder().build();
    /* Gson object for parsing Json
    set to parse fields according to annotations
    and with specified date format
     */
    private static final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
            .setDateFormat("yyyy-MM-dd HH:mm:ss Z").create();

    private static final String ADDRESS = CommonCommunication.ADDRESS;

    /**
     * Sends an HTTP request to ask a question with the current Lecture id.
     * @param   uid the id of the user
     * @param   lectureId the id of the lecture
     * @param   questionText the text of the question
     * @return  0 if the question was asked successfully
     *         -1 if current lecture does not exist
     *         -2 if an exception occurred when communicating with the server
     *         -3 if unexpected response was received
     *         -4 if server rejects the question
     */
    public static int askQuestion(long uid, UUID lectureId, String questionText) {
        //Check if current lecture has been set
        if (Lecture.getCurrentLecture() == null) {
            System.out.println("You are not connected to a lecture!");
            return -1;
        }

        //Parameters for question
        Question question = new Question(lectureId, questionText, uid);

        //Parameters for request
        String json = gson.toJson(question);
        HttpRequest.BodyPublisher req =  HttpRequest.BodyPublishers.ofString(json);
        String address = ADDRESS + "/api/question/ask";

        //Creating request and defining response
        HttpRequest request = HttpRequest.newBuilder().POST(req).uri(
                URI.create(address)).setHeader("Content-Type", "application/json").build();
        HttpResponse<String> response;

        //Catching error when communicating with server
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.out.println("There was an exception!");
            //e.printStackTrace();
            return -2;
        }

        int result = handleResponse(response);
        if (result != 0) {
            return -1;
        }
        //Question has been asked successfully
        System.out.println("The question was asked successfully! " + response.body());
        User.addQuestionId(Long.parseLong(response.body()));
        return 0;
    }

    /**
     * Fetch questions list.
     * @return the list of questions related to current lecture,
     *       null if error occurs or no current lecture is set
     */
    public static List<Question> fetchQuestions() {
        //Check if current lecture has been set
        if (Lecture.getCurrentLecture() == null) {
            System.out.println("You are not connected to a lecture!");
            return null;
        }

        //Parameters for request
        String lectureId = URLEncoder.encode(
                Lecture.getCurrentLecture().getUuid().toString(), StandardCharsets.UTF_8);
        String address = ADDRESS + "/api/question/fetch?lid=";

        //Creating request and defining response
        HttpRequest request = HttpRequest.newBuilder().GET().uri(
                URI.create(address + lectureId)).build();
        HttpResponse<String> response;

        //Catching error when communicating with server
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.out.println("An exception occurred when trying to communicate with the server!");
            //e.printStackTrace();
            return null;
        }

        int result = handleResponse(response);
        if (result != 0) {
            return null;
        }

        //Printing the response body for testing
        System.out.println("The questions were retrieved successfully! " + response.body());

        //Return object from response
        Type listType = new TypeToken<List<Question>>(){}.getType();
        return gson.fromJson(response.body(), listType);
    }

    /** Method that sends a request to upvote a question to the server.
     * @param qid - the question id
     * @param uid - the user id
     * @return - the "status code"
     *           0 if the question was upvoted/downvoted successfully
     *          -1 if current lecture does not exist
     *          -2 if an exception occurred when communicating with the server
     *          -3 if unexpected response was received
     *          -4 if the question wasn't upvoted/downvoted (e.g wrong uid, wrong qid etc.)
     */
    public static int upvoteQuestion(long qid, long uid) {
        //Check if current lecture has been set
        if (Lecture.getCurrentLecture() == null) {
            System.out.println("You are not connected to a lecture!");
            return -1;
        }

        //Parameters for request
        HttpRequest.BodyPublisher req =  HttpRequest.BodyPublishers.ofString("");
        String address = ADDRESS + "/api/question/upvote";

        //Creating request and defining response
        HttpRequest request = HttpRequest.newBuilder().PUT(req).uri(
                URI.create(address + "?qid=" + qid + "&uid=" + uid)).build();

        HttpResponse<String> response;
        //Catching error when communicating with server
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.out.println("Exception when trying to communicate with the server!");
            //e.printStackTrace();
            return -2;
        }

        int result = handleResponse(response);
        if (result == 0) {
            System.out.println("The question was upvoted/downvoted successfully! "
                    + response.body());
        }
        return result;
    }

    /**
     * Method that sends a request to mark as answered a question to the server.
     * @param qid the id of the question
     * @param modkey the moderator key
     * @return  0 if the question was marked as answered successfully
     *         -1 if current lecture does not exist
     *         -2 if an exception occurred when communicating with the server
     *         -3 if unexpected response was received
     *         -4 if the question wasn't marked as answered (e.g wrong qid, wrong modkey etc.)
     */
    public static int markedAsAnswered(long qid, UUID modkey, String answer) {
        //Check if current lecture has been set
        if (Lecture.getCurrentLecture() == null) {
            System.out.println("You are not connected to a lecture!");
            return -1;
        }

        //Parameters for request
        if (answer == null) {
            answer = " ";
        }
        HttpRequest.BodyPublisher req =  HttpRequest.BodyPublishers.ofString(answer);
        String address = ADDRESS + "/api/question/answer/" + qid + "/" + modkey;

        //Creating request and defining response
        HttpRequest request = HttpRequest.newBuilder().PUT(req)
                .uri(URI.create(address + "?qid=" + qid + "&uid=" + modkey))
                .setHeader("Content-Type", "application/json").build();

        HttpResponse<String> response;

        //Catching error when communicating with server
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.out.println("An exception occurred when trying to communicate with the server!");
            //e.printStackTrace();
            return -2;
        }

        int result = handleResponse(response);
        if (result == 0) {
            System.out.println("The question was marked as answered successfully!"
                    + response.body());
        }
        return result;
    }

    /**
     * Method that sends a request to edit a question to the server.
     * @param qid the id of the question
     * @param modKey the moderator key
     * @param newText the edited text of the question
     * @return 0 if the question was edited successfully
     *        -1 if current lecture does not exist
     *        -2 if an exception occurred when communicating with the server
     *        -3 if unexpected response was received
     *        -4 if the question wasn't edited (e.g wrong qid, wrong modkey etc.)
     */
    public static int edit(long qid, UUID modKey, String newText) {
        //Check if current lecture has been set
        if (Lecture.getCurrentLecture() == null) {
            System.out.println("You are not connected to a lecture!!!");
            return -1;
        }

        //Create a json object with the data to be sent
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", qid);
        jsonObject.addProperty("modkey", modKey.toString());
        jsonObject.addProperty("text", newText);
        jsonObject.addProperty("uid", User.getUid());
        String json = gson.toJson(jsonObject);

        HttpRequest.BodyPublisher req =  HttpRequest.BodyPublishers.ofString(json);
        String address = ADDRESS + "/api/question/edit";

        //Create request and defining response
        HttpRequest request = HttpRequest.newBuilder().PUT(req).uri(
                URI.create(address)).setHeader("Content-Type", "application/json").build();
        HttpResponse<String> response;
        //Catching error when communicating with server
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.out.println("Exception when communicating with the server!");
            //e.printStackTrace();
            return -2;
        }

        int result = handleResponse(response);
        System.out.println("Status: " + result);
        if (result == 0) {
            System.out.println("The question with id " + qid + " was modified successfully!");
            System.out.println("New text: " + newText);
        }
        return result;
    }

    /**
     * Method that sends a request to delete a question (done by the user who asked the question).
     * @param qid the id of the question
     * @param uid the id of the user
     * @return  0 if the question was deleted successfully
     *         -1 if current lecture does not exist
     *         -2 if an exception occurred when communicating with the server
     *         -3 if unexpected response was received
     *         -4 if the question wasn't deleted (e.g wrong uid, wrong qid etc.)
     */
    public static int deleteQuestion(long qid, long uid) {
        //Check if current lecture has been set
        if (Lecture.getCurrentLecture() == null) {
            System.out.println("You are not connected to a lecture!");
            return -1;
        }
        //Parameters for request
        String address = ADDRESS + "/api/question/delete";

        //Creating request and defining response
        HttpRequest request = HttpRequest.newBuilder().DELETE()
                .uri(URI.create(address + "?qid=" + qid + "&uid=" + uid)).build();

        HttpResponse<String> response;
        //Catching error when communicating with server
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.out.println("Exception occurred when communicating with the server!");
            //e.printStackTrace();
            return -2;
        }

        int result = handleResponse(response);
        if (result == 0) {
            System.out.println("The question with id " + qid + " was deleted successfully!");
            User.deleteQuestionId(qid);
        }
        return result;
    }

    /**
     * Method that sends a request to delete a question (done by the moderator).
     * @param qid the id of the question
     * @param modkey the moderator key
     * @return  0 if the question was deleted successfully
     *         -1 if current lecture does not exist
     *         -2 if an exception occurred when communicating with the server
     *         -3 if unexpected response was received
     *         -4 if the question wasn't deleted (e.g wrong qid, wrong modkey etc.)
     */
    //TODO remove qid from user's set of questions after deletion
    public static int modDelete(long qid, UUID modkey) {
        //Check if current lecture has been set
        if (Lecture.getCurrentLecture() == null) {
            System.out.println("You are not connected to a lecture!");
            return -1;
        }
        //Parameters for request
        String address = ADDRESS + "/api/question/moderator/delete";

        //Creating request and defining response
        HttpRequest request = HttpRequest.newBuilder().DELETE()
                .uri(URI.create(address + "?qid=" + qid + "&modkey=" + modkey)).build();

        HttpResponse<String> response;
        //Catching error when communicating with server
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.out.println("An exception occurred when communicating with the server!");
            //e.printStackTrace();
            return -2;
        }

        int result = handleResponse(response);
        if (result == 0) {
            System.out.println("The question with id " + qid + " was deleted successfully!");
        }
        return result;
    }

    /**
     * Sets status.
     *
     * @param qid    the qid
     * @param modkey the modkey
     * @param status the status
     * @param uid    the uid
     * @return the status
     */
    public static int setStatus(long qid, UUID modkey, String status, long uid) {
        //Check if current lecture has been set
        if (Lecture.getCurrentLecture() == null) {
            System.out.println("You are not connected to a lecture!!!");
            return -1;
        }

        //Create a json object with the data to be sent

        HttpRequest.BodyPublisher req =  HttpRequest.BodyPublishers.ofString(status);
        String address = ADDRESS + "/api/question/status/" + qid + "/" + uid + "/"
                + modkey.toString();

        //Create request and defining response
        HttpRequest request = HttpRequest.newBuilder().PUT(req).uri(
                URI.create(address)).setHeader("Content-Type", "application/json").build();
        HttpResponse<String> response;
        //Catching error when communicating with server
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.out.println("Exception when communicating with the server!");
            //e.printStackTrace();
            return -2;
        }

        int result = handleResponseNoAlerts(response);
        System.out.println("Status: " + result);
        if (result == 0) {
            System.out.println("The question with id " + qid + " have changed status!");
            System.out.println("New status: " + status);
        }
        return result;
    }

}






