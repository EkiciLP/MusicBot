package net.tomatentum.musicbot.command.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.tomatentum.musicbot.TomatenMusic;
import net.tomatentum.musicbot.music.FavoriteSongManager;
import net.tomatentum.musicbot.utils.GuildCommand;

import java.util.concurrent.TimeUnit;

public class FavoriteCommand implements GuildCommand {
	@Override
	public void execute(Member member, TextChannel channel, Message message, String[] args) {
		message.delete().queueAfter(1, TimeUnit.SECONDS);

		if (channel.getGuild().getAudioManager().isConnected() && channel.getGuild().getAudioManager().getConnectedChannel().getMembers().contains(member) || !channel.getGuild().getAudioManager().isConnected()) {
			if (!channel.getGuild().getAudioManager().isConnected()) {
				TomatenMusic.getInstance().getAudioManager().getMusicManager(channel.getGuild()).connect(member.getVoiceState().getChannel());
			}
			FavoriteSongManager favoriteSongManager = TomatenMusic.getInstance().getAudioManager().getFavoriteSongManager(member);

			favoriteSongManager.showPanel(channel);
		}
	}
}
