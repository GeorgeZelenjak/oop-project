package nl.tudelft.oopp.livechat.businesslogic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PercentageCalculatorTest {
    @Test
    public void sameSpeedTest() {
        double expected = 4.5;
        double result = PercentageCalculator.determineNewStartCoordinates(2, 7, 5, 5);
        assertEquals(expected, result);
    }

    @Test
    public void differentSpeedTest() {
        double expected = Math.round((3 + 10.0 / 3.0) * 100) / 100;
        double result = PercentageCalculator.determineNewStartCoordinates(3, 8, 3, 6);
        result = Math.round(result * 100) / 100;
        assertEquals(expected, result);
    }
}
