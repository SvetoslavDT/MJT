package bg.sofia.uni.fmi.mjt.music.server.model;

import java.util.Map;
import java.util.Objects;

public class Playlist {

    private final String name;
    private final Map<Song, Integer> songs;

    public Playlist(String name, Map<Song, Integer> songs) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Playlist name cannot be null or blank");
        }
        if (songs == null) {
            throw new IllegalArgumentException("Playlist songs cannot be null");
        }
//        if (songs.isEmpty()) {
//            throw new IllegalArgumentException("Playlist songs cannot be empty");
//        }

        this.name = name;
        this.songs = songs;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Playlist other = (Playlist) obj;

        return name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
