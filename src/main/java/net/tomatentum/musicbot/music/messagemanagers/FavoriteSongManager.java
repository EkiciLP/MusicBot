package net.tomatentum.musicbot.music.messagemanagers;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.TextChannel;
import net.tomatentum.musicbot.MusicBot;
import net.tomatentum.musicbot.music.GuildMusicManager;
import net.tomatentum.musicbot.utils.PageManager;
import net.tomatentum.musicbot.utils.Selectable;
import net.tomatentum.musicbot.utils.SelectionPanel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FavoriteSongManager implements Selectable {

	private final Member member;
	private final GuildMusicManager musicManager;
	private PageManager<AudioTrack> pageManager;

	public FavoriteSongManager(Member member, GuildMusicManager musicManager) {
		this.member = member;
		this.musicManager = musicManager;
		List<String> identifiers = MusicBot.getInstance().getConfiguration().getStringList("FavoriteSongs." + member.getIdLong());

		for (String identifier : identifiers) {
			musicManager.getAudioPlayerManager().loadItem("ytsearch:" + identifier, new AudioLoadResultHandler() {
				@Override
				public void trackLoaded(AudioTrack audioTrack) {
					add(audioTrack);
				}

				@Override
				public void playlistLoaded(AudioPlaylist audioPlaylist) {
					add(audioPlaylist.getTracks().get(0));
				}

				@Override
				public void noMatches() {

				}

				@Override
				public void loadFailed(FriendlyException e) {

				}
			});
		}
		this.pageManager = new PageManager<>(new ArrayList<>(), 9);

	}
	public void add(AudioTrack track) {
		System.out.println(track.getInfo().title);
		pageManager.addItem(track);
		List<String> identifiers = MusicBot.getInstance().getConfiguration().getStringList("FavoriteSongs." + member.getIdLong());
		if (!identifiers.contains(track.getIdentifier()))
			identifiers.add(track.getIdentifier());

		MusicBot.getInstance().getConfiguration().set("FavoriteSongs." + member.getIdLong(), identifiers);
		try {
			MusicBot.getInstance().getConfiguration().save(MusicBot.getInstance().getConfigFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void remove(AudioTrack track) {
		pageManager.removeItem(track);

		List<String> identifiers = MusicBot.getInstance().getConfiguration().getStringList("FavoriteSongs." + member.getIdLong());

		identifiers.remove(track.getIdentifier());

		MusicBot.getInstance().getConfiguration().set("FavoriteSongs." + member.getIdLong(), identifiers);
		try {
			MusicBot.getInstance().getConfiguration().save(MusicBot.getInstance().getConfigFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
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

	public void showPanel(TextChannel channel) {
		new SelectionPanel(channel, "Favorite Songs of: " + member.getEffectiveName(), this);
	}

	@Override
	public void handleReaction(MessageReaction reaction, int currentpage) {
		List<AudioTrack> contents = pageManager.getPage(currentpage);
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

	@Override
	public int getPageSize(int Page) {
		return pageManager.getPage(Page).size();
	}

	@Override
	public int getTotalPages() {
		return pageManager.getTotalPages();
	}

}
