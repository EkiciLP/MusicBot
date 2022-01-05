package net.tomatentum.musicbot.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.tomatentum.musicbot.TomatenMusic;
import net.tomatentum.musicbot.exception.PanelException;
import net.tomatentum.musicbot.exception.ReturnException;
import net.tomatentum.musicbot.music.GuildMusicManager;

import java.util.ArrayList;
import java.util.List;

public class Utils {

	public static VoiceChannel findSuitableVoiceChannel(Member member) {
		VoiceChannel voiceChannel = (VoiceChannel) member.getVoiceState().getChannel();
		if (voiceChannel == null) {
			throw new PanelException("You are not in a Voice Channel", member.getGuild());
		}
		return voiceChannel;
	}



	public static boolean checkSameChannel(Member member) {
		if (!member.getGuild().getAudioManager().isConnected()) {
			return false;
		}
		if (!member.getVoiceState().getChannel().equals(member.getGuild().getAudioManager().getConnectedChannel())) {
			return false;
		}
		return true;
	}

	public static void checkPanelChannel(TextChannel channel) {
		if (TomatenMusic.getInstance().getAudioManager().getMusicManager(channel.getGuild()).getPanelManager().getChannel() == null ||
		channel.getIdLong() != TomatenMusic.getInstance().getAudioManager().getMusicManager(channel.getGuild()).getPanelManager().getChannel().getIdLong()) {
			throw new ReturnException();
		}
	}

	public static void broadcastMessage(String title, String text) {
		JDA bot = TomatenMusic.getInstance().getBot();
		List<GuildMusicManager> musicManagers = new ArrayList<>();
		User tueem = bot.getUserById(365497290053386242L);

		bot.getGuilds().forEach(guild -> musicManagers.add(TomatenMusic.getInstance().getAudioManager().getMusicManager(guild)));

		EmbedBuilder builder = new EmbedBuilder();

		builder.setAuthor(tueem.getName(), "https://tomatentum.net", tueem.getAvatarUrl());
		builder.setDescription(text);
		builder.setFooter(" - TomatenTum development - ");
		builder.setColor(0x2c2f33);
		builder.setTitle(title);

		for (GuildMusicManager musicManager : musicManagers) {
			Member owner = musicManager.getGuild().getOwner();
			TextChannel channel = musicManager.getGuild().getDefaultChannel();

			if (channel != null) {
				channel.sendMessage("Hey @here, I've got a new update! View the Patch Notes here:").setEmbeds(builder.build()).queue();
			}
		}
	}


}
