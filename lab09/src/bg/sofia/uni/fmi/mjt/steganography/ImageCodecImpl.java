package bg.sofia.uni.fmi.mjt.steganography;

import bg.sofia.uni.fmi.mjt.steganography.tasks.EmbedTask;
import bg.sofia.uni.fmi.mjt.steganography.tasks.ExtractTask;
import bg.sofia.uni.fmi.mjt.steganography.threads.embed.EmbedConsumer;
import bg.sofia.uni.fmi.mjt.steganography.threads.embed.EmbedProducer;
import bg.sofia.uni.fmi.mjt.steganography.threads.extract.ExtractConsumer;
import bg.sofia.uni.fmi.mjt.steganography.threads.extract.ExtractProducer;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ImageCodecImpl implements ImageCodec {

    @Override
    public void embedPNGImages(String coverSourceDirectory, String secretSourceDirectory, String outputDirectory) {

        Path coverDirectory = Paths.get(coverSourceDirectory);
        Path secretDirectory = Paths.get(secretSourceDirectory);
        Path outDirectory = Paths.get(outputDirectory);

        validateDirectory(coverDirectory, "coverSourceDirectory");
        validateDirectory(secretDirectory, "secretSourceDirectory");

        List<Path> covers = getSortedPngFiles(coverDirectory);
        List<Path> secrets = getSortedPngFiles(secretDirectory);
        if (covers.size() != secrets.size()) {
            throw new IllegalArgumentException("covers and secrets must have same size");
        }
        try {
            Files.createDirectories(outDirectory);
        } catch (IOException e) {
            throw new UncheckedIOException("Exception thrown from making directories", e);
        }

        final LinkedList<EmbedTask> tasks = new LinkedList<>();
        final AtomicInteger remainingProducers = new AtomicInteger(covers.size());
        final int numConsumers = Runtime.getRuntime().availableProcessors();

        List<Thread> consumers = startEmbedConsumers(tasks, remainingProducers, outDirectory, numConsumers);
        List<Thread> producers = startEmbedProducers(covers, secrets, tasks, remainingProducers);

        joinThreads(producers);
        joinThreads(consumers);
    }

    @Override
    public void extractPNGImages(String sourceDirectory, String outputDirectory) {
        Path srcDir = Paths.get(sourceDirectory);
        Path outDir = Paths.get(outputDirectory);

        validateDirectory(srcDir, "sourceDirectory");

        List<Path> pngs = getSortedPngFiles(srcDir);
        try {
            Files.createDirectories(outDir);
        } catch (IOException e) {
            throw new UncheckedIOException("Exception thrown from making directories", e);
        }

        final LinkedList<ExtractTask> tasks = new LinkedList<>();
        final AtomicInteger producersRemaining = new AtomicInteger(pngs.size());
        final int numConsumers = Runtime.getRuntime().availableProcessors();

        List<Thread> consumers = startExtractConsumers(tasks, producersRemaining, outDir, numConsumers);
        List<Thread> producers = startExtractProducers(pngs, tasks, producersRemaining);

        joinThreads(consumers);
        joinThreads(producers);
    }

    private static List<Thread> startExtractConsumers(LinkedList<ExtractTask> tasks, AtomicInteger producersRemaining,
                                                      Path outDir, int numConsumers) {
        List<Thread> consumers = new ArrayList<>(numConsumers);
        for (int i = 0; i < numConsumers; i++) {
            Thread thread = new Thread(new ExtractConsumer(tasks, producersRemaining, outDir),
                    "extract-consumer-" + i);
            thread.start();
            consumers.add(thread);
        }

        return consumers;
    }

    private List<Thread> startExtractProducers(List<Path> pngs, LinkedList<ExtractTask> tasks,
                                               AtomicInteger producersRemaining) {
        List<Thread> producers = new ArrayList<>(pngs.size());
        int idx = 0;
        for (Path p : pngs) {
            Thread pr = new Thread(new ExtractProducer(p, tasks, producersRemaining),
                    "extract-producer-" + idx + "-" + p.getFileName().toString());
            pr.start();
            producers.add(pr);
            idx++;
        }

        return producers;
    }

    private static List<Thread> startEmbedConsumers(LinkedList<EmbedTask> tasks, AtomicInteger remainingProducers,
                                                    Path outDir, int numConsumers) {
        List<Thread> consumers = new ArrayList<>(numConsumers);
        for (int i = 0; i < numConsumers; i++) {
            Thread thread = new Thread(new EmbedConsumer(tasks, remainingProducers, outDir), "embed-" + i);
            thread.start();
            consumers.add(thread);
        }

        return consumers;
    }

    private static List<Thread> startEmbedProducers(List<Path> covers, List<Path> secrets,
                                                    LinkedList<EmbedTask> tasks, AtomicInteger remainingProducers) {
        List<Thread> producers = new ArrayList<>(covers.size());

        for (int i = 0; i < covers.size(); i++) {
            final Path coverPath = covers.get(i);
            final Path secretPath = secrets.get(i);
            final String outputName = coverPath.getFileName().toString();

            Thread thread = new Thread(new EmbedProducer(coverPath, secretPath, outputName, tasks, remainingProducers),
                    "embed-" + i);
            thread.start();
            producers.add(thread);
        }

        return producers;
    }

    private static void validateDirectory(Path dir, String name) {
        if (!Files.exists(dir) || !Files.isDirectory(dir)) {
            throw new IllegalArgumentException(name + " does not exist or is not a directory: " + dir);
        }
    }

    private static List<Path> getSortedPngFiles(Path dir) {
        try (Stream<Path> s = Files.list(dir)) {

            return s.filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().toLowerCase().endsWith(".png"))
                    .sorted(Comparator.comparing(p -> p.getFileName().toString()))
                    .collect(Collectors.toList());

        } catch (IOException e) {
            throw new UncheckedIOException("Failed to list PNG files", e);
        }
    }

    private static void joinThreads(List<Thread> list) {
        for (Thread thread : list) {

            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}