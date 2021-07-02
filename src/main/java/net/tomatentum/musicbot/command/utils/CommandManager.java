package net.tomatentum.musicbot.command.utils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CommandManager extends ListenerAdapter {

	private HashMap<String, GuildCommand> commands;

	public CommandManager() {
		commands = new HashMap<>();
	}


	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event) {
		if (event.getMessage().getContentDisplay().startsWith(",")) {
			String[] args = event.getMessage().getContentDisplay().split(" ");
				if (commands.containsKey(args[0].substring(1).toLowerCase())) {
					commands.get(args[1].substring(1).toLowerCase()).execute(event.getMember(), event.getTextChannel(), event.getMessage(), args);
				}

		}
	}

	public void registerCommand(String command, GuildCommand provider) {
		commands.put(command, provider);
	}
}
