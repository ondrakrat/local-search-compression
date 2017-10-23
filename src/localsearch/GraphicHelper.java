package localsearch;

import de.androidpit.colorthief.ColorThief;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

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
        return (0xff << 24) + (red << 16) + (green << 8) + blue;
    }

    /**
     * Extract the red colour part from given colour.
     *
     * @param colour colour
     * @return value of red part (0-255)
     */
    public static int getRed(int colour) {
        return (colour & 0xff0000) >> 16;
    }

    /**
     * Extract the green colour part from given colour.
     *
     * @param colour colour
     * @return value of green part (0-255)
     */
    public static int getGreen(int colour) {
        return (colour & 0xff00) >> 8;
    }

    /**
     * Extract the blue colour part from given colour.
     *
     * @param colour colour
     * @return value of blue part (0-255)
     */
    public static int getBlue(int colour) {
        return colour & 0xff;
    }

    /**
     * Extract the alpha channel from given colour.
     *
     * @param colour colour
     * @return value of alpha channel (0-255)
     */
    public static int getAlpha(int colour) {
        return (colour & 0xff000000) >>> 24;
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
     * [{@code centerX}, {@code centerY}] and with given {@code diameter}. With this implementation, counts
     * occurrences of the exact pixels.
     *
     * @param image source image
     * @param centerX x coordinate of the center of the circle
     * @param centerY y coordinate of the center of the circle
     * @param diameter diameter of the circle
     * @return most frequent colour in the area of the circle
     */
    public static int getMajorityColour(BufferedImage image, int centerX, int centerY, int diameter) {
        if (diameter == 0) {
            return image.getRGB(centerX, centerY);
        }
        // TODO extract circle iteration into separate function
        Map<Integer, Integer> colourFrequency = new LinkedHashMap<>();
        int lowerBoundX = Math.max(0, centerX - diameter);
        int upperBoundX = Math.min(image.getWidth(), centerX + diameter);
        int lowerBoundY = Math.max(0, centerY - diameter);
        int upperBoundY = Math.min(image.getHeight(), centerY + diameter);
        for (int i = lowerBoundX; i < upperBoundX; ++i) {
            for (int j = lowerBoundY; j < upperBoundY; ++j) {
                if (dist(i, j, centerX, centerY) > diameter) {
                    continue;
                } else {
                    int pixelColour = image.getRGB(i, j);
                    colourFrequency.compute(pixelColour, (colour, frequency) -> frequency == null ? 1 : (frequency + 1));
                }
            }
        }
        return colourFrequency.entrySet().stream()
                .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No colours found in the circle"))
                .getKey();
    }

    /**
     * Get the dominant colour of the section of the image using {@code Color Thief} library. This effectively picks
     * the dominant colour from the (section of the) image. At this moment, selects the most dominant colour from
     * the <strong>circumscribed square</strong> around the circle.
     *
     * @param image source image
     * @param centerX x coordinate of the center of the circle
     * @param centerY y coordinate of the center of the circle
     * @param diameter diameter of the circle
     * @return most dominant colour in the area of the circle
     */
    public static int getDominantColour(BufferedImage image, int centerX, int centerY, int diameter) {
        if (diameter == 0) {
            return image.getRGB(centerX, centerY);
        }
        int width;
        if (centerX + diameter > image.getWidth()) {    // right overflow
            width = diameter + image.getWidth() - centerX;
        } else if (centerX - diameter < 0) {  // left overflow
            width = diameter + centerX;
        } else {
            width = 2 * diameter;
        }
        int height;
        if (centerY - diameter < 0) {   // bottom overflow
            height = diameter + centerY;
        } else if (centerY + diameter > image.getHeight()) {  // top overflow
            height = diameter + image.getHeight() - centerY;
        } else {
            height = 2 * diameter;
        }
        BufferedImage square = image.getSubimage(
                Math.max(0, centerX - diameter),
                Math.max(0, centerY - diameter),
                width,
                height);
        BufferedImage croppedImage = new BufferedImage(
                image.getColorModel(),
                image.getRaster().createCompatibleWritableRaster(width, height),
                image.isAlphaPremultiplied(),
                null);
        square.copyData(croppedImage.getRaster());
        int[] rgb = ColorThief.getColor(croppedImage);
        return (0xff << 24) + (rgb[0] << 16) + (rgb[1] << 8) + rgb[2];
    }
}
