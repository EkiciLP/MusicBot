package net.tomatentum.musicbot.music;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.tomatentum.musicbot.MusicBot;

import java.util.concurrent.TimeUnit;

public class MessageReceivePlayHandler extends ListenerAdapter {
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		GuildMusicManager musicManager = MusicBot.getInstance().getAudioManager().getMusicManager(event.getGuild());


		if (event.getMember().getUser().isBot() || event.getMessage().getContentDisplay().startsWith(",")) {
			return;
		}



		if (event.getTextChannel().getIdLong() != musicManager.getPanelManager().getMessage().getTextChannel().getIdLong())
			return;


		if (musicManager.getGuild().getAudioManager().isConnected() &&
				!event.getMember().getVoiceState().getChannel().equals(musicManager.getGuild().getAudioManager().getConnectedChannel())
		) {
			event.getMessage().delete().queue();
			event.getChannel().sendMessage("🛑 Bot already playing").complete().delete().queueAfter(5, TimeUnit.SECONDS);

			return;
		}
			event.getMessage().delete().queueAfter(1, TimeUnit.SECONDS);

			if (event.getMember().getVoiceState().inVoiceChannel()) {

				if (event.getMessage().getAttachments().size() > 0) {
					musicManager.connect(event.getMember().getVoiceState().getChannel());
					musicManager.play(event.getMessage().getAttachments().get(0).getUrl());
					return;
				}

					musicManager.connect(event.getMember().getVoiceState().getChannel());
					musicManager.play(event.getMessage().getContentDisplay());
				}else
					event.getTextChannel().sendMessage("You are not in a Voice Channel!").complete().delete().queueAfter(5, TimeUnit.SECONDS);

	}
}
