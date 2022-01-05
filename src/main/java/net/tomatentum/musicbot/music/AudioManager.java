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
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.tomatentum.musicbot.TomatenMusic;
import net.tomatentum.musicbot.favourites.Database;
import net.tomatentum.musicbot.favourites.FavoriteSongManager;
import org.jetbrains.annotations.NotNull;

import javax.xml.crypto.Data;
import java.util.*;

public class AudioManager extends ListenerAdapter {
	private TomatenMusic main;
	private HashMap<Long, GuildMusicManager> musicManagerHashMap;
	private HashMap<FavoriteSongManager.User, FavoriteSongManager> favoriteSongManagerHashMap;
	private Database database;
	private UpdateTimer updateTimer;

	private AudioPlayerManager audioPlayerManager;

	public AudioManager(TomatenMusic main) {
		this.main = main;
		musicManagerHashMap = new HashMap<>();
		favoriteSongManagerHashMap = new HashMap<>();
		this.database = new Database();

		audioPlayerManager = new DefaultAudioPlayerManager();
		AudioSourceManagers.registerRemoteSources(audioPlayerManager);
		audioPlayerManager.registerSourceManager(new YoutubeAudioSourceManager());
		audioPlayerManager.registerSourceManager(new HttpAudioSourceManager());
		TomatenMusic.getInstance().getBot().addEventListener(this);
		updateTimer = new UpdateTimer();


		for (Guild guild : TomatenMusic.getInstance().getBot().getGuilds()) {
			for (Member member : guild.getMembers()) {
				getFavoriteSongManager(member);
			}
		}
	}


	@Override
	public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
		getFavoriteSongManager(event.getMember());
	}

	public GuildMusicManager getMusicManager(Guild guild) {
		if (!musicManagerHashMap.containsKey(guild.getIdLong())) {
			musicManagerHashMap.put(guild.getIdLong(), new GuildMusicManager(audioPlayerManager, guild));
		}
		return musicManagerHashMap.get(guild.getIdLong());

	}
	public FavoriteSongManager getFavoriteSongManager(Member member) {
		if (!favoriteSongManagerHashMap.containsKey(new FavoriteSongManager.User(member))) {
			favoriteSongManagerHashMap.put(new FavoriteSongManager.User(member), new FavoriteSongManager(new FavoriteSongManager.User(member), getMusicManager(member.getGuild()), database));
		}
		return favoriteSongManagerHashMap.get(new FavoriteSongManager.User(member));

	}

	public AudioPlayerManager getAudioPlayerManager() {
		return audioPlayerManager;
	}

	public Collection<GuildMusicManager> getMusicManagers() {
		return musicManagerHashMap.values();
	}
}
