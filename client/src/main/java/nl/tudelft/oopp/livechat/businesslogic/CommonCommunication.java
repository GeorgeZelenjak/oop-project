package nl.tudelft.oopp.livechat.businesslogic;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.tudelft.oopp.livechat.controllers.AlertController;

import java.net.http.HttpResponse;

public abstract class CommonCommunication {

    public static final String ADDRESS = "http://localhost:8080";

    /**
     * Handle response given response codes and error messages by server.
     *
     * @param response the response
     * @return 0 if 200 OK, alert and -1 otherwise
     */
    public static int handleResponse(HttpResponse<String> response) {
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
     * Handle response given response codes and error messages by server
     * does not set an alert.
     *
     * @param response the response
     * @return 0 if 200 OK, alert and -1 otherwise
     */
    public static int handleResponseNoAlerts(HttpResponse<String> response) {
        if (response.statusCode() != 200) {
            if (response.body().contains("{")) {
                JsonObject res = JsonParser.parseString(response.body()).getAsJsonObject();
                String reason = res.get("message").getAsString();
                String error = res.get("error").getAsString();
            }
            return -1;
        }
        return 0;
    }

}
