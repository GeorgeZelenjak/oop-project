package nl.tudelft.oopp.livechat.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "The answer text is too long")
public class QuestionNotAnsweredException extends QuestionException {
}
