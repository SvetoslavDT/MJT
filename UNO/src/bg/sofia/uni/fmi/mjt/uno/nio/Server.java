package bg.sofia.uni.fmi.mjt.uno.nio;

import bg.sofia.uni.fmi.mjt.uno.pipeline.Session;
import bg.sofia.uni.fmi.mjt.uno.repositories.SessionRepository;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

import static bg.sofia.uni.fmi.mjt.uno.pipeline.CommandHandler.handleCommand;

public final class Server {

    private static final String HOST = "0.0.0.0";
    private static final int PORT = 4444;

    private static final String PROTOCOL_DELIMITER = "\n";

    private final SessionRepository sessionRepo = SessionRepository.getInstance();

    void main() {
        try (ServerSocketChannel serverChannel = ServerSocketChannel.open();
             Selector selector = Selector.open()) {

            serverChannel.bind(new InetSocketAddress(HOST, PORT));
            serverChannel.configureBlocking(false);
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("Uno server listening on port " + HOST + ":" + PORT);

            while (true) {
                int readyChannels = selector.select();
                if (readyChannels == 0) {
                    continue;
                }

                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (!key.isValid()) {
                        continue;
                    }

                    try {
                        if (key.isAcceptable()) {
                            accept(key, selector);
                        } else if (key.isReadable()) {
                            read(key);
                        }
                    } catch (IOException e) {
                        Object attachment = key.attachment();
                        if (attachment instanceof Session s) {
                            System.out.println(
                                "IO error for client " + s.getRemoteAddress() + ": " + e.getMessage());
                        } else {
                            System.out.println("IO error: " + e.getMessage());
                        }

                        closeKey(key);
                    }
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Fatal error occurred", e);
        }
    }

    private void accept(SelectionKey key, Selector selector) throws IOException {
        ServerSocketChannel serverSocket = (ServerSocketChannel) key.channel();
        SocketChannel client = serverSocket.accept();
        if (client == null) {
            return;
        }

        client.configureBlocking(false);

        Session session = new Session(client);
        SelectionKey clientKey = client.register(selector, SelectionKey.OP_READ, session);
        session.setSelectionKey(clientKey);

        sessionRepo.addSession(session);

        System.out.println("Accepted: " + session.getRemoteAddress());
    }

    private void closeKey(SelectionKey key) throws IOException {
        try {
            Object attachment = key.attachment();
            if (attachment instanceof Session s) {
                sessionRepo.removeSession(s);
            }
        } finally {
            key.cancel();
            Channel ch = key.channel();
            if (ch != null) {
                ch.close();
            }
        }
    }

    private void read(SelectionKey key) throws IOException {
        Session session = (Session) key.attachment();
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer inBuf = session.readBuffer();
        int read = channel.read(inBuf);
        if (read == -1) {
            System.out.println("Client closed: " + session.getRemoteAddress());
            closeKey(key);
            return;
        } else if (read == 0) {
            return;
        }
        inBuf.flip();
        byte[] bytes = new byte[inBuf.remaining()];
        inBuf.get(bytes);
        inBuf.clear();

        String chunk = new String(bytes, StandardCharsets.UTF_8);
        session.appendToInput(chunk);

        String line;
        while ((line = session.pollLine(PROTOCOL_DELIMITER)) != null) {
            line = line.trim();
            if (line.isBlank()) {
                continue;
            }

            handleCommand(session, line, sessionRepo);
        }
    }
}
