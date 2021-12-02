package net.tomatentum.musicbot.music;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.StageChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.tomatentum.musicbot.TomatenMusic;
import net.tomatentum.musicbot.spotify.SpotifyWrapper;
import net.tomatentum.musicbot.utils.Utils;

import java.util.concurrent.TimeUnit;

public class MessageReceivePlayHandler extends ListenerAdapter {
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {

		if (event.getChannelType().equals(ChannelType.PRIVATE)) {
			System.out.println(event.getPrivateChannel().getUser().getAsTag() + " >> " + event.getMessage().getContentDisplay());
			return;
		}


		GuildMusicManager musicManager = TomatenMusic.getInstance().getAudioManager().getMusicManager(event.getGuild());


		if (event.getMember().getUser().isBot() || event.getMessage().getContentDisplay().startsWith(",")) {
			return;
		}



		Utils.checkPanelChannel(event.getTextChannel());
		event.getMessage().delete().queueAfter(1, TimeUnit.SECONDS);


		Utils.checkSameChannel(event.getMember());
		VoiceChannel vc = Utils.findSuitableVoiceChannel(event.getMember());


				musicManager.getPanelManager().setLoading();

				musicManager.connect(vc);


				if (event.getMessage().getContentRaw().startsWith("https://open.spotify.com/track")) {
					SpotifyWrapper.getInstance().playTrack(event.getMessage().getContentDisplay(), musicManager);

					return;
				}else if (event.getMessage().getContentRaw().startsWith("https://open.spotify.com/playlist")) {
					SpotifyWrapper.getInstance().playPlaylist(event.getMessage().getContentDisplay(), musicManager);

					return;
				}else if (event.getMessage().getContentRaw().startsWith("https://open.spotify.com/album")) {
					SpotifyWrapper.getInstance().playAlbum(event.getMessage().getContentDisplay(), musicManager);

					return;
				}

				if (event.getMessage().getAttachments().size() > 0) {
					musicManager.searchPlay(event.getMessage().getAttachments().get(0).getUrl());
					return;
				}

					musicManager.searchPlay(event.getMessage().getContentDisplay());
					return;


	}
}
