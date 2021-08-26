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
	private final PageManager<AudioTrack> pageManager;

	public FavoriteSongManager(Member member, GuildMusicManager musicManager) {
		this.pageManager = new PageManager<>(new ArrayList<>(), 9);
		this.member = member;
		this.musicManager = musicManager;
		List<String> identifiers = MusicBot.getInstance().getConfiguration().getStringList("FavoriteSongs." + member.getIdLong());
		for (String identifier : identifiers) {

			musicManager.getAudioPlayerManager().loadItem(identifier, new AudioLoadResultHandler() {
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


	}
	public void add(AudioTrack track) {

		List<String> identifiers = MusicBot.getInstance().getConfiguration().getStringList("FavoriteSongs." + member.getIdLong());
		pageManager.addItem(track);
		if (!identifiers.contains(track.getIdentifier())) {
			identifiers.add(track.getInfo().uri);

		}
		MusicBot.getInstance().getConfiguration().set("FavoriteSongs." + member.getIdLong(), identifiers);
		try {
			MusicBot.getInstance().getConfiguration().save(MusicBot.getInstance().getConfigFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void remove(AudioTrack track) {
		for (AudioTrack itrack : pageManager.getContents()) {
			if (itrack.getIdentifier().equals(track.getIdentifier())) {
				pageManager.removeItem(itrack);
			}
		}

		List<String> identifiers = MusicBot.getInstance().getConfiguration().getStringList("FavoriteSongs." + member.getIdLong());

		identifiers.remove(track.getInfo().uri);

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
		List<AudioTrackInfo> songInfos = new ArrayList<>();

		for (AudioTrack track : pageManager.getPage(page)) {
			songInfos.add(track.getInfo());
		}


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
		List<AudioTrack> contents = pageManager.getPage(currentpage);

		try {
			switch (reaction.getReactionEmote().getEmoji()) {
				case "1️⃣":
					musicManager.play(contents.get(0).makeClone());
					break;
				case "2️⃣":
					musicManager.play(contents.get(1).makeClone());
					break;
				case "3️⃣":
					musicManager.play(contents.get(2).makeClone());
					break;
				case "4️⃣":
					musicManager.play(contents.get(3).makeClone());
					break;
				case "5️⃣":
					musicManager.play(contents.get(4).makeClone());
					break;
				case "6️⃣":
					musicManager.play(contents.get(5).makeClone());
					break;
				case "7️⃣":
					musicManager.play(contents.get(6).makeClone());
					break;
				case "8️⃣":
					musicManager.play(contents.get(7).makeClone());
					break;
				case "9️⃣":
					musicManager.play(contents.get(8).makeClone());
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
