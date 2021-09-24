package net.tomatentum.musicbot.command.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.tomatentum.musicbot.TomatenMusic;
import net.tomatentum.musicbot.utils.GuildCommand;

import java.util.concurrent.TimeUnit;

public class SearchCommand implements GuildCommand {
	@Override
	public void execute(Member member, TextChannel channel, Message message, String[] args) {
		message.delete().queue();
		if (channel.getGuild().getAudioManager().isConnected() && channel.getGuild().getAudioManager().getConnectedChannel().getMembers().contains(member) || !channel.getGuild().getAudioManager().isConnected()) {

			if (args.length > 1) {
				StringBuilder URL = new StringBuilder();

				for (int i = 1; i < args.length; i++) {
					URL.append(args[i]).append(" ");
				}
				if (!channel.getGuild().getAudioManager().isConnected()) {
					TomatenMusic.getInstance().getAudioManager().getMusicManager(channel.getGuild()).connect(member.getVoiceState().getChannel());
				}
				channel.sendMessage("🔍 Searching for: ``" + URL.toString() + "``").complete().delete().queueAfter(5, TimeUnit.SECONDS);
				TomatenMusic.getInstance().getAudioManager().getMusicManager(channel.getGuild()).search(URL.toString(), channel);
			}
		}
	}
}
