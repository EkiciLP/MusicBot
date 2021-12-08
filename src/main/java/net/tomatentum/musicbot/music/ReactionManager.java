package net.tomatentum.musicbot.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.tomatentum.musicbot.TomatenMusic;
import net.tomatentum.musicbot.favourites.Database;
import net.tomatentum.musicbot.favourites.FavoriteSongManager;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class ReactionManager extends ListenerAdapter {

	@Override
	public void onButtonClick(@NotNull ButtonClickEvent event) {

		if (event.getUser().isBot()) {
			return;
		}

		GuildMusicManager musicManager = TomatenMusic.getInstance().getAudioManager().getMusicManager(event.getGuild());
		FavoriteSongManager favoriteSongManager = TomatenMusic.getInstance().getAudioManager().getFavoriteSongManager(event.getMember());

		if (event.getMessageIdLong() == musicManager.getPanelManager().getMessage().getIdLong()) {
			if (event.getGuild().getAudioManager().isConnected() && event.getGuild().getAudioManager().getConnectedChannel().getMembers().contains(event.getMember())) {
				event.deferEdit().queue();
				AudioTrack currenttrack = musicManager.getPlayer().getPlayingTrack();
				event.getHook().setEphemeral(true);

				switch (event.getComponentId()) {
					case "play":
							if (musicManager.getPlayer().isPaused() && currenttrack != null) {
								musicManager.setPaused(false);
							} else {
								musicManager.setPaused(true);
							}
						break;
					case "skip":
							try {
								musicManager.getTrackScheduler().nextTrack();
							} catch (IllegalArgumentException e) {
								event.getHook()
										.sendMessage("⛔ Queue is Empty!")
										.queue();
							}
						break;
					case "stop":
							musicManager.quit();
							musicManager.getPanelManager().setIdle();
						break;
					case "clear":
							musicManager.getTrackScheduler().clear();
						break;
					case "loop":
						musicManager.getTrackScheduler().setRepeating(!musicManager.getTrackScheduler().isRepeating());

						break;
					case "shuffle":
							musicManager.getTrackScheduler().shuffle();
							event.getHook()
									.sendMessage("🔀 Queue Shuffled!")
									.queue();
						break;
					case "forward":
						musicManager.skip(30);
						break;
					case "rewind":
						musicManager.rewind(30);
						break;
					case "fav":
						AudioTrackInfo trackInfo = currenttrack.getInfo();
						if (favoriteSongManager.add(trackInfo)) {

							event.getHook()
									.sendMessage(
											"***" + musicManager.getPlayer().getPlayingTrack().getInfo().title + "***" +
													"\nAdded to your favourites!"
									).queue();
						}else
							event.getHook().sendMessage("Favourites already contain track!").queue();

						break;
					case "unfav":
						try {
						favoriteSongManager.remove(currenttrack.getInfo().uri);
						event.getHook()
								.sendMessage(
										"***" + musicManager.getPlayer().getPlayingTrack().getInfo().title + "***" +
												"\nRemoved from your favourites!"
								).queue();
						}catch (IllegalArgumentException exception) {
							event.getHook().sendMessage(exception.getMessage()).queue();
						}
						break;
				}
				event.getHook().editOriginal(musicManager.getPanelManager().getNextMessage().build()).queue();

			}else
				event.reply("You cant do that!").setEphemeral(true).queue();
		}
	}
}
