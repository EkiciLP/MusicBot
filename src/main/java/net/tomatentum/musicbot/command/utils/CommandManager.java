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
import java.util.concurrent.TimeUnit;

public class CommandManager extends ListenerAdapter {

	private HashMap<String, GuildCommand> commands;

	public CommandManager() {
		commands = new HashMap<>();
	}

	public void registerCommand(String command, GuildCommand provider) {
		commands.put(command.toLowerCase(), provider);
	}

	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event) {
		if (event.getMessage().getContentDisplay().startsWith(",")) {
			String[] args = event.getMessage().getContentDisplay().split(" ");
				if (commands.containsKey(args[0].replace(",", "").toLowerCase())) {
					commands.get(args[0].replace(",", "").toLowerCase()).execute(event.getMember(), event.getTextChannel(), event.getMessage(), args);
				}else
					event.getTextChannel().sendMessage("Unknown Command: ``" + args[0] + "``").complete().delete().queueAfter(5, TimeUnit.SECONDS);

		}
	}

}
