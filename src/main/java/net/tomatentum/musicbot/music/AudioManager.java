package net.tomatentum.musicbot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.tomatentum.musicbot.MusicBot;
import net.tomatentum.musicbot.music.messagemanagers.FavoriteSongManager;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class AudioManager {
	private MusicBot main;
	private HashMap<Long, GuildMusicManager> musicManagerHashMap;
	private HashMap<Long, FavoriteSongManager> favoriteSongManagerHashMap;

	private AudioPlayerManager audioPlayerManager;

	public AudioManager(MusicBot main) {
		this.main = main;
		musicManagerHashMap = new HashMap<>();
		favoriteSongManagerHashMap = new HashMap<>();

		audioPlayerManager = new DefaultAudioPlayerManager();
		AudioSourceManagers.registerRemoteSources(audioPlayerManager);
		audioPlayerManager.registerSourceManager(new YoutubeAudioSourceManager());
		audioPlayerManager.registerSourceManager(new HttpAudioSourceManager());
		audioPlayerManager.registerSourceManager(new TwitchStreamAudioSourceManager());

		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				for (Guild guild : MusicBot.getInstance().getBot().getGuilds()) {
					for (Member member : guild.getMembers()) {
						getFavoriteSongManager(member, guild);
					}
				}
			}
		}, 10000, 10000);
	}

	public GuildMusicManager getMusicManager(Guild guild) {
		if (musicManagerHashMap.containsKey(guild.getIdLong())) {
			return musicManagerHashMap.get(guild.getIdLong());
		}else {
			musicManagerHashMap.put(guild.getIdLong(), new GuildMusicManager(audioPlayerManager, guild));
			return musicManagerHashMap.get(guild.getIdLong());
		}
	}
	public FavoriteSongManager getFavoriteSongManager(Member member, Guild guild) {
		if (favoriteSongManagerHashMap.containsKey(member.getIdLong())) {
			return favoriteSongManagerHashMap.get(member.getIdLong());
		}else {
			favoriteSongManagerHashMap.put(member.getIdLong(), new FavoriteSongManager(member, getMusicManager(guild)));
			return favoriteSongManagerHashMap.get(member.getIdLong());
		}

	}
}
