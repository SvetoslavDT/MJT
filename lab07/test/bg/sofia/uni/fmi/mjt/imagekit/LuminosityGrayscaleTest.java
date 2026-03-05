package bg.sofia.uni.fmi.mjt.imagekit;

import bg.sofia.uni.fmi.mjt.imagekit.algorithm.grayscale.LuminosityGrayscale;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LuminosityGrayscaleTest {

    private LuminosityGrayscale l = new LuminosityGrayscale();

    private static int expectedGray24(int r, int g, int b) {
        long rounded = Math.round(0.21 * r + 0.72 * g + 0.07 * b);
        int diapason = (int) Math.min(255, Math.max(0, rounded));
        return (diapason << 16) | (diapason << 8) | diapason;
    }

    @Test
    void testProcessWithNullArgument() {
        assertThrows(IllegalArgumentException.class, () -> l.process(null), "Expected to be thrown " +
                "IllegalArgumentException when parameter is null");
    }

    @Test
    void testProcessResultHasSameSize() {
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        BufferedImage result = l.process(image);
        assertEquals(image.getWidth(), result.getWidth(), "Expected same width of image");
        assertEquals(image.getHeight(), result.getHeight(), "Expected same height of image");
    }

    @Test
    void testProcessResultIsDifferentObject() {
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        BufferedImage result = l.process(image);
        assertNotSame(image, result, "Expected different image object as result");
    }

    @Test
    void testProcessConvertsCorrectlyToGray() {
        BufferedImage in = new BufferedImage(3, 1, BufferedImage.TYPE_INT_RGB);
        in.setRGB(0, 0, (255 << 16) | (0 << 8) | 0);   // red
        in.setRGB(1, 0, (0 << 16) | (255 << 8) | 0);   // green
        in.setRGB(2, 0, (0 << 16) | (0 << 8) | 255);   // blue

        BufferedImage out = l.process(in);

        int out0 = out.getRGB(0, 0) & 0x00FFFFFF;
        int out1 = out.getRGB(1, 0) & 0x00FFFFFF;
        int out2 = out.getRGB(2, 0) & 0x00FFFFFF;

        assertEquals(expectedGray24(255, 0, 0), out0, "Red should convert to expected gray");
        assertEquals(expectedGray24(0, 255, 0), out1, "Green should convert to expected gray");
        assertEquals(expectedGray24(0, 0, 255), out2, "Blue should convert to expected gray");
    }
}