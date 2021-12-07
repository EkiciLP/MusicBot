package net.tomatentum.musicbot.command;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.tomatentum.musicbot.TomatenMusic;
import net.tomatentum.musicbot.utils.GuildCommand;
import net.tomatentum.musicbot.music.GuildMusicManager;

public class PanelCommand implements GuildCommand {
	@Override
	public void execute(Member member, TextChannel channel, Message message, String[] args) {
		message.delete().queue();
		if (member.hasPermission(Permission.MANAGE_CHANNEL)) {
			GuildMusicManager musicManager = TomatenMusic.getInstance().getAudioManager().getMusicManager(channel.getGuild());
			try {
				TextChannel oldchannel = TomatenMusic.getInstance().getBot().getTextChannelById(TomatenMusic.getInstance().getConfiguration().getLong("panels." + musicManager.getGuild().getIdLong() + ".channelid"));
				Message oldmessage = oldchannel.retrieveMessageById(TomatenMusic.getInstance().getConfiguration().getLong("panels." + musicManager.getGuild().getIdLong() + ".messageid")).complete();
				oldmessage.delete().queue();
			}catch (Exception e) {
				System.out.println("No Previous Panel Found for " + channel.getGuild().getName());
			}

			musicManager.getPanelManager().setPanel(channel);


		}
	}
}
