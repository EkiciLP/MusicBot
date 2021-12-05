package net.tomatentum.musicbot.favourites;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.tomatentum.musicbot.TomatenMusic;
import net.tomatentum.musicbot.music.GuildMusicManager;
import net.tomatentum.musicbot.utils.PageManager;
import net.tomatentum.musicbot.utils.Selectable;
import net.tomatentum.musicbot.utils.SelectionPanel;
import net.tomatentum.musicbot.utils.Utils;
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection;

import java.io.IOException;
import java.util.*;

public class FavoriteSongManager implements Selectable {

	private final User user;
	private final GuildMusicManager musicManager;
	private final PageManager<String> pageManager;
	private final Map<String, String> URLNameMap;

	public FavoriteSongManager(User user, GuildMusicManager musicManager) {
		this.pageManager = new PageManager<>(new ArrayList<>(), 9);
		this.URLNameMap = new HashMap<>();
		this.user = user;
		this.musicManager = musicManager;
		ConfigurationSection section = TomatenMusic.getInstance().getConfiguration().getConfigurationSection("FavoriteSongs." + user.getMember().getIdLong());
		if (section == null)
			return;
		for (String name : section.getKeys(false)) {
			add(name, section.getString(name));
		}


	}
	public void add(String name, String trackURL) {

		ConfigurationSection section = TomatenMusic.getInstance().getConfiguration().getConfigurationSection(String.format("FavoriteSongs.%s", user.getMember().getIdLong()));

		if (section.getValues(false).values().contains(trackURL)) {
			throw new IllegalArgumentException("Favorites already contain the track");
		}
		pageManager.addItem(trackURL);
		URLNameMap.put(trackURL, name);
		TomatenMusic.getInstance().getConfiguration().set(String.format("FavoriteSongs.%s.%s", user.getMember().getIdLong(), name), trackURL);
		try {
			TomatenMusic.getInstance().getConfiguration().save(TomatenMusic.getInstance().getConfigFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void remove(String trackURL) {
		pageManager.removeItem(trackURL);
		URLNameMap.remove(trackURL);

		ConfigurationSection section = TomatenMusic.getInstance().getConfiguration().getConfigurationSection(String.format("FavoriteSongs.%s", user.getMember().getIdLong()));

		Map<String, Object> values = section.getValues(false);

		for (Map.Entry<String, Object> entry : values.entrySet()) {
			if (entry.getValue().equals(trackURL)) {
				values.remove(entry.getKey());
				return;
			}

		}

		TomatenMusic.getInstance().getConfiguration().set(String.format("FavoriteSongs.%s", user.getMember().getIdLong()), section);

		try {
			TomatenMusic.getInstance().getConfiguration().save(TomatenMusic.getInstance().getConfigFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public String getPage(int page) {
		int count = 1;
		StringBuilder builder = new StringBuilder();
		ConfigurationSection section = TomatenMusic.getInstance().getConfiguration().getConfigurationSection(String.format("FavoriteSongs.%s", user.getMember().getIdLong()));
		Collection<Object> trackURLs = section.getValues(false).values();



		for (Object URL : trackURLs) {
				builder.append(count).append(": ").append(URLNameMap.get((String) URL).equals("Unknown title") ? (String) URL : URLNameMap.get((String) URL));
				count++;
			}
			return builder.toString();
	}

	public void showPanel(TextChannel channel) {
		new SelectionPanel(channel, "Favorite Songs of: " + user.getMember().getEffectiveName(), this);
	}

	@Override
	public void handleReaction(int item, int currentpage, Member member, ButtonClickEvent action) {
		List<String> trackUrls = pageManager.getPage(currentpage);
		if (!member.getVoiceState().inVoiceChannel()) {
			return;
		}


		Utils.checkSameChannel(member);
		musicManager.connect(Utils.findSuitableVoiceChannel(member));

		try {
			musicManager.searchPlay(trackUrls.get(item));
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
