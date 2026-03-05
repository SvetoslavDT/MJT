package bg.sofia.uni.fmi.mjt.imagekit;

import bg.sofia.uni.fmi.mjt.imagekit.algorithm.ImageAlgorithm;
import bg.sofia.uni.fmi.mjt.imagekit.algorithm.grayscale.LuminosityGrayscale;
import bg.sofia.uni.fmi.mjt.imagekit.filesystem.FileSystemImageManager;
import bg.sofia.uni.fmi.mjt.imagekit.filesystem.LocalFileSystemImageManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LocalFileSystemImageManagerTest {

    private LocalFileSystemImageManager manager = new LocalFileSystemImageManager();

    @Test
    void testLoadImageImageFileIsNullThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> manager.loadImage(null),
                "Expected IllegalArgumentException to be thrown");
    }

    @Test
    void testLoadImageImageFileDoesNotExistThrowsIOException() {
        File file = mock();
        when(file.exists()).thenReturn(false);
        assertThrows(IOException.class, () -> manager.loadImage(file), "Expected IOException to be thrown");
    }

    @Test
    void testLoadImageFileIsNotRegularThrowsIOException() {
        File file = mock();
        when(file.isFile()).thenReturn(false);
        assertThrows(IOException.class, () -> manager.loadImage(file), "Expected IOException to be thrown");
    }

    @Test
    void loadImage_readsPngSuccessfully(@TempDir Path tempDir) throws IOException {
        File f = writeTestImage(tempDir, "img.png", "png");
        assertTrue(f.exists());
        BufferedImage loaded = manager.loadImage(f);
        assertNotNull(loaded, "Expected non-null BufferedImage for a valid PNG");
        assertEquals(10, loaded.getWidth());
        assertEquals(8, loaded.getHeight());
    }

    @Test
    void testLoadImagesFromDirectoryFileIsNullThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> manager.loadImagesFromDirectory(null),
                "Expected IllegalArgumentException to be thrown");
    }

    @Test
    void testLoadImagesFromDirectoryFileDoesNotExistThrowsIOException() {
        File file = mock();
        when(file.exists()).thenReturn(false);
        assertThrows(IOException.class, () -> manager.loadImagesFromDirectory(file),
                "Expected IOException to be thrown");
    }

    @Test
    void testLoadImagesFromDirectoryFileIsNotRegularThrowsIOException() {
        File file = mock();
        when(file.isFile()).thenReturn(false);
        assertThrows(IOException.class, () -> manager.loadImagesFromDirectory(file),
                "Expected IOException to be thrown");
    }

    @Test
    void loadImagesFromDirectory_withSupportedImages_readsAll(@TempDir Path tempDir) throws IOException {
        Path dir = tempDir.resolve("imgs");
        Files.createDirectory(dir);

        // create supported images
        writeTestImage(dir, "a.png", "png");
        writeTestImage(dir, "b.jpg", "jpg");
        writeTestImage(dir, "c.jpeg", "jpeg");

        List<BufferedImage> imgs = manager.loadImagesFromDirectory(dir.toFile());
        assertNotNull(imgs);
        assertEquals(3, imgs.size(), "Expected 3 images loaded");
        for (BufferedImage bi : imgs) {
            assertEquals(10, bi.getWidth());
            assertEquals(8, bi.getHeight());
        }
    }

    @Test
    void testSaveImageImageIsNullThrowsIllegalArgumentException() {
        File file = new File("file");
        assertThrows(IllegalArgumentException.class, () -> manager.saveImage(null, file),
                "Expected IllegalArgumentException to be thrown");
    }

    @Test
    void testSaveImageWorksCorrectly(@TempDir Path tempDir) throws IOException {
        ImageAlgorithm grayscaleAlgorithm = new LuminosityGrayscale();
        FileSystemImageManager fsImageManager = new LocalFileSystemImageManager();

        BufferedImage image = new BufferedImage(10, 8, BufferedImage.TYPE_INT_RGB);

        BufferedImage grayscaleImage = grayscaleAlgorithm.process(image);
        File out = tempDir.resolve("out-grayscale.png").toFile();
        fsImageManager.saveImage(grayscaleImage, out);

        assertTrue(out.exists(), "Output file should exist");
        BufferedImage read = ImageIO.read(out);
        assertEquals(grayscaleImage.getWidth(), read.getWidth());
        assertEquals(grayscaleImage.getHeight(), read.getHeight());
    }

    @Test
    void testSaveImageImageFileIsNullThrowsIllegalArgumentException() {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        assertThrows(IllegalArgumentException.class, () -> manager.saveImage(image, null),
                "Expected IllegalArgumentException to be thrown");
    }

    private static File writeTestImage(Path dir, String name, String format) throws IOException {
        BufferedImage img = new BufferedImage(10, 8, BufferedImage.TYPE_INT_RGB);

        Path p = dir.resolve(name);
        ImageIO.write(img, format, p.toFile());
        return p.toFile();
    }
}