package net.tomatentum.musicbot.music.messagemanagers;

import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.tomatentum.musicbot.TomatenMusic;
import net.tomatentum.musicbot.music.GuildMusicManager;

import java.awt.*;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class PanelManager {

	private GuildMusicManager guildMusicManager;
	private EmbedBuilder builder;
	private Message message;
	private TextChannel channel;

	public PanelManager(GuildMusicManager musicManager) {
		this.guildMusicManager = musicManager;
		this.builder = new EmbedBuilder();
		try {
			this.channel = musicManager.getGuild().getTextChannelById(TomatenMusic.getInstance().getConfiguration().getLong("Panels." + musicManager.getGuild().getIdLong() + ".channelid"));
			this.message = channel.retrieveMessageById(TomatenMusic.getInstance().getConfiguration().getLong("Panels." + musicManager.getGuild().getIdLong() + ".messageid")).complete();
		}catch (NullPointerException e) {
			System.out.println("No panel found for " + musicManager.getGuild().getName());
		}


		if (message != null) {
			startUpdateLoop(6000);
		}
	}



	public void setPanel(TextChannel channel) {
		this.channel = channel;
		this.builder = new EmbedBuilder();


		MessageAction action = channel.sendMessageEmbeds(builder.build());


		try {
			action.setActionRows(getActionRows()).queue();

			startUpdateLoop(5000);
		}catch (Exception e) {
			System.out.println("error");
		}

		TomatenMusic.getInstance().getConfiguration().set("Panels." + this.guildMusicManager.getGuild().getIdLong() + ".channelid", channel.getIdLong());
		TomatenMusic.getInstance().getConfiguration().set("Panels." + this.guildMusicManager.getGuild().getIdLong() + ".messageid", message.getIdLong());
		try {
			TomatenMusic.getInstance().getConfiguration().save(TomatenMusic.getInstance().getConfigFile());
		} catch (IOException e) {
			e.printStackTrace();
		}

		setIdle();
	}

	public void setIdle() {
		builder.setColor(Color.RED);
		builder.setAuthor("Stopped");
		builder.setTitle(" - Nothing Playing -");
		builder.setImage("https://media.tomatentum.net/TMBanner.gif");
	}

	public void setLoading() {
		builder.setImage("https://c.tenor.com/DHkIdy0a-UkAAAAC/loading-cat.gif");
		builder.setAuthor("Loading");
		builder.setColor(Color.WHITE);
		message = message.editMessage("**Send a Link or a Search Query to play a Song!**\n\n__Queue__:\n" + guildMusicManager.getTrackScheduler().getQueueString()).embed(builder.build()).complete();

	}

	public void setPlaying(AudioTrack track) {
		builder.setColor(Color.CYAN);
		builder.setAuthor("Playing");
		builder.setTitle(track.getInfo().title == "Unknown title" ? track.getInfo().identifier : track.getInfo().title, track.getInfo().uri);

		if (track.getSourceManager().getClass().equals(YoutubeAudioSourceManager.class))
			builder.setImage("https://img.youtube.com/vi/" + track.getIdentifier() + "/mqdefault.jpg");
		else
			builder.setImage("https://c.tenor.com/TFxhVKgpg0MAAAAd/sus-bloke-sus.gif");


	}
	public void setPaused() {
		builder.setColor(Color.ORANGE);
		builder.setAuthor("Paused");
	}

	public void update() {
		message = message.editMessage(getNextMessage()).complete();
	}

	private Message getNextMessage() {
		if (guildMusicManager.getPlayer().getPlayingTrack() != null) {
			guildMusicManager.getPanelManager().setPlaying(guildMusicManager.getPlayer().getPlayingTrack());
		}else
			guildMusicManager.getPanelManager().setIdle();
		if (guildMusicManager.getPlayer().isPaused()) {
			guildMusicManager.getPanelManager().setPaused();
		}


		if (!guildMusicManager.getTrackScheduler().isRepeating()) {
			if (guildMusicManager.getPlayer().getPlayingTrack() != null) {
				builder.setFooter(TomatenMusic.getTimestamp(guildMusicManager.getPlayer().getPlayingTrack().getPosition()) + "/" + TomatenMusic.getTimestamp(guildMusicManager.getPlayer().getPlayingTrack().getDuration()));
			}else
				builder.setFooter(" ");
		}else
			builder.setFooter(TomatenMusic.getTimestamp(guildMusicManager.getPlayer().getPlayingTrack().getPosition()) + "/" + TomatenMusic.getTimestamp(guildMusicManager.getPlayer().getPlayingTrack().getDuration()) + " | Looping Enabled" );

		MessageBuilder builder = new MessageBuilder();

		builder.setActionRows(
				getActionRows())
				.setContent("**Send a Link or a Search Query to play a Song!**\n\n__Queue__:\n" + guildMusicManager.getTrackScheduler().getQueueString())
				.setEmbeds(this.builder.build());

		return builder.build();
	}

	public void sendMessage(String text) {
		channel.sendMessage(text).queue(message -> {
			message.delete().queueAfter(5, TimeUnit.SECONDS);
		});


	}

	public ActionRow[] getActionRows() {
		return new ActionRow[] {
				ActionRow.of(
						guildMusicManager.getPlayer().isPaused() ? Button.danger("play", "⏯️") : Button.success("play", "⏯️"),
						Button.primary("skip", "⏭️"),
						Button.danger("stop", "⏹"),
						Button.danger("clear", "🚫")
				),
				ActionRow.of(
						guildMusicManager.getTrackScheduler().isRepeating() ? Button.danger("loop", "🔄") : Button.success("loop", "🔄"),
						Button.primary("shuffle", "🔀"),
						Button.secondary("rewind", "↩"),
						Button.secondary("forward", "↪")
				),
				ActionRow.of(
						Button.success("fav", "⭐"),
						Button.danger("unfav", "❌")
				)
		};
	}


	public void startUpdateLoop(long delay) {
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
					if (guildMusicManager.getPlayer().getPlayingTrack() != null) {
						if (message != null) {
							if (guildMusicManager.getPlayer().isPaused()) {
								setPaused();
								update();
							} else if (guildMusicManager.getPlayer().getPlayingTrack() != null) {
								setPlaying(guildMusicManager.getPlayer().getPlayingTrack());
								update();
							} else {
								setIdle();
								update();
							}
						}
					}
			}
		}, delay , delay);
	}


	public TextChannel getChannel() {
		return channel;
	}

	public Message getMessage() {
		return message;
	}
}
