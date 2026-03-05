package bg.sofia.uni.fmi.mjt.steganography.threads.extract;

import bg.sofia.uni.fmi.mjt.steganography.image.ImageManipulator;
import bg.sofia.uni.fmi.mjt.steganography.image.SecretManipulator;
import bg.sofia.uni.fmi.mjt.steganography.tasks.ExtractTask;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

public class ExtractConsumer implements Runnable {

    private final LinkedList<ExtractTask> tasks;
    private final AtomicInteger producersRemaining;
    private final Path outDir;

    public ExtractConsumer(LinkedList<ExtractTask> tasks, AtomicInteger producersRemaining, Path outDir) {
        this.tasks = tasks;
        this.producersRemaining = producersRemaining;
        this.outDir = outDir;
    }

    @Override
    public void run() {
        try {
            while (true) {
                ExtractTask task;
                synchronized (tasks) {
                    while (tasks.isEmpty() && producersRemaining.get() > 0) {
                        tasks.wait();
                    }
                    if (tasks.isEmpty() && producersRemaining.get() == 0) {
                        break;
                    }
                    task = tasks.removeFirst();
                }
                BufferedImage secret = SecretManipulator.extractSecretImage(task.getEmbedded());
                ImageManipulator.saveImage(secret, outDir.toString(), task.getOutputName());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}