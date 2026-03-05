package bg.sofia.uni.fmi.mjt.uno.nio;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public final class Client {

    private static final int SERVER_PORT = 4444;
    private static final String SERVER_HOST = "localhost";
    static final int BUFFER_SIZE = 4096;
    private static final String LINE_DELIMITER = System.lineSeparator();

    void main() {
        AtomicBoolean running = new AtomicBoolean(true);

        try (SocketChannel socketChannel = SocketChannel.open();
             Scanner scanner = new Scanner(System.in)) {

            socketChannel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
            System.out.print("Connected to the server");

            Thread serverReaderThread = Thread.startVirtualThread(() -> readLoop(socketChannel, running));

            while (running.get()) {
                System.out.print("> ");
                if (!scanner.hasNextLine()) {
                    break;
                }
                String line = scanner.nextLine().trim();
                if (line.equalsIgnoreCase("quit")) {
                    running.set(false);
                    break;
                }
                sendLine(socketChannel, line);
            }
            serverReaderThread.join();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("There is a problem with the network communication", e);
        }
    }

    private static void sendLine(SocketChannel socketChannel, String line) {
        byte[] bytes = (line + LINE_DELIMITER).getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        synchronized (socketChannel) {
            try {
                while (buffer.hasRemaining()) {
                    socketChannel.write(buffer);
                }
            } catch (IOException e) {
                throw new UncheckedIOException("Failed to send message to server", e);
            }
        }
    }

    private static void readLoop(SocketChannel socketChannel, AtomicBoolean running) {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        StringBuilder stringBuilder = new StringBuilder();

        try {
            while (running.get()) {
                buffer.clear();
                int read = socketChannel.read(buffer);
                if (read == -1) {
                    System.out.println(LINE_DELIMITER + "server disconnected");
                    running.set(false);
                    break;
                } else if (read == 0) {
                    continue;
                }

                buffer.flip();
                byte[] bytes = new byte[buffer.remaining()];
                buffer.get(bytes);
                String chunk = new String(bytes, StandardCharsets.UTF_8);
                stringBuilder.append(chunk);

                int idx;
                while ((idx = stringBuilder.indexOf(LINE_DELIMITER)) != -1) {
                    String line = stringBuilder.substring(0, idx);
                    System.out.println(LINE_DELIMITER + line);
                    stringBuilder.delete(0, idx + LINE_DELIMITER.length());
                    System.out.print("> ");
                }
            }
        } catch (IOException e) {
            if (running.get()) {
                throw new RuntimeException("There is a problem with the network communication", e);
            }
            running.set(false);
        }
    }
}
