package net.tomatentum.musicbot.music.messagemanagers;

import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.tomatentum.musicbot.TomatenMusic;
import net.tomatentum.musicbot.music.GuildMusicManager;
import net.tomatentum.musicbot.utils.PageManager;
import net.tomatentum.musicbot.utils.Selectable;
import net.tomatentum.musicbot.utils.SelectionPanel;
import net.tomatentum.musicbot.utils.Utils;

import java.util.List;

public class SearchOperation implements Selectable {
	private PageManager<AudioTrack> pageManager;
	private AudioPlaylist playlist;
	private GuildMusicManager musicManager;

	public SearchOperation(AudioPlaylist playlist, TextChannel channel, GuildMusicManager musicManager) {


		this.playlist = playlist;
		this.musicManager = musicManager;
		this.pageManager = new PageManager<>(playlist.getTracks(), 9);

		new SelectionPanel(channel, playlist.getName(), this);
	}
	@Override
	public String getPage(int page) {
		int count = 1;
		StringBuilder builder = new StringBuilder();
		List<AudioTrack> contents = pageManager.getPage(page);

		if (contents != null) {
			for (AudioTrack track : contents) {
				builder.append(count).append(": ").append(track.getInfo().title.equals("Unknown title") ? track.getIdentifier() : track.getInfo().title).append(" [").append(TomatenMusic.getTimestamp(track.getDuration())).append("]\n");
				count++;
			}
			return builder.toString();
		}else
			return null;
	}

	@Override
	public int getPageSize(int Page) {
		return pageManager.getPage(Page).size();
	}

	@Override
	public int getTotalPages() {
		return pageManager.getTotalPages();
	}

	@Override
	public void handleReaction(int item, int currentpage, Member member, ButtonClickEvent action) {
		List<AudioTrack> contents = pageManager.getPage(currentpage);
		if (!member.getVoiceState().inAudioChannel()) {
			return;
		}

		musicManager.connect(Utils.findSuitableVoiceChannel(member));

		try {
			musicManager.play(contents.get(item-1).makeClone());

		}catch (IndexOutOfBoundsException ignored) {
			ignored.printStackTrace();
		}
	}
}
