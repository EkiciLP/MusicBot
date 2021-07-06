package net.tomatentum.musicbot.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.tomatentum.musicbot.MusicBot;

import javax.annotation.Nullable;
import java.awt.*;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class PanelManager {

	private GuildMusicManager guildMusicManager;
	private EmbedBuilder builder;
	private Message message;
	private TextChannel channel;
	private boolean recentlchanged = false;

	public PanelManager(GuildMusicManager musicManager) {
		this.guildMusicManager = musicManager;
		try {
			this.channel = MusicBot.getInstance().getBot().getTextChannelById(MusicBot.getInstance().getConfiguration().getLong("Panels." + musicManager.getGuild().getIdLong() + ".channelid"));
			this.message = channel.retrieveMessageById(MusicBot.getInstance().getConfiguration().getLong("Panels." + musicManager.getGuild().getIdLong() + ".messageid")).complete();
			this.builder = new EmbedBuilder(message.getEmbeds().get(0));
		}catch (NullPointerException ignored) {
		}
		startUpdateLoop(3000);
	}



	public void setPanel(TextChannel channel) {
		this.channel = channel;
		this.builder = new EmbedBuilder();


		this.message = this.channel.sendMessage("Building...").complete();


		try {
			this.message.addReaction("⏯").queue();
			this.message.addReaction("⏭").queue();
			this.message.addReaction("⏹").queue();
			this.message.addReaction("🚫").queue();
			this.message.addReaction("🔄").queue();
			this.message.addReaction("🔀").queue();
			this.message.addReaction("🔊").queue();
			this.message.addReaction("🔉").queue();
		}catch (Exception e) {
			System.out.println("error");
		}

		MusicBot.getInstance().getConfiguration().set("Panels." + this.guildMusicManager.getGuild().getIdLong() + ".channelid", channel.getIdLong());
		MusicBot.getInstance().getConfiguration().set("Panels." + this.guildMusicManager.getGuild().getIdLong() + ".messageid", message.getIdLong());
		try {
			MusicBot.getInstance().getConfiguration().save(MusicBot.getInstance().getConfigFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setIdle() {
		recentlchanged = true;
		builder.setColor(Color.RED);
		builder.setAuthor("Stopped");
		builder.setTitle(" - Nothing Playing -");
		builder.setImage("https://i.imgur.com/9Q80W4c.png");
		builder.setFooter("Volume: " + guildMusicManager.getPlayer().getVolume() + "%");
		message = message.editMessage("**Send a Link or a Search Query to play a Song!**\n\n__Queue__:\n" + guildMusicManager.getTrackScheduler().getQueueString()).embed(builder.build()).complete();

		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				recentlchanged = false;
			}
		}, 1000);
	}

	public void setPlaying(AudioTrack track) {
		recentlchanged = true;
		builder.setColor(Color.CYAN);
		builder.setAuthor("Playing");
		builder.setTitle(track.getInfo().title);
		builder.setImage("https://img.youtube.com/vi/" + track.getIdentifier() + "/maxresdefault.jpg");
		if (!guildMusicManager.getTrackScheduler().isRepeating()) {
			builder.setFooter("Volume: " + guildMusicManager.getPlayer().getVolume() + "% | " + MusicBot.getTimestamp(track.getPosition()) + "/" + MusicBot.getTimestamp(track.getDuration()));
		}else
			builder.setFooter("Volume: " + guildMusicManager.getPlayer().getVolume() + "% | " + MusicBot.getTimestamp(track.getPosition()) + "/" + MusicBot.getTimestamp(track.getDuration()) + " | Looping Enabled" );

		message = message.editMessage("**Send a Link or a Search Query to play a Song!**\n\n__Queue__:\n" + guildMusicManager.getTrackScheduler().getQueueString()).embed(builder.build()).complete();
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				recentlchanged = false;
			}
		}, 1000);
	}
	public void setPaused() {
		recentlchanged = true;
		builder.setColor(Color.ORANGE);
		builder.setAuthor("Paused");
		if (!guildMusicManager.getTrackScheduler().isRepeating()) {
			builder.setFooter("Volume: " + guildMusicManager.getPlayer().getVolume() + "% | " + MusicBot.getTimestamp(guildMusicManager.getPlayer().getPlayingTrack().getPosition()) + "/" + MusicBot.getTimestamp(guildMusicManager.getPlayer().getPlayingTrack().getDuration()));
		}else
			builder.setFooter("Volume: " + guildMusicManager.getPlayer().getVolume() + "% | " + MusicBot.getTimestamp(guildMusicManager.getPlayer().getPlayingTrack().getPosition()) + "/" + MusicBot.getTimestamp(guildMusicManager.getPlayer().getPlayingTrack().getDuration()) + " | Looping Enabled" );


		message = message.editMessage("**Send a Link or a Search Query to play a Song!**\n\n__Queue__:\n" + guildMusicManager.getTrackScheduler().getQueueString()).embed(builder.build()).complete();
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				recentlchanged = false;
			}
		}, 1000);
	}


	public void startUpdateLoop(long delay) {
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				if (!recentlchanged) {
					if (message != null) {
						if (guildMusicManager.getPlayer().isPaused()) {
							setPaused();
						} else if (guildMusicManager.getPlayer().getPlayingTrack() != null) {
							setPlaying(guildMusicManager.getPlayer().getPlayingTrack());
						} else {
							setIdle();
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
