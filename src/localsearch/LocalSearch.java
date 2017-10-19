package localsearch;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import static localsearch.GraphicHelper.dist;
import static localsearch.GraphicHelper.getMajorityColour;
import static localsearch.GraphicHelper.mixColour;

public class LocalSearch {

    private final static int CIRCLE_COUNT = 13000;
    private static int width;
    private static int height;
    private static int maxDiameter;
    private static Circle[] circles = new Circle[CIRCLE_COUNT];

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

        // do the magics
        BufferedImage outputImage = compress(inputImage);

        // write the magics
        ImageIO.write(outputImage, "jpeg", new File(outputFileName));
    }

    public static BufferedImage compress(BufferedImage inputImage) {
        // create a black copy of the input image
        BufferedImage outputImage = new BufferedImage(width, height, inputImage.getType());

        // initialize the circles
        int circleCount = 0;
        while (circleCount < CIRCLE_COUNT) {
            updateBoundaries(circleCount);
            ThreadLocalRandom random = ThreadLocalRandom.current();
            int centerX = random.nextInt(width);
            int centerY = random.nextInt(height);
            int diameter = random.nextInt(maxDiameter);
            int majorityColour = getMajorityColour(inputImage, centerX, centerY, diameter);
            Circle circle = new Circle(centerX, centerY, diameter, majorityColour);
            if (calculateFitnessChange(inputImage, outputImage, circle) > 0) {
                circles[circleCount++] = drawCircle(inputImage, outputImage, circle);
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
        double percentageDone = (iteration / (double) CIRCLE_COUNT) * 100;
        // TODO: dynamically set the constant for max diameter, HAS to correlate with width/height!
        if (percentageDone < 10) {
            maxDiameter = Math.min(width, height) / 16;
        } else if (percentageDone < 25) {
            maxDiameter = Math.min(width, height) / 32;
        } else if (percentageDone < 50) {
            maxDiameter = Math.min(width, height) / 48;
        } else if (percentageDone < 75) {
            maxDiameter = Math.min(width, height) / 56;
        } else {
            maxDiameter = Math.min(width, height) / 64;
        }
    }

    private static Circle drawCircle(BufferedImage inputImage, BufferedImage outputImage,
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
        return new Circle(centerX, centerY, diameter, majorityColour);
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
                outputImage.setRGB(i, j, circle.getColour());
            }
        }
        return circle;
    }

    private static void perturb(BufferedImage inputImage, BufferedImage outputImage) {
        for (Circle circle : circles) {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            int currentFitness = calculateFitness(inputImage, outputImage);
            // try improving the position
            int originalX = circle.getX();
            int originalY = circle.getY();
            circle.setX(random.nextInt(originalX));
            circle.setY(random.nextInt(originalY));
            int newFitness = calculateFitness(inputImage, outputImage);
            if (newFitness > currentFitness) {
                currentFitness = newFitness;
            } else {
                circle.setX(originalX);
                circle.setY(originalY);
            }

            // try improving the diameter

            // try improving the colour
        }
    }

    private static int calculateFitness(BufferedImage inputImage, BufferedImage outputImage) {
        int fitness = 0;
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                // TODO scale the difference?
                int inputRGB = inputImage.getRGB(i, j);
                int outputRGB = outputImage.getRGB(i, j);
                fitness += Math.abs(inputRGB - outputRGB);
                assert fitness >= 0;
            }
        }
        return fitness;
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
