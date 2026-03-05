package bg.sofia.uni.fmi.mjt.steganography.threads.extract;

import bg.sofia.uni.fmi.mjt.steganography.image.ImageManipulator;
import bg.sofia.uni.fmi.mjt.steganography.tasks.ExtractTask;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

public class ExtractProducer implements Runnable {

    private final Path path;
    private final LinkedList<ExtractTask> tasks;
    private final AtomicInteger producersRemaining;

    public ExtractProducer(Path path, LinkedList<ExtractTask> tasks, AtomicInteger producersRemaining) {
        this.path = path;
        this.tasks = tasks;
        this.producersRemaining = producersRemaining;
    }

    @Override
    public void run() {
        try {
            BufferedImage embedded = ImageManipulator.loadImage(path);
            synchronized (tasks) {
                tasks.addLast(new ExtractTask(embedded, path.getFileName().toString()));
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
