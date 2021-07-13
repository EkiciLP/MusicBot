package net.tomatentum.musicbot.music.messagemanagers;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
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
	private final PageManager<String> pageManager;
	private final List<AudioTrackInfo> songInfos;

	public FavoriteSongManager(Member member, GuildMusicManager musicManager) {
		this.songInfos = new ArrayList<>();
		this.pageManager = new PageManager<>(new ArrayList<>(), 9);
		this.member = member;
		this.musicManager = musicManager;
		List<String> identifiers = MusicBot.getInstance().getConfiguration().getStringList("FavoriteSongs." + member.getIdLong());
		for (String identifier : identifiers) {
			add(identifier);

			musicManager.getAudioPlayerManager().loadItem("ytsearch:" + identifier, new AudioLoadResultHandler() {
				@Override
				public void trackLoaded(AudioTrack audioTrack) {
					songInfos.add(audioTrack.getInfo());
				}

				@Override
				public void playlistLoaded(AudioPlaylist audioPlaylist) {
					songInfos.add(audioPlaylist.getTracks().get(0).getInfo());
				}

				@Override
				public void noMatches() {

				}

				@Override
				public void loadFailed(FriendlyException e) {

				}
			});
		}


	}
	public void add(String identifier) {

		List<String> identifiers = MusicBot.getInstance().getConfiguration().getStringList("FavoriteSongs." + member.getIdLong());
		pageManager.addItem(identifier);
		if (!identifiers.contains(identifier)) {
			identifiers.add(identifier);
			System.out.println("added to fav");

		}
		MusicBot.getInstance().getConfiguration().set("FavoriteSongs." + member.getIdLong(), identifiers);
		try {
			MusicBot.getInstance().getConfiguration().save(MusicBot.getInstance().getConfigFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void remove(String identifier) {
		pageManager.removeItem(identifier);

		List<String> identifiers = MusicBot.getInstance().getConfiguration().getStringList("FavoriteSongs." + member.getIdLong());

		identifiers.remove(identifier);

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


			for (AudioTrackInfo track : songInfos) {
				builder.append(count).append(": ").append(track.title).append(" [").append(MusicBot.getTimestamp(track.length)).append("]\n");
				count++;
			}
			return builder.toString();
	}

	public void showPanel(TextChannel channel) {
		new SelectionPanel(channel, "Favorite Songs of: " + member.getEffectiveName(), this);
	}

	@Override
	public void handleReaction(MessageReaction reaction, int currentpage) {
		List<String> contents = pageManager.getPage(currentpage);

		try {
			switch (reaction.getReactionEmote().getEmoji()) {
				case "1️⃣":
					musicManager.loadAndQueue(contents.get(0));
					break;
				case "2️⃣":
					musicManager.loadAndQueue(contents.get(1));
					break;
				case "3️⃣":
					musicManager.loadAndQueue(contents.get(2));
					break;
				case "4️⃣":
					musicManager.loadAndQueue(contents.get(3));
					break;
				case "5️⃣":
					musicManager.loadAndQueue(contents.get(4));
					break;
				case "6️⃣":
					musicManager.loadAndQueue(contents.get(5));
					break;
				case "7️⃣":
					musicManager.loadAndQueue(contents.get(6));
					break;
				case "8️⃣":
					musicManager.loadAndQueue(contents.get(7));
					break;
				case "9️⃣":
					musicManager.loadAndQueue(contents.get(8));
					break;
			}
		}catch (IndexOutOfBoundsException ex) {
			ex.printStackTrace();
		}
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
