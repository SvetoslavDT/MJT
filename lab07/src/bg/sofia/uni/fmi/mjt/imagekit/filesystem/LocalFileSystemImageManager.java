package bg.sofia.uni.fmi.mjt.imagekit.filesystem;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LocalFileSystemImageManager implements FileSystemImageManager {

    private static final List<String> SUPPORTED_FORMATS = Arrays.asList("png", "jpeg", "bmp", "jpg");

    public LocalFileSystemImageManager() {

    }

    @Override
    public BufferedImage loadImage(File imageFile) throws IOException {
        if (imageFile == null) {
            throw new IllegalArgumentException("imageFile is null");
        }
        if (!imageFile.exists() || !imageFile.isFile()) {
            throw new IOException("imageFile does not exist or is not a file");
        }

        String format = getFileExtension(imageFile.getName());
        if (!isSupportedFormat(format)) {
            throw new IOException("Unsupported format");
        }

        BufferedImage image = ImageIO.read(imageFile);

        return image;
    }

    @Override
    public List<BufferedImage> loadImagesFromDirectory(File imagesDirectory) throws IOException {
        if (imagesDirectory == null) {
            throw new IllegalArgumentException("imagesDirectory is null");
        }
        if (!imagesDirectory.exists() || !imagesDirectory.isDirectory()) {
            throw new IOException("imagesDirectory does not exist or is not a directory");
        }

        File[] files = imagesDirectory.listFiles();
        if (files == null) {
            throw new IOException("imagesDirectory does not exist or is not a file");
        }

        List<BufferedImage> images = new ArrayList<>();
        for (File file : files) {

            if (file.isFile()) {
                if (!isSupportedFormat(getFileExtension(file.getName()))) {
                    throw new IOException("Unsupported format");
                }

                BufferedImage image = ImageIO.read(file);
                if (image != null) {
                    images.add(image);
                }
            }

        }
        return images;
    }

    @Override
    public void saveImage(BufferedImage image, File imageFile) throws IOException {
        if (image == null || imageFile == null) {
            throw new IllegalArgumentException("image or imageFile is null");
        }
        File imageFileParent = imageFile.getAbsoluteFile().getParentFile();
        if (imageFile.exists() || (imageFileParent != null && !imageFileParent.exists())) {
            throw new IOException("image file does not exist or its parent does not exist");
        }

        ImageIO.write(image, "png", imageFile);
    }

    private static String getFileExtension(String fileName) {
        int index = fileName.lastIndexOf('.');

        if (index < 0 || index == fileName.length() - 1) {
            return null;
        }

        return fileName.substring(index + 1);
    }

    private static boolean isSupportedFormat(String format) {
        return format != null && SUPPORTED_FORMATS.contains(format.toLowerCase());
    }
}