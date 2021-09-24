package net.tomatentum.musicbot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.TextChannel;
import net.tomatentum.musicbot.TomatenMusic;
import net.tomatentum.musicbot.utils.PageManager;
import net.tomatentum.musicbot.utils.Selectable;
import net.tomatentum.musicbot.utils.SelectionPanel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FavoriteSongManager implements Selectable {

	private final User user;
	private final GuildMusicManager musicManager;
	private final PageManager<AudioTrack> pageManager;

	public FavoriteSongManager(User user, GuildMusicManager musicManager) {
		this.pageManager = new PageManager<>(new ArrayList<>(), 9);
		this.user = user;
		this.musicManager = musicManager;
		List<String> identifiers = TomatenMusic.getInstance().getConfiguration().getStringList("FavoriteSongs." + user.getMember().getIdLong());
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
					System.out.println("No matches: " + identifier);
				}

				@Override
				public void loadFailed(FriendlyException e) {
					System.out.println(e.getMessage());
				}
			});
		}


	}
	public void add(AudioTrack track) {

		List<String> identifiers = TomatenMusic.getInstance().getConfiguration().getStringList("FavoriteSongs." + user.getMember().getIdLong());
		pageManager.addItem(track);
		if (!identifiers.contains(track.getInfo().uri)) {
			identifiers.add(track.getInfo().uri);

		}
		TomatenMusic.getInstance().getConfiguration().set("FavoriteSongs." + user.getMember().getIdLong(), identifiers);
		try {
			TomatenMusic.getInstance().getConfiguration().save(TomatenMusic.getInstance().getConfigFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void remove(AudioTrack track) {

		List<String> uris = new ArrayList<>();
		pageManager.getContents().forEach(itrack -> {
			if (track.getInfo().uri.equals(itrack.getInfo().uri))
				uris.add(itrack.getInfo().uri);
		});



			if (uris.contains(track.getInfo().uri))
				pageManager.removeItem(track);

		List<String> identifiers = TomatenMusic.getInstance().getConfiguration().getStringList("FavoriteSongs." + user.getMember().getIdLong());

		identifiers.remove(track.getInfo().uri);

		TomatenMusic.getInstance().getConfiguration().set("FavoriteSongs." + user.getMember().getIdLong(), identifiers);
		try {
			TomatenMusic.getInstance().getConfiguration().save(TomatenMusic.getInstance().getConfigFile());
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
				builder.append(count).append(": ").append(track.title.equals("Unknown title") ? track.identifier : track.title).append(" [").append(TomatenMusic.getTimestamp(track.length)).append("]\n");
				count++;
			}
			return builder.toString();
	}

	public void showPanel(TextChannel channel) {
		new SelectionPanel(channel, "Favorite Songs of: " + user.getMember().getEffectiveName(), this);
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

	public static class User {

		private final long userid;
		private final long guildid;

		public User(Member member) {
			this.userid = member.getIdLong();
			this.guildid = member.getGuild().getIdLong();
		}

		public Member getMember() {
			return TomatenMusic.getInstance().getBot().getGuildById(guildid).getMemberById(userid);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			User user = (User) o;
			return userid == user.userid && guildid == user.guildid;
		}

		@Override
		public int hashCode() {
			return Objects.hash(userid, guildid);
		}
	}

}
