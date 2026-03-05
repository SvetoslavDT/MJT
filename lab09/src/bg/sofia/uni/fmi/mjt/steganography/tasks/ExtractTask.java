package bg.sofia.uni.fmi.mjt.steganography.tasks;

import java.awt.image.BufferedImage;

public class ExtractTask {

    private final BufferedImage embedded;
    private final String outputName;

    public ExtractTask(BufferedImage embedded, String outputName) {
        this.embedded = embedded;
        this.outputName = outputName;
    }

    public BufferedImage getEmbedded() {
        return embedded;
    }

    public String getOutputName() {
        return outputName;
    }
}
