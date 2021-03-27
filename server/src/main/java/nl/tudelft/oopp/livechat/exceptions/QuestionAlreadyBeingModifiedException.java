package nl.tudelft.oopp.livechat.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT,
        reason = "Another moderator is already handling this question,"
                + " if you are sure of what you are doing you can continue")
public class QuestionAlreadyBeingModifiedException extends QuestionException{
}
