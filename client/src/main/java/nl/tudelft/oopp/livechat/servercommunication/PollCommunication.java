package nl.tudelft.oopp.livechat.servercommunication;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import nl.tudelft.oopp.livechat.businesslogic.CommonCommunication;
import nl.tudelft.oopp.livechat.data.Poll;
import nl.tudelft.oopp.livechat.data.PollAndOptions;
import nl.tudelft.oopp.livechat.data.PollOption;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

import static nl.tudelft.oopp.livechat.businesslogic.CommonCommunication.handleResponse;

/**
 * The type Poll communication.
 */
public class PollCommunication {
    private static final HttpClient client = HttpClient.newBuilder().build();

    private static final Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss Z").create();

    private static final String ADDRESS = CommonCommunication.ADDRESS;


    /**
     * Creates a poll on the server.
     *
     * @param lectureId         the lecture id
     * @param modkey       the modkey
     * @param questionText the question text
     * @return the votes on lecture speed
     */
    public static Poll createPoll(UUID lectureId, UUID modkey, String questionText) {

        //Parameters for request
        String address = ADDRESS + "/api/poll/create/";
        if (questionText == null || questionText.equals("")) {
            return  null;
        }
        HttpRequest.BodyPublisher req =  HttpRequest.BodyPublishers.ofString(questionText);
        //Creating request and defining response
        HttpRequest request = HttpRequest.newBuilder().PUT(req).uri(
                URI.create(address + lectureId + "/" + modkey)).build();

        HttpResponse<String> response;
        //Catching error when communicating with server
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.out.println("An exception when trying to communicate with the server!");
            //e.printStackTrace();
            return null;
        }
        if (handleResponse(response) != 0) {
            return null;
        }
        return gson.fromJson(response.body(), Poll.class);
    }


    /**
     * Add option poll option.
     *
     * @param pollid     the pollid
     * @param modkey     the modkey
     * @param isCorrect  the is correct
     * @param optionText the option text
     * @return the poll option
     */
    public static PollOption addOption(
            long pollid, UUID modkey, boolean isCorrect, String optionText) {
        //Check if current lecture has been set

        //Parameters for request
        String address = ADDRESS + "/api/poll/addOption/";
        HttpRequest.BodyPublisher req =  HttpRequest.BodyPublishers.ofString(optionText);

        //Creating request and defining response
        HttpRequest request = HttpRequest.newBuilder().PUT(req).uri(
                URI.create(address + pollid + "/" + modkey + "/" + isCorrect)).build();

        HttpResponse<String> response;
        //Catching error when communicating with server
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.out.println("An exception when trying to communicate with the server!");
            //e.printStackTrace();
            return null;
        }
        if (handleResponse(response) != 0) {
            return null;
        }
        return gson.fromJson(response.body(), PollOption.class);
    }


    /**
     * Toggle int.
     *
     * @param pollId the poll id
     * @param modkey the modkey
     * @return the int
     */
    public static int toggle(long pollId, UUID modkey) {
        //Check if current lecture has been set

        //Parameters for request
        String address = ADDRESS + "/api/poll/toggle/";
        HttpRequest.BodyPublisher req =  HttpRequest.BodyPublishers.ofString("");

        //Creating request and defining response
        HttpRequest request = HttpRequest.newBuilder().PUT(req).uri(
                URI.create(address + pollId + "/" + modkey)).build();

        HttpResponse<String> response;
        //Catching error when communicating with server
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.out.println("An exception when trying to communicate with the server!");
            //e.printStackTrace();
            return -1;
        }
        return handleResponse(response);
    }

    /**
     * Fetch poll and options poll and options.
     *
     * @param lectureId the lecture id
     * @return the poll and options
     */
    public static PollAndOptions fetchPollAndOptionsStudent(UUID lectureId) {
        //Check if current lecture has been set

        //Parameters for request
        String address = ADDRESS + "/api/poll/fetchStudent/";

        //Creating request and defining response
        HttpRequest request = HttpRequest.newBuilder().GET().uri(
                URI.create(address + lectureId)).build();

        HttpResponse<String> response;
        //Catching error when communicating with server
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.out.println("An exception when trying to communicate with the server!");
            //e.printStackTrace();
            return null;
        }

        if (CommonCommunication.handleResponseNoAlerts(response) != 0) {
            return null;
        }
        return gson.fromJson(response.body(), PollAndOptions.class);
    }

    /**
     * Fetch poll and options poll and options.
     *
     * @param lectureId   the lecture id
     * @param modkey the modkey
     * @return the poll and options
     */
    public static PollAndOptions fetchPollAndOptionsModerator(UUID lectureId, UUID modkey) {
        //Check if current lecture has been set

        //Parameters for request
        String address = ADDRESS + "/api/poll/fetchMod/";

        //Creating request and defining response
        HttpRequest request = HttpRequest.newBuilder().GET().uri(
                URI.create(address + lectureId + "/" + modkey)).build();

        HttpResponse<String> response;
        //Catching error when communicating with server
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.out.println("An exception when trying to communicate with the server!");
            //e.printStackTrace();
            return null;
        }

        if (CommonCommunication.handleResponseNoAlerts(response) != 0) {
            return null;
        }
        return gson.fromJson(response.body(), PollAndOptions.class);
    }

    /**
     * Vote int.
     *
     * @param userid       the userid
     * @param pollOptionId the poll option id
     * @return the int
     */
    public static int vote(long userid, long pollOptionId) {
        //Check if current lecture has been set

        //Parameters for request
        String address = ADDRESS + "/api/poll/vote/";
        HttpRequest.BodyPublisher req =  HttpRequest.BodyPublishers.ofString("");

        //Creating request and defining response
        HttpRequest request = HttpRequest.newBuilder().PUT(req).uri(
                URI.create(address + userid + "/" + pollOptionId)).build();

        HttpResponse<String> response;
        //Catching error when communicating with server
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.out.println("An exception when trying to communicate with the server!");
            //e.printStackTrace();
            return -1;
        }

        return handleResponse(response);
    }

    /**
     * Reset votes int.
     *
     * @param pollId the poll id
     * @param modkey the modkey
     * @return the int
     */
    public static int resetVotes(long pollId, UUID modkey) {
        //Check if current lecture has been set

        //Parameters for request
        String address = ADDRESS + "/api/poll/reset/";

        //Creating request and defining response
        HttpRequest request = HttpRequest.newBuilder().DELETE().uri(
                URI.create(address + pollId + "/" + modkey)).build();

        HttpResponse<String> response;
        //Catching error when communicating with server
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.out.println("An exception when trying to communicate with the server!");
            //e.printStackTrace();
            return -1;
        }
        return handleResponse(response);
    }
}
