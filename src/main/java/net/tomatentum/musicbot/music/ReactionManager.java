package net.tomatentum.musicbot.music;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.tomatentum.musicbot.MusicBot;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class ReactionManager extends ListenerAdapter {

	@Override
	public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event) {

		if (event.getUser().isBot()) {
			return;
		}

		GuildMusicManager musicManager = MusicBot.getInstance().getAudioManager().getMusicManager(event.getGuild());
		if (event.getMessageIdLong() == musicManager.getPanelManager().getMessage().getIdLong()) {
			event.getReaction().removeReaction(event.getUser()).queue();

			switch (event.getReaction().getReactionEmote().getEmoji()) {
				case "⏯":
					if (event.getGuild().getAudioManager().isConnected() && event.getGuild().getAudioManager().getConnectedChannel().getMembers().contains(event.getMember())) {
						if (musicManager.getPlayer().isPaused() && musicManager.getPlayer().getPlayingTrack() != null) {
							musicManager.setPaused(false);
						} else {
							musicManager.setPaused(true);
						}
					}
					break;
				case "⏭":
					if (event.getGuild().getAudioManager().isConnected() && event.getGuild().getAudioManager().getConnectedChannel().getMembers().contains(event.getMember())) {
						try {
							musicManager.getTrackScheduler().nextTrack();
						}catch (IllegalArgumentException e) {
							event.getChannel().sendMessage("⛔ Queue is Empty!").complete().delete().queueAfter(5, TimeUnit.SECONDS);
						}
					}
					break;
				case "⏹":
					if (event.getGuild().getAudioManager().isConnected() && event.getGuild().getAudioManager().getConnectedChannel().getMembers().contains(event.getMember())) {
						musicManager.quit();
						musicManager.getPanelManager().setIdle();
					}
					break;
				case "🚫":
					if (event.getGuild().getAudioManager().isConnected() && event.getGuild().getAudioManager().getConnectedChannel().getMembers().contains(event.getMember())) {
						musicManager.getTrackScheduler().clear();
					}
					break;
				case "🔄":
					if (event.getGuild().getAudioManager().isConnected() && event.getGuild().getAudioManager().getConnectedChannel().getMembers().contains(event.getMember())) {
						if (musicManager.getTrackScheduler().isRepeating()) {
							musicManager.getTrackScheduler().setRepeating(false);
						}else
							musicManager.getTrackScheduler().setRepeating(true);

					}
						break;
				case "🔀":
					if (event.getGuild().getAudioManager().isConnected() && event.getGuild().getAudioManager().getConnectedChannel().getMembers().contains(event.getMember())) {
						musicManager.getTrackScheduler().shuffle();
						event.getChannel().sendMessage("🔀 Queue Shuffled!").complete().delete().queueAfter(5, TimeUnit.SECONDS);
					}
						break;

			}
		}
	}
}
