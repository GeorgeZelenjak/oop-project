package nl.tudelft.oopp.livechat.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Wrong modkey, don't do this")
public class InvalidModkeyException extends Exception{
}
