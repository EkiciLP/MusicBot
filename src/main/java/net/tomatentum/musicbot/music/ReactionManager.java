package net.tomatentum.musicbot.music;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.tomatentum.musicbot.MusicBot;
import net.tomatentum.musicbot.music.messagemanagers.FavoriteSongManager;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class ReactionManager extends ListenerAdapter {

	@Override
	public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event) {

		if (event.getUser().isBot()) {
			return;
		}

		GuildMusicManager musicManager = MusicBot.getInstance().getAudioManager().getMusicManager(event.getGuild());
		FavoriteSongManager favoriteSongManager = MusicBot.getInstance().getAudioManager().getFavoriteSongManager(event.getMember(), event.getGuild());
		if (event.getMessageIdLong() == musicManager.getPanelManager().getMessage().getIdLong()) {
			event.getReaction().removeReaction(event.getUser()).queue();
			if (event.getGuild().getAudioManager().isConnected() && event.getGuild().getAudioManager().getConnectedChannel().getMembers().contains(event.getMember())) {

				switch (event.getReaction().getReactionEmote().getEmoji()) {
					case "⏯":
							if (musicManager.getPlayer().isPaused() && musicManager.getPlayer().getPlayingTrack() != null) {
								musicManager.setPaused(false);
							} else {
								musicManager.setPaused(true);
							}
						break;
					case "⏭":
							try {
								musicManager.getTrackScheduler().nextTrack();
							} catch (IllegalArgumentException e) {
								event.getChannel().sendMessage("⛔ Queue is Empty!").complete().delete().queueAfter(5, TimeUnit.SECONDS);
							}
						break;
					case "⏹":
							musicManager.quit();
							musicManager.getPanelManager().setIdle();
						break;
					case "🚫":
							musicManager.getTrackScheduler().clear();
						break;
					case "🔄":
							if (musicManager.getTrackScheduler().isRepeating()) {
								musicManager.getTrackScheduler().setRepeating(false);
							} else
								musicManager.getTrackScheduler().setRepeating(true);

						break;
					case "🔀":
							musicManager.getTrackScheduler().shuffle();
							event.getChannel().sendMessage("🔀 Queue Shuffled!").complete().delete().queueAfter(5, TimeUnit.SECONDS);
						break;
					case "🔊":
							int morevolume = musicManager.getPlayer().getVolume() + 10;

							musicManager.getPlayer().setVolume(Integer.min(100, Integer.max(1, morevolume)));
							musicManager.getPanelManager().update();

						break;
					case "🔉":
							int lessvolume = musicManager.getPlayer().getVolume() - 10;

							musicManager.getPlayer().setVolume(Integer.min(100, Integer.max(1, lessvolume)));
							musicManager.getPanelManager().update();
						break;
					case "⭐":
							favoriteSongManager.add(musicManager.getPlayer().getPlayingTrack().getIdentifier());
						break;
					case "❌":
						favoriteSongManager.remove(musicManager.getPlayer().getPlayingTrack().getIdentifier());
						break;

				}
			}
		}
	}
}
