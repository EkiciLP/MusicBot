package net.tomatentum.musicbot.utils;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.tomatentum.musicbot.TomatenMusic;


public abstract class GuildCommand {

	private JDA bot = TomatenMusic.getInstance().getBot();
	protected Command command;

	protected GuildCommand(String name, String description, OptionData... options) {
		TomatenMusic.getInstance().getCmdmanager().commands.put(name, this);
		if (options == null)
			return;
		for (Guild guild : bot.getGuilds()) {
			command = guild.upsertCommand(name, description).addOptions(options).complete();
		}
	}
	public abstract void execute(SlashCommandEvent command);
}
