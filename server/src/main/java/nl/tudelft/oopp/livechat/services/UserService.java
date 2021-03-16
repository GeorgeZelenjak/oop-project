package nl.tudelft.oopp.livechat.services;

import nl.tudelft.oopp.livechat.entities.UserEntity;
import nl.tudelft.oopp.livechat.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    /**
     * New user.
     *
     * @param user the user
     * @return 0 if successful,
     *          -1 if uid is invalid
     */
    public int newUser(UserEntity user, String ip) {
        user.setIp(ip);
        if (!luhnCheck(user.getUid())) { // use mac address checksum
            return -1;
        }
        userRepository.save(user);
        return 0;
    }

    /**
     * Luhn check for the given number.
     *
     * @param n the number
     * @return true if checksum is valid, false otherwise
     */
    public static boolean luhnCheck(long n) {
        String number = Long.toString(n);
        long temp = 0;
        for (int i = 0;i < number.length();i++) {
            int digit;
            if (i % 2 == 1) {
                digit = Character.getNumericValue(number.charAt(i)) * 2;
                digit %= 9;
                if (digit == 0) digit = 9;
            } else {
                digit = Character.getNumericValue(number.charAt(i));
            }
            temp += digit;
        }
        return temp % 10 == 0;
    }

}
