package net.tomatentum.musicbot.favourites;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.tomatentum.musicbot.TomatenMusic;
import net.tomatentum.musicbot.music.GuildMusicManager;
import net.tomatentum.musicbot.utils.PageManager;
import net.tomatentum.musicbot.utils.Selectable;
import net.tomatentum.musicbot.utils.SelectionPanel;
import net.tomatentum.musicbot.utils.Utils;

import java.util.*;

public class FavoriteSongManager implements Selectable {

	private final User user;
	private final GuildMusicManager musicManager;
	private final PageManager<Database.DbTrack> pageManager;
	private final Database database;

	public FavoriteSongManager(User user, GuildMusicManager musicManager, Database database) {
		this.pageManager = new PageManager<>(new ArrayList<>(), 9);
		this.user = user;
		this.musicManager = musicManager;
		this.database = database;
		List<Database.DbTrack> tracks = database.getFavourites(user.userid);

		for (Database.DbTrack track : tracks) {
			add(track);
		}


	}

	/**
	 *
	 * @return if value was added successfully
	 */
	public boolean add(AudioTrackInfo trackInfo) {

		Database.DbTrack track = new Database.DbTrack(trackInfo.uri, trackInfo.title, trackInfo.length);

		pageManager.addItem(track);
		database.addFavourite(user.userid, track);

		return true;
	}

	public boolean add(Database.DbTrack trackInfo) {

		pageManager.addItem(trackInfo);

		return true;
	}

	public void remove(String trackURL) {
		Database.DbTrack toRemove = null;
		for (Database.DbTrack track : pageManager.getContents()) {
			if (track.getUrl().equals(trackURL)) {
				toRemove = track;
			}

		}

		if (toRemove == null)
			return;

		pageManager.removeItem(toRemove);

		database.removeFavourite(user.userid, trackURL);

	}
	public String getPage(int page) {

		int count = 1;
		StringBuilder builder = new StringBuilder();

		List<Database.DbTrack> tracks = pageManager.getPage(page);

		for (Database.DbTrack track : tracks) {
				builder.append(count).append(": ").append(track.getUrl().equals("Unknown title") ? track.getUrl() : track.getTitle() + " | [" + TomatenMusic.getTimestamp(track.getLength()) + "] \n");
				count++;
			}
			return builder.toString();
	}


	public void showPanel(TextChannel channel) {
		new SelectionPanel(channel, "Favorite Songs of: " + user.getMember().getEffectiveName(), this);
	}
	public void showPanel(Message replyTo) {
		new SelectionPanel(replyTo, "Favorite Songs of: " + user.getMember().getEffectiveName(), this);
	}

	@Override
	public void handleReaction(int item, int currentpage, Member member, ButtonClickEvent action) {
		List<Database.DbTrack> trackInfos = pageManager.getPage(currentpage);
		if (!member.getVoiceState().inAudioChannel()) {
			return;
		}


		Utils.checkSameChannel(member);
		musicManager.connect(Utils.findSuitableVoiceChannel(member));

		try {
			musicManager.searchPlay(trackInfos.get(item-1).getUrl());
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
