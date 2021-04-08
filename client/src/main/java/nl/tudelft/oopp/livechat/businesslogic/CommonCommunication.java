package nl.tudelft.oopp.livechat.businesslogic;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.tudelft.oopp.livechat.controllers.gui.AlertController;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public abstract class CommonCommunication {

    /**
     * Client object for sending requests.
     */
    private static final HttpClient client = HttpClient.newBuilder().build();

    /**
     * The address of the server.
     */
    public static final String ADDRESS = "http://localhost:8080";


    private CommonCommunication() {

    }

    /**
     * A method to send an HTTP request to the server and receive the response.
     * @param request HTTP request to the server
     * @return HTTP response if successful, null if not
     */
    public static HttpResponse<String> sendAndReceive(HttpRequest request) {
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            return null;
        }
        return response;
    }

    /**
     * Handles HTTP response received from the server and shows alerts.
     * @param response the HTTP response received from the server
     * @return 0 if 200 OK, -1 otherwise
     */
    public static int handleResponse(HttpResponse<String> response) {
        if (response == null) {
            AlertController.alertError("Error", "Connection error");
            return -1;
        }
        if (response.statusCode() != 200) {
            if (response.body().contains("{")) {
                JsonObject res = JsonParser.parseString(response.body()).getAsJsonObject();
                String reason = res.get("message").getAsString();
                String error = res.get("error").getAsString();
                AlertController.alertError(error, reason);
            } else {
                AlertController.alertError("Error", response.body());
            }
            return -1;
        }
        return 0;
    }

    /**
     * Handles HTTP response received from the server without showing alerts.
     * @param response the HTTP response received from the server
     * @return 0 if 200 OK, -1 otherwise
     */
    public static int handleResponseNoAlerts(HttpResponse<String> response) {
        if (response == null) {
            System.out.println("Something happened");
            return -1;
        }
        if (response.statusCode() != 200) {
            if (response.body().contains("{")) {
                JsonObject res = JsonParser.parseString(response.body()).getAsJsonObject();
                String reason = res.get("message").getAsString();
                String error = res.get("error").getAsString();
                System.out.println(res.get("status").getAsString());
                System.out.println(error + ":" + reason);
            } else {
                System.out.println(response.body());
            }
            return -1;
        }
        return 0;
    }

}
