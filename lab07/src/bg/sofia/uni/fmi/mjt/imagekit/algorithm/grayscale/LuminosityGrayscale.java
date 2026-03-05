package bg.sofia.uni.fmi.mjt.imagekit.algorithm.grayscale;

import java.awt.image.BufferedImage;

public class LuminosityGrayscale implements GrayscaleAlgorithm {

    private static final int RED_BITS_MOVE = 16;
    private static final int GREEN_BITS_MOVE = 8;
    private static final int MAX_COLOR_VALUE = 0xff;
    private static final double RED_PRODUCT_COEFFICIENT = 0.21;
    private static final double GREEN_PRODUCT_COEFFICIENT = 0.72;
    private static final double BLUE_PRODUCT_COEFFICIENT = 0.07;
    private static final int MAX_DIAPASON = 255;

    public LuminosityGrayscale() {

    }

    @Override
    public BufferedImage process(BufferedImage image) {
        if (image == null) {
            throw new IllegalArgumentException("Image may not be null");
        }

        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {

                int rgb = image.getRGB(x, y);
                int grayRgb = getGrayRgb(rgb);
                result.setRGB(x, y, grayRgb);
            }
        }

        return result;
    }

    private static int getGrayRgb(int rgb) {
        int r = (rgb >> RED_BITS_MOVE) & MAX_COLOR_VALUE;
        int g = (rgb >> GREEN_BITS_MOVE) & MAX_COLOR_VALUE;
        int b = rgb & MAX_COLOR_VALUE;

        long rounded = Math.round(RED_PRODUCT_COEFFICIENT * r + GREEN_PRODUCT_COEFFICIENT * g +
                BLUE_PRODUCT_COEFFICIENT * b);
        int diapason = (int) Math.min(MAX_DIAPASON, Math.max(0, rounded));

        return (diapason << RED_BITS_MOVE) | (diapason << GREEN_BITS_MOVE) | diapason;
    }
}