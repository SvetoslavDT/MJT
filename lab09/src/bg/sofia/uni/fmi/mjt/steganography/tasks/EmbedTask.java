package bg.sofia.uni.fmi.mjt.steganography.tasks;

import java.awt.image.BufferedImage;

public class EmbedTask {

    private final BufferedImage cover;
    private final BufferedImage secret;
    private final String outputName;

    public EmbedTask(BufferedImage cover, BufferedImage secret, String outputName) {
        this.cover = cover;
        this.secret = secret;
        this.outputName = outputName;
    }

    public BufferedImage getCover() {
        return cover;
    }

    public BufferedImage getSecret() {
        return secret;
    }

    public String getOutputName() {
        return outputName;
    }
}
