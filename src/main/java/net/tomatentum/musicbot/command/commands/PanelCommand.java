package net.tomatentum.musicbot.command.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.tomatentum.musicbot.MusicBot;
import net.tomatentum.musicbot.command.utils.GuildCommand;
import net.tomatentum.musicbot.music.GuildMusicManager;

public class PanelCommand implements GuildCommand {
	@Override
	public void execute(Member member, TextChannel channel, Message message, String[] args) {
		if (member.hasPermission(Permission.MANAGE_CHANNEL)) {
			GuildMusicManager musicManager = MusicBot.getInstance().getAudioManager().getMusicManager(channel.getGuild());
			try {
				TextChannel oldchannel = MusicBot.getInstance().getBot().getTextChannelById(MusicBot.getInstance().getConfiguration().getLong("Panels." + musicManager.getGuild().getIdLong() + ".channelid"));
				Message oldmessage = oldchannel.retrieveMessageById(MusicBot.getInstance().getConfiguration().getLong("Panels." + musicManager.getGuild().getIdLong() + ".messageid")).complete();
				oldmessage.delete().queue();
			}catch (Exception e) {
				System.out.println("No Previous Panel Found for " + channel.getGuild().getName());
			}

			musicManager.getPanelManager().setPanel(channel);


		}
	}
}
