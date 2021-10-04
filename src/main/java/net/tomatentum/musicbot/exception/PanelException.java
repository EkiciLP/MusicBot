package net.tomatentum.musicbot.exception;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.tomatentum.musicbot.TomatenMusic;

public class PanelException extends RuntimeException{

	private Guild guild;

	public PanelException(String message, Guild guild) {
		super(message);
		this.guild = guild;

		sendPanelMessage();

	}

	@Override
	public void printStackTrace() {
	}

	public void sendPanelMessage() {
		TomatenMusic.getInstance().getAudioManager().getMusicManager(guild).getPanelManager().sendMessage("⛔ " + getMessage());
	}


}
