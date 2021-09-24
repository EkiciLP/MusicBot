package net.tomatentum.musicbot.music;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.tomatentum.musicbot.TomatenMusic;
import net.tomatentum.musicbot.spotify.SpotifyWrapper;

import java.util.concurrent.TimeUnit;

public class MessageReceivePlayHandler extends ListenerAdapter {
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		GuildMusicManager musicManager = TomatenMusic.getInstance().getAudioManager().getMusicManager(event.getGuild());


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
		musicManager.getPanelManager().setLoading();

			event.getMessage().delete().queueAfter(1, TimeUnit.SECONDS);

			if (event.getMember().getVoiceState().inVoiceChannel()) {


				if (event.getMessage().getContentRaw().startsWith("https://open.spotify.com/track")) {
					musicManager.connect(event.getMember().getVoiceState().getChannel());
					SpotifyWrapper.getInstance().playTrack(event.getMessage().getContentDisplay(), musicManager);

					return;
				}else if (event.getMessage().getContentRaw().startsWith("https://open.spotify.com/playlist")) {
					musicManager.connect(event.getMember().getVoiceState().getChannel());
					SpotifyWrapper.getInstance().playPlaylist(event.getMessage().getContentDisplay(), musicManager);

					return;
				}else if (event.getMessage().getContentRaw().startsWith("https://open.spotify.com/album")) {
					musicManager.connect(event.getMember().getVoiceState().getChannel());
					SpotifyWrapper.getInstance().playAlbum(event.getMessage().getContentDisplay(), musicManager);

					return;
				}

				if (event.getMessage().getAttachments().size() > 0) {
					musicManager.connect(event.getMember().getVoiceState().getChannel());
					musicManager.searchPlay(event.getMessage().getAttachments().get(0).getUrl());
					return;
				}

					musicManager.connect(event.getMember().getVoiceState().getChannel());
					musicManager.searchPlay(event.getMessage().getContentDisplay());
					return;
				}else
					event.getTextChannel().sendMessage("You are not in a Voice Channel!").complete().delete().queueAfter(5, TimeUnit.SECONDS);

	}
}
