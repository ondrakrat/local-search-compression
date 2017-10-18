package localsearch;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

public class LocalSearch {

    private final static int CIRCLE_COUNT = 100;
    private static int width;
    private static int height;
    private static int maxDiameter; // TODO is int ok?

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("Please specify input and output file names");
            System.exit(1);
        }
        // read the image
        String inputFileName = args[0];
        String outputFileName = args[1];
        BufferedImage inputImage = ImageIO.read(new File(inputFileName));
        width = inputImage.getWidth();
        height = inputImage.getHeight();
        maxDiameter = Math.min(width, height) / 2;

        // do the magics
        BufferedImage outputImage = compress(inputImage);

        // write the magics
        ImageIO.write(outputImage, "bmp", new File(outputFileName));
    }

    public static BufferedImage compress(BufferedImage inputImage) {
        // create a black copy of the input image
        BufferedImage outputImage = new BufferedImage(width, height, inputImage.getType());

        // initialize the circles
        for (int i = 0; i < CIRCLE_COUNT; ++i) {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            int centerX = random.nextInt(width);
            int centerY = random.nextInt(height);
            int diameter = random.nextInt(maxDiameter);
            drawCircle(inputImage, outputImage, centerX, centerY, diameter);
        }

        return outputImage;
    }

    public static double dist(int x1, int y1, int x2, int y2) {
        return Math.abs(Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2)));
    }

    public static int getMajorityColour(BufferedImage image, int centerX, int centerY, int diameter) {
        // TODO find majority colour in inputImage, now just returns random colour
        return (int) (Math.random() * 0x1000000);
    }

    private static void drawCircle(BufferedImage inputImage, BufferedImage outputImage,
                                   int centerX, int centerY, int diameter) {
        int majorityColour = getMajorityColour(inputImage, centerX, centerY, diameter);
        // iterate only in in circumscribed square
        int lowerBoundX = Math.max(0, centerX - diameter);
        int upperBoundX = Math.min(width, centerX + diameter);
        int lowerBoundY = Math.max(0, centerY - diameter);
        int upperBoundY = Math.min(height, centerY + diameter);
        for (int i = lowerBoundX; i < upperBoundX; ++i) {
            for (int j = lowerBoundY; j < upperBoundY; ++j) {
                // is the pixel within the circle?
                if (dist(i, j, centerX, centerY) > diameter) {
                    continue;
                }
                // TODO calculate color based on majority in inputImage and current in outputImage
                outputImage.setRGB(i, j, majorityColour);
            }
        }
    }
}
