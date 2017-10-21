package localsearch;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import static localsearch.GraphicHelper.*;

public class LocalSearch {

    private final static CompressionQuality COMPRESSION_QUALITY = CompressionQuality.HIGH;
    private final static int CIRCLE_PLACEMENT_RETRY_COUNT = 50;
    private static int circleCount;
    private static int width;
    private static int height;
    private static int maxDiameter;
    // array containing information about the rendered circles. Can be used for vector reconstruction of the image.
    private static Circle[] circles;

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
        circleCount = (width * height) / COMPRESSION_QUALITY.getFactor();
        circles = new Circle[circleCount];

        // do the magics
        long startTime = System.currentTimeMillis();
        BufferedImage outputImage = compress(inputImage);
        System.out.println(String.format("Compression time: %d ms, quality: %s",
                (System.currentTimeMillis() - startTime), COMPRESSION_QUALITY.name()));

        // write the magics
        ImageIO.write(outputImage, "jpeg", new File(outputFileName));
    }

    public static BufferedImage compress(BufferedImage inputImage) {
        // create a black copy of the input image
        BufferedImage outputImage = new BufferedImage(width, height, inputImage.getType());

        // initialize the circles
        int circleCount = 0;
        int retries = 0;
        while (circleCount < LocalSearch.circleCount) {
            updateBoundaries(circleCount);
            ThreadLocalRandom random = ThreadLocalRandom.current();
            int centerX = random.nextInt(width);
            int centerY = random.nextInt(height);
            int diameter = random.nextInt(maxDiameter);
            int majorityColour = getDominantColour(inputImage, centerX, centerY, diameter);
            Circle circle = new Circle(centerX, centerY, diameter, majorityColour);
            // TODO what to do when retry count is reached?
            if (calculateFitnessChange(inputImage, outputImage, circle) > 0 ||
                    retries > CIRCLE_PLACEMENT_RETRY_COUNT) {
                if (retries > CIRCLE_PLACEMENT_RETRY_COUNT) {
                    System.out.println("Retry count reached. Circles placed: " + circleCount);
                }
                circles[circleCount++] = drawCircle(inputImage, outputImage, circle);
                retries = 0;
            } else {
                ++retries;
                continue;
            }
        }

        return outputImage;
    }

    /**
     * Used for non-linear distribution of the diameter of the circles. In the early stages of the algorithm, we
     * want to generate large circles, whereas in the later stages, we want only small ones.
     *
     * @param iteration current iteration of the hill climbing algorithm
     */
    private static void updateBoundaries(int iteration) {
        double percentageDone = (iteration / (double) circleCount) * 100;
        int minDimension = Math.min(width, height);
        if (percentageDone < 25) {
            maxDiameter = (int) ((minDimension / (16 * (minDimension / 100))) * COMPRESSION_QUALITY.getDiameterFactor());
        } else if (percentageDone > 75) {
            maxDiameter = (int) ((minDimension / (48 * (minDimension / 100))) * COMPRESSION_QUALITY.getDiameterFactor());
        } else {
            maxDiameter = (int) ((minDimension / (28 * (minDimension / 100))) * COMPRESSION_QUALITY.getDiameterFactor());
        }
    }

    private static Circle drawCircle(BufferedImage inputImage, BufferedImage outputImage, Circle circle) {
        int lowerBoundX = Math.max(0, circle.getX() - circle.getDiameter());
        int upperBoundX = Math.min(width, circle.getX() + circle.getDiameter());
        int lowerBoundY = Math.max(0, circle.getY() - circle.getDiameter());
        int upperBoundY = Math.min(height, circle.getY() + circle.getDiameter());
        for (int i = lowerBoundX; i < upperBoundX; ++i) {
            for (int j = lowerBoundY; j < upperBoundY; ++j) {
                // is the pixel within the circle?
                if (dist(i, j, circle.getX(), circle.getY()) > circle.getDiameter()) {
                    continue;
                }
                // TODO calculate color based on majority in inputImage and current in outputImage
                int newColour = mixColour(outputImage.getRGB(i, j), circle.getColour());
                outputImage.setRGB(i, j, newColour);
            }
        }
        return circle;
    }

    private static long calculateFitnessChange(BufferedImage inputImage, BufferedImage outputImage, Circle circle) {
        long oldFitness = 0;
        long newFitness = 0;
        int lowerBoundX = Math.max(0, circle.getX() - circle.getDiameter());
        int upperBoundX = Math.min(width, circle.getX() + circle.getDiameter());
        int lowerBoundY = Math.max(0, circle.getY() - circle.getDiameter());
        int upperBoundY = Math.min(height, circle.getY() + circle.getDiameter());
        for (int i = lowerBoundX; i < upperBoundX; ++i) {
            for (int j = lowerBoundY; j < upperBoundY; ++j) {
                // TODO scale the difference?
                int inputRGB = inputImage.getRGB(i, j) & 0x00ffffff;
                assert (inputRGB & 0xff000000) >>> 24 == 0xff;
                int outputRGB = outputImage.getRGB(i, j) & 0x00ffffff;
                assert (outputRGB & 0xff000000) >>> 24 == 0xff;
                oldFitness += Math.abs(inputRGB - outputRGB);
                assert oldFitness >= 0;

                int newOutputRGB = mixColour(outputRGB, circle.getColour());
                assert (newOutputRGB & 0xff000000) >>> 24 == 0xff;
                newFitness += Math.abs(inputRGB - newOutputRGB);
                assert newFitness >= 0;
            }
        }
        return newFitness - oldFitness;
    }
}
