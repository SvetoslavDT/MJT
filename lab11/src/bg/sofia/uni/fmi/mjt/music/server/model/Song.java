package bg.sofia.uni.fmi.mjt.music.server.model;

import java.util.Objects;

public record Song(String title, String artist, int duration) {

    public Song {
        if (title == null || artist == null) {
            throw new IllegalArgumentException("title or artist is null");
        }
        if (title.isBlank() || artist.isBlank()) {
            throw new IllegalArgumentException("title or artist is blank");
        }
        if (duration <= 0) {
            throw new IllegalArgumentException("duration is negative or zero");
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Song song = (Song) obj;

        return Objects.equals(title, song.title) && Objects.equals(artist, song.artist);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, artist);
    }
}