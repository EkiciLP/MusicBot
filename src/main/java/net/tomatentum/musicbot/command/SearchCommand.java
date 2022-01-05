package net.tomatentum.musicbot.command;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.tomatentum.musicbot.TomatenMusic;
import net.tomatentum.musicbot.utils.GuildCommand;
import net.tomatentum.musicbot.utils.Utils;

import java.util.concurrent.TimeUnit;

public class SearchCommand extends GuildCommand {
	protected SearchCommand() {
		super("search", "Searches youtube for the provided Query",
				new OptionData(OptionType.STRING, "query", "The Query to search for").setRequired(true)
		);
	}

	@Override
	public void execute(SlashCommandEvent command) {
		Utils.checkSameChannel(command.getMember());


			String URL = command.getOption("query").getAsString();

			Utils.checkSameChannel(command.getMember());

			TomatenMusic.getInstance().getAudioManager().getMusicManager(command.getGuild()).connect(Utils.findSuitableVoiceChannel(command.getMember()));

			command.reply("🔍 Searching for: ``" + URL.toString() + "``").queue();


			TomatenMusic.getInstance().getAudioManager().getMusicManager(command.getGuild()).search(URL, command.getTextChannel());
	}
}
