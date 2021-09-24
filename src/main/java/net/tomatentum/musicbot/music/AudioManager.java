package net.tomatentum.musicbot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.tomatentum.musicbot.TomatenMusic;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class AudioManager {
	private TomatenMusic main;
	private HashMap<Long, GuildMusicManager> musicManagerHashMap;
	private HashMap<FavoriteSongManager.User, FavoriteSongManager> favoriteSongManagerHashMap;

	private AudioPlayerManager audioPlayerManager;

	public AudioManager(TomatenMusic main) {
		this.main = main;
		musicManagerHashMap = new HashMap<>();
		favoriteSongManagerHashMap = new HashMap<>();

		audioPlayerManager = new DefaultAudioPlayerManager();
		AudioSourceManagers.registerRemoteSources(audioPlayerManager);
		audioPlayerManager.registerSourceManager(new YoutubeAudioSourceManager());
		audioPlayerManager.registerSourceManager(new HttpAudioSourceManager());
		audioPlayerManager.registerSourceManager(new TwitchStreamAudioSourceManager());
		audioPlayerManager.registerSourceManager(new SoundCloudAudioSourceManager.Builder().withAllowSearch(true).build());

		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				for (Guild guild : TomatenMusic.getInstance().getBot().getGuilds()) {
					for (Member member : guild.getMembers()) {
						getFavoriteSongManager(member);
					}
				}
			}
		}, 5000, 10000);
	}

	public GuildMusicManager getMusicManager(Guild guild) {
		if (!musicManagerHashMap.containsKey(guild.getIdLong())) {
			musicManagerHashMap.put(guild.getIdLong(), new GuildMusicManager(audioPlayerManager, guild));
		}
		return musicManagerHashMap.get(guild.getIdLong());

	}
	public FavoriteSongManager getFavoriteSongManager(Member member) {
		if (!favoriteSongManagerHashMap.containsKey(new FavoriteSongManager.User(member))) {
			favoriteSongManagerHashMap.put(new FavoriteSongManager.User(member), new FavoriteSongManager(new FavoriteSongManager.User(member), getMusicManager(member.getGuild())));
		}
		return favoriteSongManagerHashMap.get(new FavoriteSongManager.User(member));

	}

	public AudioPlayerManager getAudioPlayerManager() {
		return audioPlayerManager;
	}
}
