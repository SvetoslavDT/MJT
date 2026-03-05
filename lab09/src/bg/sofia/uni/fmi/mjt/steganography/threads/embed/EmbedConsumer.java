package bg.sofia.uni.fmi.mjt.steganography.threads.embed;

import bg.sofia.uni.fmi.mjt.steganography.image.ImageManipulator;
import bg.sofia.uni.fmi.mjt.steganography.image.SecretManipulator;
import bg.sofia.uni.fmi.mjt.steganography.tasks.EmbedTask;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class EmbedConsumer implements Runnable {

    private final List<EmbedTask> tasks;
    private final AtomicInteger producersRemaining;
    private final Path outDir;

    public EmbedConsumer(LinkedList<EmbedTask> tasks, AtomicInteger producersRemaining, Path outDir) {
        this.tasks = tasks;
        this.producersRemaining = producersRemaining;
        this.outDir = outDir;
    }

    @Override
    public void run() {
        try {
            while (true) {
                EmbedTask task;

                synchronized (tasks) {
                    while (tasks.isEmpty() && producersRemaining.get() > 0) {
                        tasks.wait();
                    }
                    if (tasks.isEmpty() && producersRemaining.get() == 0) {
                        break;
                    }
                    task = tasks.removeFirst();
                }

                BufferedImage embeded = SecretManipulator.embedSecretImage(task.getCover(), task.getSecret());
                if (embeded == null) {
                    continue;
                }
                ImageManipulator.saveImage(embeded, outDir.toString(), task.getOutputName());

            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
