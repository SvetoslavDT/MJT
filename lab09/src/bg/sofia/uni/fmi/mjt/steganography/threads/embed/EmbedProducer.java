package bg.sofia.uni.fmi.mjt.steganography.threads.embed;

import bg.sofia.uni.fmi.mjt.steganography.image.ImageManipulator;
import bg.sofia.uni.fmi.mjt.steganography.image.SecretManipulator;
import bg.sofia.uni.fmi.mjt.steganography.tasks.EmbedTask;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

public class EmbedProducer implements Runnable {

    private final Path coverPath;
    private final Path secretPath;
    private final String outputName;
    private final LinkedList<EmbedTask> tasks;
    private final AtomicInteger producersRemaining;

    public EmbedProducer(Path coverPath, Path secretPath, String outputName,
                         LinkedList<EmbedTask> tasks, AtomicInteger producersRemaining) {
        this.coverPath = coverPath;
        this.secretPath = secretPath;
        this.outputName = outputName;
        this.tasks = tasks;
        this.producersRemaining = producersRemaining;
    }

    @Override
    public void run() {
        try {
            BufferedImage coverImage = ImageManipulator.loadImage(coverPath);
            BufferedImage secretImage = ImageManipulator.loadImage(secretPath);

            if (!SecretManipulator.checkBufferedImageParameters(coverImage, secretImage)) {
                return;
            }

            synchronized (tasks) {
                tasks.addLast(new EmbedTask(coverImage, secretImage, outputName));
                tasks.notifyAll();
            }

        } finally {
            producersRemaining.decrementAndGet();
            synchronized (tasks) {
                tasks.notifyAll();
            }
        }
    }
}