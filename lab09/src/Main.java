import bg.sofia.uni.fmi.mjt.steganography.ImageCodecImpl;

void main() {

    ImageCodecImpl codec = new ImageCodecImpl();
    codec.embedPNGImages("C:\\Users\\Svetoslav Tsvetkov\\IdeaProjects\\lab09\\ratchet",
            "C:\\Users\\Svetoslav Tsvetkov\\IdeaProjects\\lab09\\batman",
            "C:\\Users\\Svetoslav Tsvetkov\\IdeaProjects\\lab09\\result");

    codec.extractPNGImages("C:\\Users\\Svetoslav Tsvetkov\\IdeaProjects\\lab09\\result",
            "C:\\Users\\Svetoslav Tsvetkov\\IdeaProjects\\lab09");
}
