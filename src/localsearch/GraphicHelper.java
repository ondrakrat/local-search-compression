package localsearch;

import java.awt.image.BufferedImage;

/**
 * Class including static helper methods for graphical operations.
 *
 * @author Ondřej Kratochvíl
 */
public final class GraphicHelper {

    /**
     * Perform additive mixing of given RGB colours.
     *
     * @param colour1 first colour
     * @param colour2 second colour
     * @return mixed colour
     */
    public static int mixColour(int colour1, int colour2) {
        int red = (getRed(colour1) + getRed(colour2)) / 2;
        int green = (getGreen(colour1) + getGreen(colour2)) / 2;
        int blue = (getBlue(colour1) + getBlue(colour2)) / 2;
        return ((((red << 8) + green) << 8) + blue) << 8;
    }

    /**
     * Extract the red colour part from given colour.
     *
     * @param colour colour
     * @return value of red part (0-255)
     */
    public static int getRed(int colour) {
        return (colour & 0xFF000000) >> 24;
    }

    /**
     * Extract the green colour part from given colour.
     *
     * @param colour colour
     * @return value of green part (0-255)
     */
    public static int getGreen(int colour) {
        return (colour & 0x00FF0000) >> 16;
    }

    /**
     * Extract the blue colour part from given colour.
     *
     * @param colour colour
     * @return value of blue part (0-255)
     */
    public static int getBlue(int colour) {
        return (colour & 0x0000FF00) >> 8;
    }

    /**
     * Calculate the distance between two points.
     *
     * @param x1 x coordinate of first point
     * @param y1 y coordinate of first point
     * @param x2 x coordinate of second point
     * @param y2 y coordinate of second point
     * @return distance between the two points
     */
    public static double dist(int x1, int y1, int x2, int y2) {
        return Math.abs(Math.sqrt(((x1 - x2) * (x1 - x2)) + ((y1 - y2) * (y1 - y2))));
    }

    /**
     * Get the most frequent colour in given {@link BufferedImage} in the circle with center in coordinates
     * [{@code centerX}, {@code centerY}] and with given {@code diameter}.
     *
     * @param image source image
     * @param centerX x coordinate of the center of the circle
     * @param centerY y coordinate of the center of the circle
     * @param diameter diameter of the circle
     * @return most frequent colour in the area of the circle
     */
    public static int getMajorityColour(BufferedImage image, int centerX, int centerY, int diameter) {
        // TODO find majority colour in inputImage instead of colour of the center
        return image.getRGB(centerX, centerY);
    }
}
