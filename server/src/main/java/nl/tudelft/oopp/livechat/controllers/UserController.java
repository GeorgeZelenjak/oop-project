package nl.tudelft.oopp.livechat.controllers;

import nl.tudelft.oopp.livechat.entities.UserEntity;
import nl.tudelft.oopp.livechat.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    /**
     * Creates a new user controller.
     * @param userService user service object
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * POST endpoint to create a new user in the database.
     * @param user the new user
     * @return 0 if successful, -1 if not
     */
    @PostMapping("/register")
    public int newUser(@RequestBody UserEntity user) {
        String remoteAddress = ((ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes())
                .getRequest().getRemoteAddr();
        return userService.newUser(user, remoteAddress);
    }

    /**
     * Exception handler.
     * @param exception exception that has occurred
     * @return response body with 400 and 'Invalid UUID' message
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private ResponseEntity<Object> badUUID(HttpMessageNotReadableException exception) {
        System.out.println(exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Don't do this");
    }

}
