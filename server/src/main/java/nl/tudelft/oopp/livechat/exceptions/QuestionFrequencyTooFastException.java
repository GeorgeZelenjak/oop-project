package nl.tudelft.oopp.livechat.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.TOO_EARLY,
        reason = "Not enough time has passed between questions")
public class QuestionFrequencyTooFastException extends QuestionException {
    public QuestionFrequencyTooFastException(String message) {
        super(message);
    }
}
