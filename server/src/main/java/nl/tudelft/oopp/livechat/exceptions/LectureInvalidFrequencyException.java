package nl.tudelft.oopp.livechat.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST,
        reason = "The frequency must be between 0 and 300 seconds")
public class LectureInvalidFrequencyException extends LectureException {
}
