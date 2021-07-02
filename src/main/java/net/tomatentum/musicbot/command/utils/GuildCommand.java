package net.tomatentum.musicbot.command.utils;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;


public interface GuildCommand {
	void execute(Member member, TextChannel channel, Message message, String[] args);
}
