package net.tomatentum.musicbot.utils;

import javafx.scene.layout.Pane;
import net.dv8tion.jda.api.entities.*;
import net.tomatentum.musicbot.TomatenMusic;
import net.tomatentum.musicbot.exception.PanelException;
import net.tomatentum.musicbot.exception.ReturnException;

import java.util.concurrent.TimeUnit;

public class Utils {

	public static VoiceChannel findSuitableVoiceChannel(Member member) {
		VoiceChannel voiceChannel = member.getVoiceState().getChannel();
		if (voiceChannel == null) {
			throw new PanelException("You are not in a Voice Channel", member.getGuild());
		}
		return voiceChannel;
	}



	public static void checkSameChannel(Member member) {
		if (member.getGuild().getAudioManager().isConnected() &&
				!member.getVoiceState().getChannel().equals(member.getGuild().getAudioManager().getConnectedChannel())) {

			throw new PanelException("Bot already playing", member.getGuild());
		}
	}

	public static void checkPanelChannel(TextChannel channel) {
		if (channel.getIdLong() != TomatenMusic.getInstance().getAudioManager().getMusicManager(channel.getGuild()).getPanelManager().getChannel().getIdLong()) {
			throw new ReturnException();
		}
	}


}
