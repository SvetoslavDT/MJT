package bg.sofia.uni.fmi.mjt.music.server.repository;

import bg.sofia.uni.fmi.mjt.music.server.model.Playlist;
import bg.sofia.uni.fmi.mjt.music.server.model.Song;
import bg.sofia.uni.fmi.mjt.music.server.repository.exception.PlaylistAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.music.server.repository.exception.PlaylistNotFoundException;
import bg.sofia.uni.fmi.mjt.music.server.repository.exception.SongAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.music.server.repository.exception.SongNotFoundException;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryPlaylistRepository implements PlaylistRepository {

    private final ConcurrentHashMap<String, Playlist> playlists;

    public InMemoryPlaylistRepository() {
        playlists = new ConcurrentHashMap<>();
    }

//    private static class PlaylistData {
//        final ConcurrentHashMap<Song, AtomicInteger> likes = new ConcurrentHashMap<>();
//        final CopyOnWriteArrayList<Song> songOrder = new CopyOnWriteArrayList<>();
//    }

    @Override
    public void createPlaylist(String playlistName) throws PlaylistAlreadyExistsException {
        if (playlistName == null) {
            throw new IllegalArgumentException("Playlist name must not be null");
        }
        if (playlistName.contains(" ")) {
            throw new IllegalArgumentException("Playlist name must be one word");
        }
        if (playlists.containsKey(playlistName)) {
            throw new PlaylistAlreadyExistsException(playlistName + " already exists");
        }

        synchronized (playlists) {
            playlists.put(playlistName, new Playlist(playlistName, new HashMap<>()));
        }
    }

    @Override
    public Song addSong(String playlistName, String songTitle, String artistName, int duration)
        throws PlaylistNotFoundException, SongAlreadyExistsException {
        return null;
    }

    @Override
    public int likeSong(String playlistName, String songTitle, String artistName)
        throws PlaylistNotFoundException, SongNotFoundException {
        return 0;
    }

    @Override
    public int unlikeSong(String playlistName, String songTitle, String artistName)
        throws PlaylistNotFoundException, SongNotFoundException {
        return 0;
    }

    @Override
    public Collection<String> getAllPlaylists() {
        return List.of();
    }

    @Override
    public Playlist getPlaylist(String playlistName) throws PlaylistNotFoundException {
        return null;
    }
}
