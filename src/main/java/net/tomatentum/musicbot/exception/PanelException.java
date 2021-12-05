package net.tomatentum.musicbot.exception;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.tomatentum.musicbot.TomatenMusic;

public class PanelException extends RuntimeException{

	private Guild guild;
	private static final long serialVersionUID = 1L;
	private String message;


	public PanelException(String message, Guild guild) {
		super("", null, true, false);
		this.guild = guild;
		this.message = message;

		sendPanelMessage();

	}

	public void sendPanelMessage() {
		TomatenMusic.getInstance().getAudioManager().getMusicManager(guild).getPanelManager().sendMessage("⛔ " + message);
	}


}
