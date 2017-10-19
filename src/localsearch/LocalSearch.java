package localsearch;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import static localsearch.GraphicHelper.dist;
import static localsearch.GraphicHelper.getMajorityColour;

public class LocalSearch {

    private final static int CIRCLE_COUNT = 1000;
    private final static int PERTURBATION_MAX_CHANGE = 5;   // in percentage
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
        maxDiameter = Math.min(width, height) / 16;

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
            circles[i] = drawCircle(inputImage, outputImage, centerX, centerY, diameter);
        }

        perturb(inputImage, outputImage);

        return outputImage;
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
                // TODO scale the difference
                int inputRGB = inputImage.getRGB(i, j);
                // TODO implement diff
            }
        }
        return fitness;
    }
}
