package net.tomatentum.musicbot.music;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.tomatentum.musicbot.MusicBot;

import java.util.concurrent.TimeUnit;

public class MessageReceivePlayHandler extends ListenerAdapter {
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (event.getMember().getUser().isBot() || event.getMessage().getContentDisplay().startsWith(",")) {
			return;
		}
		GuildMusicManager musicManager = MusicBot.getInstance().getAudioManager().getMusicManager(event.getGuild());
		if (event.getTextChannel().equals(musicManager.getPanelManager().getChannel())) {
			event.getMessage().delete().queue();

			if (event.getMember().getVoiceState().inVoiceChannel()) {
					musicManager.connect(event.getMember().getVoiceState().getChannel());
					musicManager.loadAndQueue(event.getMessage().getContentDisplay());
				}else
					event.getTextChannel().sendMessage("You are not in a Voice Channel!").complete().delete().queueAfter(5, TimeUnit.SECONDS);

		}
	}
}
