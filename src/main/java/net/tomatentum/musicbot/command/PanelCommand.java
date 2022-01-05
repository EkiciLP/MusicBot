package net.tomatentum.musicbot.command;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.tomatentum.musicbot.TomatenMusic;
import net.tomatentum.musicbot.utils.GuildCommand;
import net.tomatentum.musicbot.music.GuildMusicManager;

public class PanelCommand extends GuildCommand {
	protected PanelCommand() {
		super("panel", "Spawn the Panel in the current channel", null);
	}

	@Override
	public void execute(SlashCommandEvent command) {
		if (command.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {

			GuildMusicManager musicManager = TomatenMusic.getInstance().getAudioManager().getMusicManager(command.getGuild());
			try {
				TextChannel oldchannel = TomatenMusic.getInstance().getBot().getTextChannelById(TomatenMusic.getInstance().getConfiguration().getLong("panels." + musicManager.getGuild().getIdLong() + ".channelid"));
				Message oldmessage = oldchannel.retrieveMessageById(TomatenMusic.getInstance().getConfiguration().getLong("panels." + musicManager.getGuild().getIdLong() + ".messageid")).complete();
				oldmessage.delete().queue();
			}catch (Exception e) {
				System.out.println("No Previous Panel Found for " + command.getGuild().getName());
			}

			musicManager.getPanelManager().setPanel(command.getTextChannel());


		}
	}
}
