package net.tomatentum.musicbot.music.messagemanagers;

import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.TextChannel;
import net.tomatentum.musicbot.MusicBot;
import net.tomatentum.musicbot.music.GuildMusicManager;
import net.tomatentum.musicbot.utils.PageManager;
import net.tomatentum.musicbot.utils.Selectable;
import net.tomatentum.musicbot.utils.SelectionPanel;

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
				builder.append(count).append(": ").append(track.getInfo().title).append(" [").append(MusicBot.getTimestamp(track.getDuration())).append("]\n");
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
	public void handleReaction(MessageReaction reaction, int currentpage, Member member) {
		List<AudioTrack> contents = pageManager.getPage(currentpage);
		if (!member.getVoiceState().inVoiceChannel()) {
			return;
		}

		musicManager.connect(member.getVoiceState().getChannel());


		try {
			switch (reaction.getReactionEmote().getEmoji()) {
				case "1️⃣":
					musicManager.play(contents.get(0));
					break;
				case "2️⃣":
					musicManager.play(contents.get(1));
					break;
				case "3️⃣":
					musicManager.play(contents.get(2));
					break;
				case "4️⃣":
					musicManager.play(contents.get(3));
					break;
				case "5️⃣":
					musicManager.play(contents.get(4));
					break;
				case "6️⃣":
					musicManager.play(contents.get(5));
					break;
				case "7️⃣":
					musicManager.play(contents.get(6));
					break;
				case "8️⃣":
					musicManager.play(contents.get(7));
					break;
				case "9️⃣":
					musicManager.play(contents.get(8));
					break;
			}
		}catch (IndexOutOfBoundsException ignored) {}
	}
}
