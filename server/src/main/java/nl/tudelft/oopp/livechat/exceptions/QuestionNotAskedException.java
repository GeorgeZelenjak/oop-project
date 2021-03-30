package nl.tudelft.oopp.livechat.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Question not asked, name too long")
public class QuestionNotAskedException  extends QuestionException {
}
