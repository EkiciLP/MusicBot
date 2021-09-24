package net.tomatentum.musicbot.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.tomatentum.musicbot.TomatenMusic;
import net.tomatentum.musicbot.music.GuildMusicManager;

import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;

public class SelectionPanel extends ListenerAdapter {
	private final GuildMusicManager musicManager;
	private final Selectable handle;
	private TextChannel channel;
	private EmbedBuilder builder;
	private Message message;
	private int currentPage;


	public SelectionPanel(TextChannel channel, String Title, Selectable handle) {
		currentPage = 1;
		handle.getPage(currentPage);

		TomatenMusic.getInstance().getBot().addEventListener(this);
		this.channel = channel;
		this.musicManager = TomatenMusic.getInstance().getAudioManager().getMusicManager(channel.getGuild());
		this.handle = handle;
		this.builder = new EmbedBuilder();

		this.builder.setTitle(Title);
		this.builder.setTimestamp(OffsetDateTime.now());

		this.message = channel.sendMessage(builder.build()).complete();
		showPage(currentPage);

		this.message.delete().queueAfter(1, TimeUnit.MINUTES);



	}

	public void showPage(int page) {

		if (handle.getTotalPages() == 0) {
			builder.setDescription("Favorites Empty");
			message = message.editMessage(builder.build()).complete();
			return;
		}
		currentPage = page;
		message.clearReactions().queue();
		builder.setDescription(handle.getPage(currentPage));
		builder.setFooter("- Select an entry by reacting with the correct number | Page: " + currentPage + " / " + handle.getTotalPages());


		message = message.editMessage(builder.build()).complete();

		if (handle.getTotalPages() == 1) {

		}else if (page == 1) {
			message.addReaction("⏩").queue();
		}else if (page == handle.getTotalPages()) {
			message.addReaction("⏪").queue();
		}else{
			message.addReaction("⏩").queue();
			message.addReaction("⏪").queue();
		}


		switch (handle.getPageSize(currentPage)) {
			case 1:
				message.addReaction("1️⃣").queue();
				break;
			case 2:
				message.addReaction("1️⃣").queue();
				message.addReaction("2️⃣").queue();
				break;
			case 3:
				message.addReaction("1️⃣").queue();
				message.addReaction("2️⃣").queue();
				message.addReaction("3️⃣").queue();
				break;
			case 4:
				message.addReaction("1️⃣").queue();
				message.addReaction("2️⃣").queue();
				message.addReaction("3️⃣").queue();
				message.addReaction("4️⃣").queue();
				break;
			case 5:
				message.addReaction("1️⃣").queue();
				message.addReaction("2️⃣").queue();
				message.addReaction("3️⃣").queue();
				message.addReaction("4️⃣").queue();
				message.addReaction("5️⃣").queue();
				break;
			case 6:
				message.addReaction("1️⃣").queue();
				message.addReaction("2️⃣").queue();
				message.addReaction("3️⃣").queue();
				message.addReaction("4️⃣").queue();
				message.addReaction("5️⃣").queue();
				message.addReaction("6️⃣").queue();
				break;
			case 7:
				message.addReaction("1️⃣").queue();
				message.addReaction("2️⃣").queue();
				message.addReaction("3️⃣").queue();
				message.addReaction("4️⃣").queue();
				message.addReaction("5️⃣").queue();
				message.addReaction("6️⃣").queue();
				message.addReaction("7️⃣").queue();
				break;
			case 8:
				message.addReaction("1️⃣").queue();
				message.addReaction("2️⃣").queue();
				message.addReaction("3️⃣").queue();
				message.addReaction("4️⃣").queue();
				message.addReaction("5️⃣").queue();
				message.addReaction("6️⃣").queue();
				message.addReaction("7️⃣").queue();
				message.addReaction("8️⃣").queue();
				break;
			default:
				message.addReaction("1️⃣").queue();
				message.addReaction("2️⃣").queue();
				message.addReaction("3️⃣").queue();
				message.addReaction("4️⃣").queue();
				message.addReaction("5️⃣").queue();
				message.addReaction("6️⃣").queue();
				message.addReaction("7️⃣").queue();
				message.addReaction("8️⃣").queue();
				message.addReaction("9️⃣").queue();
				break;
		}
	}

	@Override
	public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
		if (event.getUser().isBot()) {
			return;
		}
			if (event.getMessageIdLong() == message.getIdLong()) {
				event.getReaction().removeReaction(event.getUser()).queue();
				handle.handleReaction(event.getReaction(), currentPage, event.getMember());
				switch (event.getReaction().getReactionEmote().getEmoji()) {
					case "⏪":
						showPage(currentPage-1);
						break;
					case "⏩":
						showPage(currentPage+1);
						break;
				}
			}
	}


}
