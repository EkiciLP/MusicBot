package net.tomatentum.musicbot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.tomatentum.musicbot.MusicBot;

import java.util.HashMap;

public class AudioManager {
	private MusicBot main;
	private HashMap<Long, GuildMusicManager> musicManagerHashMap;

	private AudioPlayerManager audioPlayerManager;

	public AudioManager(MusicBot main) {
		this.main = main;
		musicManagerHashMap = new HashMap<>();
	}

	public GuildMusicManager getMusicManager(Guild guild) {
		if (musicManagerHashMap.containsKey(guild.getIdLong())) {
			return musicManagerHashMap.get(guild.getIdLong());
		}else {
			musicManagerHashMap.put(guild.getIdLong(), new GuildMusicManager(audioPlayerManager, guild));
			return musicManagerHashMap.get(guild.getIdLong());
		}
	}
}
