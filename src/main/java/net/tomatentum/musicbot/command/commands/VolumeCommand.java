package net.tomatentum.musicbot.command.commands;

import jdk.internal.org.objectweb.asm.tree.TryCatchBlockNode;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.tomatentum.musicbot.MusicBot;
import net.tomatentum.musicbot.command.utils.GuildCommand;
import net.tomatentum.musicbot.music.GuildMusicManager;

import java.util.concurrent.TimeUnit;

public class VolumeCommand implements GuildCommand {
	@Override
	public void execute(Member member, TextChannel channel, Message message, String[] args) {
		GuildMusicManager musicManager = MusicBot.getInstance().getAudioManager().getMusicManager(channel.getGuild());
		if (channel.getGuild().getAudioManager().isConnected() && channel.getGuild().getAudioManager().getConnectedChannel().getMembers().contains(member)) {
			int oldvolume = musicManager.getPlayer().getVolume();
			int newvolume;
			try {
				newvolume = Integer.max(100, Integer.min(1, Integer.parseInt(args[1])));

			}catch (NumberFormatException e) {
				channel.sendMessage("Please use valid numbers!").complete().delete().queueAfter(5, TimeUnit.SECONDS);
				return;
			}

			musicManager.getPlayer().setVolume(newvolume);
			channel.sendMessage("🔊 Volume has been changed from ``" + oldvolume + "`` to ``" + newvolume + "``!").complete().delete().queueAfter(5, TimeUnit.SECONDS);
		}
	}
}
