package bg.sofia.uni.fmi.mjt.imagekit;

import bg.sofia.uni.fmi.mjt.imagekit.algorithm.ImageAlgorithm;
import bg.sofia.uni.fmi.mjt.imagekit.algorithm.detection.SobelEdgeDetection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SobelEdgeDetectionTest {

    @Mock
    ImageAlgorithm imageAlgorithmMock;

    @InjectMocks
    private SobelEdgeDetection sobelEdgeDetection;

    @Test
    void testSobelEdgeDetectionNullParameterThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new SobelEdgeDetection(null),
                "Expected IllegalArgumentException to be  thrown.");
    }

    @Test
    void testSobelEdgeDetectionCorrectParameter() {
        assertDoesNotThrow(() -> new SobelEdgeDetection(imageAlgorithmMock), "Expected correct" +
                " creation of SobelEdgeDetection");
    }

    @Test
    void testProcessNullParameterThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> sobelEdgeDetection.process(null),
                "Expected IllegalArgumentException to be  thrown.");
    }

    @Test
    void testProcessReturnsImageWithSameSize() {
        BufferedImage input = new BufferedImage(4, 3, BufferedImage.TYPE_INT_RGB);
        BufferedImage gray = new BufferedImage(4, 3, BufferedImage.TYPE_INT_RGB);
        when(imageAlgorithmMock.process(input)).thenReturn(gray);

        BufferedImage out = sobelEdgeDetection.process(input);

        assertEquals(gray.getWidth(), out.getWidth(), "Expected the same width");
        assertEquals(gray.getHeight(), out.getHeight(), "Expected the same height");
    }

    @Test
    void testProcessGreyScaleCallIsCorrect() {
        BufferedImage input = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        BufferedImage gray = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);

        gray.setRGB(0, 0, (10 << 16) | (10 << 8) | 10);
        gray.setRGB(1, 0, (20 << 16) | (20 << 8) | 20);
        gray.setRGB(0, 1, (30 << 16) | (30 << 8) | 30);
        gray.setRGB(1, 1, (40 << 16) | (40 << 8) | 40);

        when(imageAlgorithmMock.process(ArgumentMatchers.eq(input))).thenReturn(gray);
        BufferedImage out = sobelEdgeDetection.process(input);

        assertEquals(2, out.getWidth(), "Expected the same width from grayscale");
        assertEquals(2, out.getHeight(), "Expected the same height  from grayscale");
    }
}