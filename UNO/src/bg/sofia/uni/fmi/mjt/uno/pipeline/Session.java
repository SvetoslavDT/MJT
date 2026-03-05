package bg.sofia.uni.fmi.mjt.uno.pipeline;

import bg.sofia.uni.fmi.mjt.uno.players.Player;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public final class Session {

    private static final int BUFFER_SIZE = 1024;

    private final SocketChannel channel;
    private SelectionKey selectionKey;
    private final ByteBuffer readBuffer;
    private final StringBuilder readStringBuilder;

    private Player player;

    public Session(SocketChannel channel) {
        this.channel = channel;
        this.selectionKey = null;
        this.readBuffer = ByteBuffer.allocate(BUFFER_SIZE);
        this.readStringBuilder = new StringBuilder();
        this.player = null;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getUsername() {
        return player == null ? null : player.getUsername();
    }

    public Player getPlayer() {
        return player;
    }

    public void setSelectionKey(SelectionKey key) {
        this.selectionKey = key;
    }

    public SelectionKey getSelectionKey() {
        return selectionKey;
    }

    public ByteBuffer readBuffer() {
        return readBuffer;
    }

    public SocketAddress getRemoteAddress() {
        try {
            return channel.getRemoteAddress();
        } catch (IOException e) {
            return null;
        }
    }

    public void appendToInput(String chunk) {
        readStringBuilder.append(chunk);
    }

    public String pollLine(String delim) {
        int idx = readStringBuilder.indexOf(delim);

        if (idx == -1) {
            return null;
        }

        String line = readStringBuilder.substring(0, idx);
        readStringBuilder.delete(0, idx + delim.length());

        return line;
    }

    public void write(String message) throws IOException {
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buf = ByteBuffer.wrap(bytes);

        synchronized (channel) {
            while (buf.hasRemaining()) {
                int written = channel.write(buf);
                if (written == 0) {
//                  partial/zero write
                    break;
                }
            }
        }
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Session session)) return false;
        return Objects.equals(channel, session.channel) &&
            Objects.equals(selectionKey, session.selectionKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(channel, selectionKey);
    }
}
