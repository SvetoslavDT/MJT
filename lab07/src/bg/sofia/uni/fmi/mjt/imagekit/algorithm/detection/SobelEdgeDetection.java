package bg.sofia.uni.fmi.mjt.imagekit.algorithm.detection;

import bg.sofia.uni.fmi.mjt.imagekit.algorithm.ImageAlgorithm;

import java.awt.image.BufferedImage;

public class SobelEdgeDetection implements EdgeDetectionAlgorithm {

    private final ImageAlgorithm grayscaleAlgorithm;
    private static final int[][] G_X = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
    private static final int[][] G_Y = {{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}};
    private static final int BLUE_BITS_MOVE = 24;
    private static final int RED_BITS_MOVE = 16;
    private static final int GREEN_BITS_MOVE = 8;
    private static final int MAX_COLOR_VALUE = 0xff;
    private static final int MAX_DIAPASON = 255;

    public SobelEdgeDetection(ImageAlgorithm grayscaleAlgorithm) {
        if (grayscaleAlgorithm == null) {
            throw new IllegalArgumentException("grayscale algorithm cannot be null");
        }

        this.grayscaleAlgorithm = grayscaleAlgorithm;
    }

    @Override
    public BufferedImage process(BufferedImage image) {
        if (image == null) {
            throw new IllegalArgumentException("image cannot be null");
        }

        BufferedImage grayImage = grayscaleAlgorithm.process(image);
        int width = grayImage.getWidth();
        int height = grayImage.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixelValue = computeSobelPixelValue(grayImage, x, y, width, height);
                int rgbFinal = (MAX_COLOR_VALUE << BLUE_BITS_MOVE)
                        | (pixelValue << RED_BITS_MOVE)
                        | (pixelValue << GREEN_BITS_MOVE)
                        | pixelValue;
                result.setRGB(x, y, rgbFinal);
            }
        }

        return result;
    }

    private int computeSobelPixelValue(BufferedImage grayImage, int x, int y, int width, int height) {
        int sumGx = 0;
        int sumGy = 0;

        for (int ky = -1; ky <= 1; ky++) {
            for (int kx = -1; kx <= 1; kx++) {
                int nx = x + kx;
                int ny = y + ky;
                int intensity = 0;

                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    int rgb = grayImage.getRGB(nx, ny);
                    intensity = rgb & MAX_COLOR_VALUE;
                }

                sumGx += G_X[ky + 1][kx + 1] * intensity;
                sumGy += G_Y[ky + 1][kx + 1] * intensity;
            }
        }

        double g = Math.sqrt((double) sumGx * sumGx + (double) sumGy * sumGy);
        return (int) Math.min(MAX_DIAPASON, Math.round(g));
    }
}