package net.tomatentum.musicbot.utils;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class CommandManager extends ListenerAdapter {

	HashMap<String, GuildCommand> commands;

	public CommandManager() {
		commands = new HashMap<>();
	}

	@Override
	public void onSlashCommand(@NotNull SlashCommandEvent event) {
		if (commands.containsKey(event.getCommandString())) {
			commands.get(event.getCommandString()).execute(event);
		}
	}

}
