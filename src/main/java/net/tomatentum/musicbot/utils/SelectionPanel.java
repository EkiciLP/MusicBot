package net.tomatentum.musicbot.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
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

		TomatenMusic.getInstance().getBot().addEventListener(this);
		this.channel = channel;
		this.musicManager = TomatenMusic.getInstance().getAudioManager().getMusicManager(channel.getGuild());
		this.handle = handle;
		this.builder = new EmbedBuilder();

		this.builder.setTitle(Title);
		this.builder.setTimestamp(OffsetDateTime.now());
		builder.setColor(0x2C2F33);

		this.message = channel.sendMessage(getPage(currentPage)).complete();


		this.message.delete().queueAfter(1, TimeUnit.MINUTES);



	}

	public Message getPage(int page) {

		if (handle.getTotalPages() == 0) {
			builder.setDescription("Results Empty");
			return new MessageBuilder().setEmbeds(builder.build()).build();
		}
		currentPage = page;
		builder.setDescription(handle.getPage(currentPage));
		builder.setFooter("- Select an entry by reacting with the correct number | Page: " + currentPage + " / " + handle.getTotalPages());

		ActionRow[] actionRows;
		switch (handle.getPageSize(currentPage)) {
			case 1:
				actionRows = new ActionRow[]{
						ActionRow.of(
								Button.primary("1", "1️⃣")
						),
						ActionRow.of(
								Button.secondary("next", "⏩").withDisabled(handle.getTotalPages() <= currentPage),
								Button.secondary("previous", "⏪").withDisabled(currentPage - 1 <= 0)
						)
				};
				break;
			case 2:
				actionRows = new ActionRow[]{
						ActionRow.of(
								Button.primary("1", "1️⃣"),
								Button.primary("2", "2️⃣")
						),
						ActionRow.of(
								Button.secondary("next", "⏩").withDisabled(handle.getTotalPages() <= currentPage),
								Button.secondary("previous", "⏪").withDisabled(currentPage - 1 <= 0)
						)
				};
				break;
			case 3:
				actionRows = new ActionRow[]{
						ActionRow.of(
								Button.primary("1", "1️⃣"),
								Button.primary("2", "2️⃣"),
								Button.primary("3", "3️⃣")
						),
						ActionRow.of(
								Button.secondary("next", "⏩").withDisabled(handle.getTotalPages() <= currentPage),
								Button.secondary("previous", "⏪").withDisabled(currentPage - 1 <= 0)
						)
				};
				break;
			case 4:
				actionRows = new ActionRow[]{
						ActionRow.of(
								Button.primary("1", "1️⃣"),
								Button.primary("2", "2️⃣"),
								Button.primary("3", "3️⃣"),
								Button.primary("4", "4️⃣")
						),
						ActionRow.of(
								Button.secondary("next", "⏩").withDisabled(handle.getTotalPages() <= currentPage),
								Button.secondary("previous", "⏪").withDisabled(currentPage - 1 <= 0)
						)
				};
				break;
			case 5:
				actionRows = new ActionRow[]{
						ActionRow.of(
								Button.primary("1", "1️⃣"),
								Button.primary("2", "2️⃣"),
								Button.primary("3", "3️⃣"),
								Button.primary("4", "4️⃣"),
								Button.primary("5", "5️⃣")
						),
						ActionRow.of(
								Button.secondary("next", "⏩").withDisabled(handle.getTotalPages() <= currentPage),
								Button.secondary("previous", "⏪").withDisabled(currentPage - 1 <= 0)
						)
				};
				break;
			case 6:
				actionRows = new ActionRow[]{
						ActionRow.of(
								Button.primary("1", "1️⃣"),
								Button.primary("2", "2️⃣"),
								Button.primary("3", "3️⃣"),
								Button.primary("4", "4️⃣"),
								Button.primary("5", "5️⃣"),
								Button.primary("6", "6️⃣")
						),
						ActionRow.of(
								Button.secondary("next", "⏩").withDisabled(handle.getTotalPages() <= currentPage),
								Button.secondary("previous", "⏪").withDisabled(currentPage - 1 <= 0)
						)
				};
				break;
			case 7:
				actionRows = new ActionRow[]{
						ActionRow.of(
								Button.primary("1", "1️⃣"),
								Button.primary("2", "2️⃣"),
								Button.primary("3", "3️⃣"),
								Button.primary("4", "4️⃣"),
								Button.primary("5", "5️⃣"),
								Button.primary("6", "6️⃣"),
								Button.primary("7", "7️⃣")
						),
						ActionRow.of(
								Button.secondary("next", "⏩").withDisabled(handle.getTotalPages() <= currentPage),
								Button.secondary("previous", "⏪").withDisabled(currentPage - 1 <= 0)
						)
				};
				break;
			case 8:
				actionRows = new ActionRow[]{
						ActionRow.of(
								Button.primary("1", "1️⃣"),
								Button.primary("2", "2️⃣"),
								Button.primary("3", "3️⃣"),
								Button.primary("4", "4️⃣"),
								Button.primary("5", "5️⃣"),
								Button.primary("6", "6️⃣"),
								Button.primary("7", "7️⃣"),
								Button.primary("8", "8️⃣")
						),
						ActionRow.of(
								Button.secondary("next", "⏩").withDisabled(handle.getTotalPages() <= currentPage),
								Button.secondary("previous", "⏪").withDisabled(currentPage - 1 <= 0)
						)
				};
				break;
			default:

				actionRows = new ActionRow[]{
						ActionRow.of(
								Button.primary("1", "1️⃣"),
								Button.primary("2", "2️⃣"),
								Button.primary("3", "3️⃣"),
								Button.primary("4", "4️⃣"),
								Button.primary("5", "5️⃣"),
								Button.primary("6", "6️⃣"),
								Button.primary("7", "7️⃣"),
								Button.primary("8", "8️⃣"),
								Button.primary("9", "9️⃣")
						),
						ActionRow.of(
								Button.secondary("next", "⏩").withDisabled(handle.getTotalPages() <= currentPage),
								Button.secondary("previous", "⏪").withDisabled(currentPage - 1 != 0)
						)
				};
				break;
		}

		return new MessageBuilder().setEmbeds(builder.build()).setActionRows(actionRows).build();
	}

	@Override
	public void onButtonClick(ButtonClickEvent event) {
		if (event.getUser().isBot()) {
			return;
		}
			if (event.getMessageIdLong() == message.getIdLong()) {
				switch (event.getComponentId()) {
					case "previous":
						event.editMessage(getPage(currentPage-1)).queue(interactionHook -> interactionHook.getInteraction().getMessageChannel().retrieveMessageById(message.getIdLong()).queue(message -> this.message = message));
						break;
					case "next":
						event.editMessage(getPage(currentPage+1)).queue(interactionHook -> interactionHook.getInteraction().getMessageChannel().retrieveMessageById(message.getIdLong()).queue(message -> this.message = message));
						break;
					default:
						event.reply("").queue();
						handle.handleReaction(Integer.parseInt(event.getComponentId()), currentPage, event.getMember(), event);
						break;

				}
			}
	}


}
