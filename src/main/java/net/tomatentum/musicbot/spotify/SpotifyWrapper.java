package net.tomatentum.musicbot.spotify;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.BasicAudioPlaylist;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import com.wrapper.spotify.model_objects.specification.*;
import net.tomatentum.musicbot.TomatenMusic;
import net.tomatentum.musicbot.music.GuildMusicManager;
import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SpotifyWrapper {


	private SpotifyApi spotifyApi;
	private String clientId = TomatenMusic.getInstance().getConfiguration().getString("Spotify.clientid");
	private String secret = TomatenMusic.getInstance().getConfiguration().getString("Spotify.secret");
	private String redirectURI = "https://tomatentum.net";
	private ClientCredentials currentCredentials;
	private long lastTokenFetch;


	private static SpotifyWrapper instance = new SpotifyWrapper();

	public SpotifyWrapper() {
		this.spotifyApi = new SpotifyApi.Builder()
				.setClientSecret(secret)
				.setClientId(clientId)
				.setRedirectUri(SpotifyHttpManager.makeUri(redirectURI))
				.build();
		try {
			currentCredentials = spotifyApi.clientCredentials().build().execute();
		} catch (IOException | SpotifyWebApiException | ParseException e) {
			e.printStackTrace();
		}
		spotifyApi.setAccessToken(currentCredentials.getAccessToken());
		lastTokenFetch = System.currentTimeMillis();
	}

	public void playTrack(String link, GuildMusicManager musicManager) {

		fetchAccessToken();

		String id = link.replace("https://open.spotify.com/track/", "").substring(0, 22);
		Track track;

		try {
			track = spotifyApi.getTrack(id).build().execute();
		} catch (IOException | SpotifyWebApiException | ParseException e) {
			e.printStackTrace();
			return;
		}
		System.out.println("Searching for: " + track.getName() + " " + track.getArtists()[0].getName());

		TomatenMusic.getInstance().getAudioManager().getAudioPlayerManager().loadItem("ytsearch:" + track.getName() + " " + track.getArtists()[0].getName(), new AudioLoadResultHandler() {
			@Override
			public void trackLoaded(AudioTrack track) {
				musicManager.play(track);
			}

			@Override
			public void playlistLoaded(AudioPlaylist playlist) {
				musicManager.play(playlist.getTracks().get(0));
			}

			@Override
			public void noMatches() {
				System.out.println("No Spotify Matches");
			}

			@Override
			public void loadFailed(FriendlyException exception) {
				System.out.println(exception.severity + ", Error: " + exception.getMessage());
				exception.printStackTrace();
			}
		});

	}

	public void playPlaylist(String link, GuildMusicManager musicManager) {
		fetchAccessToken();

		String id = link.replace("https://open.spotify.com/playlist/", "").substring(0, 22);
		Playlist playlist;
		List<AudioTrack> tracks = new ArrayList<>();

		try {
			playlist = spotifyApi.getPlaylist(id).build().execute();
		} catch (IOException | SpotifyWebApiException | ParseException e) {
			e.printStackTrace();

			return;
		}

		for (PlaylistTrack ptrack : playlist.getTracks().getItems()) {

			Track track = (Track) ptrack.getTrack();
			System.out.println("Searching for: " + track.getName() + " " + track.getArtists()[0].getName());

			TomatenMusic.getInstance().getAudioManager().getAudioPlayerManager().loadItem("ytsearch:" + track.getName() + " " + track.getArtists()[0].getName(), new AudioLoadResultHandler() {
				@Override
				public void trackLoaded(AudioTrack track) {
					tracks.add(track);

					if (playlist.getTracks().getItems().length <=tracks.size())
						musicManager.play(new BasicAudioPlaylist("Spotify search results for: " + link,tracks, tracks.get(0), true));
				}

				@Override
				public void playlistLoaded(AudioPlaylist audioPlaylist) {
					tracks.add(audioPlaylist.getTracks().get(0));

					if (playlist.getTracks().getItems().length <=tracks.size())
						musicManager.play(new BasicAudioPlaylist("Spotify search results for: " + link,tracks, tracks.get(0), true));
				}

				@Override
				public void noMatches() {
					System.out.println("No Spotify Matches");

				}

				@Override
				public void loadFailed(FriendlyException exception) {
					System.out.println(exception.severity + " : " + exception.getMessage());
					exception.printStackTrace();

				}
			});
		}
	}

	public void playAlbum(String link, GuildMusicManager musicManager) {
		fetchAccessToken();

		String id = link.replace("https://open.spotify.com/album/", "").substring(0, 22);
		Album playlist;
		List<AudioTrack> tracks = new ArrayList<>();

		try {
			playlist = spotifyApi.getAlbum(id).build().execute();
		} catch (IOException | SpotifyWebApiException | ParseException e) {
			e.printStackTrace();

			return;
		}

		for (TrackSimplified track : playlist.getTracks().getItems()) {

			System.out.println("Searching for: " + track.getName() + " " + track.getArtists()[0].getName());

			TomatenMusic.getInstance().getAudioManager().getAudioPlayerManager().loadItem("ytsearch:" + track.getName() + " " + track.getArtists()[0].getName(), new AudioLoadResultHandler() {
				@Override
				public void trackLoaded(AudioTrack track) {
					tracks.add(track);

					if (playlist.getTracks().getItems().length <= tracks.size())
						musicManager.play(new BasicAudioPlaylist("Spotify search results for: " + link, tracks, tracks.get(0), true));
				}

				@Override
				public void playlistLoaded(AudioPlaylist audioPlaylist) {
					tracks.add(audioPlaylist.getTracks().get(0));

					if (playlist.getTracks().getItems().length <= tracks.size())
						musicManager.play(new BasicAudioPlaylist("Spotify search results for: " + link, tracks, tracks.get(0), true));
				}

				@Override
				public void noMatches() {
					System.out.println("No Spotify Matches");

				}

				@Override
				public void loadFailed(FriendlyException exception) {
					System.out.println(exception.severity + " : " + exception.getMessage());
					exception.printStackTrace();

				}
			});
		}
	}

	public void fetchAccessToken() {

		if (System.currentTimeMillis() - lastTokenFetch <= 60 * 60000)
			return;

		try {
			currentCredentials = spotifyApi.clientCredentials().build().execute();
			spotifyApi.setAccessToken(currentCredentials.getAccessToken());
			lastTokenFetch = System.currentTimeMillis();
			System.out.println("[Spotiy] Fetched access token successfully!");

		} catch (IOException | SpotifyWebApiException | ParseException e) {
			e.printStackTrace();
		}
	}


	public String getAccessToken() {
		return currentCredentials.getAccessToken();
	}

	public static SpotifyWrapper getInstance() {
		return instance;
	}
}
