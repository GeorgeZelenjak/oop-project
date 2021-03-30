package nl.tudelft.oopp.livechat.businesslogic;

public abstract class PercentageCalculator {

    /**
     * Determines the new start coordinate for the line
     * depending on the number of faster and slower count.
     *
     * @param minX   the min coordinate
     * @param maxX   the max coordinate
     * @param faster the faster count
     * @param slower the slower count
     * @return where the line should start
     */
    public static double determineNewStartCoordinates(double minX, double maxX,
                                                             int faster, int slower) {
        //Correction, so we do not delete by 0
        double fraction;
        if (faster == slower) {
            fraction = 0.5;
        } else {
            //Calculate the percentage
            double total = slower + faster;
            fraction = slower / total;
        }
        //Calculate the final coordinate
        return minX + (maxX - minX) * fraction;
    }

    /**
     * Calculate percentage double.
     *
     * @param whole    the whole
     * @param fraction the fraction
     * @return the double
     */
    public static double calculatePercentage(int whole, int fraction) {
        if (whole == 0) {
            return 0;
        }
        return  (double) fraction / whole;
    }
}
