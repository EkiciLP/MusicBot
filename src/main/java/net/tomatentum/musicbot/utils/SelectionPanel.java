package net.tomatentum.musicbot.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
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
			message = message.editMessageEmbeds(builder.build()).complete();
			return;
		}
		currentPage = page;
		message.clearReactions().queue();
		builder.setDescription(handle.getPage(currentPage));
		builder.setFooter("- Select an entry by reacting with the correct number | Page: " + currentPage + " / " + handle.getTotalPages());


		switch (handle.getPageSize(currentPage)) {
			case 1:
				message.editMessageEmbeds(builder.build()).setActionRows(ActionRow.of(
						Button.primary("1", "1️⃣")
				),
						ActionRow.of(
								Button.primary("next", "⏩").withDisabled(handle.getTotalPages() > currentPage),
								Button.primary("previous", "⏪").withDisabled(currentPage-1 != 0)
						)
						).queue();
				break;
			case 2:
				message.editMessageEmbeds(builder.build()).setActionRows(ActionRow.of(
						Button.primary("1", "1️⃣"),
						Button.primary("1", "2️⃣")
				),
						ActionRow.of(
								Button.primary("next", "⏩").withDisabled(handle.getTotalPages() > currentPage),
								Button.primary("previous", "⏪").withDisabled(currentPage-1 != 0)
						)
				).queue();
				break;
			case 3:
				message.editMessageEmbeds(builder.build()).setActionRows(ActionRow.of(
						Button.primary("1", "1️⃣"),
						Button.primary("1", "2️⃣"),
						Button.primary("1", "3️⃣")
				),
						ActionRow.of(
								Button.primary("next", "⏩").withDisabled(handle.getTotalPages() > currentPage),
								Button.primary("previous", "⏪").withDisabled(currentPage-1 != 0)
						)
				).queue();
				break;
			case 4:
				message.editMessageEmbeds(builder.build()).setActionRows(ActionRow.of(
						Button.primary("1", "1️⃣"),
						Button.primary("1", "2️⃣"),
						Button.primary("1", "3️⃣"),
						Button.primary("1", "4️⃣")
				),
						ActionRow.of(
								Button.primary("next", "⏩").withDisabled(handle.getTotalPages() > currentPage),
								Button.primary("previous", "⏪").withDisabled(currentPage-1 != 0)
						)
				).queue();
				break;
			case 5:
				message.editMessageEmbeds(builder.build()).setActionRows(ActionRow.of(
						Button.primary("1", "1️⃣"),
						Button.primary("1", "2️⃣"),
						Button.primary("1", "3️⃣"),
						Button.primary("1", "4️⃣"),
						Button.primary("1", "5️⃣")
				),
						ActionRow.of(
								Button.primary("next", "⏩").withDisabled(handle.getTotalPages() > currentPage),
								Button.primary("previous", "⏪").withDisabled(currentPage-1 != 0)
						)
				).queue();
				break;
			case 6:
				message.editMessageEmbeds(builder.build()).setActionRows(ActionRow.of(
						Button.primary("1", "1️⃣"),
						Button.primary("1", "2️⃣"),
						Button.primary("1", "3️⃣"),
						Button.primary("1", "4️⃣"),
						Button.primary("1", "5️⃣"),
						Button.primary("1", "6️⃣")
				),
						ActionRow.of(
								Button.primary("next", "⏩").withDisabled(handle.getTotalPages() > currentPage),
								Button.primary("previous", "⏪").withDisabled(currentPage-1 != 0)
						)
				).queue();
				break;
			case 7:
				message.editMessageEmbeds(builder.build()).setActionRows(ActionRow.of(
						Button.primary("1", "1️⃣"),
						Button.primary("1", "2️⃣"),
						Button.primary("1", "3️⃣"),
						Button.primary("1", "4️⃣"),
						Button.primary("1", "5️⃣"),
						Button.primary("1", "6️⃣"),
						Button.primary("1", "7️⃣")
				),
						ActionRow.of(
								Button.primary("next", "⏩").withDisabled(handle.getTotalPages() > currentPage),
								Button.primary("previous", "⏪").withDisabled(currentPage-1 != 0)
						)
				).queue();
				break;
			case 8:
				message.editMessageEmbeds(builder.build()).setActionRows(ActionRow.of(
						Button.primary("1", "1️⃣"),
						Button.primary("1", "2️⃣"),
						Button.primary("1", "3️⃣"),
						Button.primary("1", "4️⃣"),
						Button.primary("1", "5️⃣"),
						Button.primary("1", "6️⃣"),
						Button.primary("1", "7️⃣"),
						Button.primary("1", "8️⃣")
						),
						ActionRow.of(
								Button.primary("next", "⏩").withDisabled(handle.getTotalPages() > currentPage),
								Button.primary("previous", "⏪").withDisabled(currentPage-1 != 0)
						)
				).queue();
				break;
			default:

				message.editMessageEmbeds(builder.build()).setActionRows(ActionRow.of(
						Button.primary("1", "1️⃣"),
						Button.primary("1", "2️⃣"),
						Button.primary("1", "3️⃣"),
						Button.primary("1", "4️⃣"),
						Button.primary("1", "5️⃣"),
						Button.primary("1", "6️⃣"),
						Button.primary("1", "7️⃣"),
						Button.primary("1", "8️⃣"),
						Button.primary("1", "9️⃣")
				),
						ActionRow.of(
								Button.primary("next", "⏩").withDisabled(handle.getTotalPages() > currentPage),
								Button.primary("previous", "⏪").withDisabled(currentPage-1 != 0)
						)
				).queue();
				break;
		}
	}

	@Override
	public void onButtonClick(ButtonClickEvent event) {
		if (event.getUser().isBot()) {
			return;
		}
			if (event.getMessageIdLong() == message.getIdLong()) {
				handle.handleReaction(Integer.parseInt(event.getComponentId()), currentPage, event.getMember(), event);
				switch (event.getComponentId()) {
					case "previous":
						showPage(currentPage-1);
						break;
					case "next":
						showPage(currentPage+1);
						break;
				}
			}
	}


}
