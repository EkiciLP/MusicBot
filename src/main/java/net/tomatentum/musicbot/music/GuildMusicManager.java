package net.tomatentum.musicbot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.BasicAudioPlaylist;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.tomatentum.musicbot.MusicBot;
import org.jetbrains.annotations.NotNull;

import javax.xml.bind.Marshaller;
import java.util.Arrays;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class GuildMusicManager extends ListenerAdapter {
	private AudioPlayer player;
	private Guild guild;
	private TrackScheduler trackScheduler;
	private AudioPlayerSendHandler audioPlayerSendHandler;
	private AudioPlayerManager audioPlayerManager;
	private PanelManager panelManager;



	public GuildMusicManager(AudioPlayerManager playerManager, Guild guild) {
		MusicBot.getInstance().getBot().addEventListener(this);
		this.audioPlayerManager = playerManager;
		this.guild = guild;
		this.player = playerManager.createPlayer();
		this.trackScheduler = new TrackScheduler(player, this);
		this.audioPlayerSendHandler = new AudioPlayerSendHandler(player);
		this.panelManager = new PanelManager(this);
		player.addListener(trackScheduler);

		this.startCleanupLoop(30000);
	}
	public void connect(VoiceChannel channel) {
		guild.getAudioManager().openAudioConnection(channel);
		guild.getAudioManager().setSendingHandler(audioPlayerSendHandler);

	}

	public void quit() {
		guild.getAudioManager().closeAudioConnection();
		guild.getAudioManager().setSendingHandler(null);
		panelManager.setIdle();
		trackScheduler.clear();
		trackScheduler.setRepeating(false);
		player.stopTrack();
		player.setPaused(false);
		player.setVolume(100);
	}

	public void setPaused(boolean paused) {
		if (paused) {
			player.setPaused(true);
			panelManager.setPaused();
		}else {
			player.setPaused(false);
			panelManager.setPlaying(player.getPlayingTrack());
		}
	}

	public void loadAndQueue(String URL) {
		String trackURL;
		if (URL.startsWith("<") && URL.endsWith(">")) {
			trackURL = URL.substring(1, URL.length()-1);
		}else
			trackURL = URL;

		audioPlayerManager.loadItem(trackURL, new AudioLoadResultHandler() {
			@Override
			public void trackLoaded(AudioTrack audioTrack) {
				trackScheduler.queue(audioTrack);
			}

			@Override
			public void playlistLoaded(AudioPlaylist audioPlaylist) {
				audioPlaylist.getTracks().forEach(trackScheduler::queue);
			}

			@Override
			public void noMatches() {
				audioPlayerManager.loadItem("ytsearch:" + trackURL, new AudioLoadResultHandler() {
					@Override
					public void trackLoaded(AudioTrack audioTrack) {
						trackScheduler.queue(audioTrack);
					}

					@Override
					public void playlistLoaded(AudioPlaylist audioPlaylist) {
						trackScheduler.queue(audioPlaylist.getTracks().get(0));
					}

					@Override
					public void noMatches() {
						panelManager.getChannel().sendMessage("⛔ No Matches for: ``" + trackURL + "``").complete().delete().queueAfter(10, TimeUnit.SECONDS);
					}

					@Override
					public void loadFailed(FriendlyException e) {
						panelManager.getChannel().sendMessage("⛔ ``" + e.getMessage() + "``").complete().delete().queueAfter(10, TimeUnit.SECONDS);
					}
				});
			}

			@Override
			public void loadFailed(FriendlyException e) {
				panelManager.getChannel().sendMessage("⛔ ``" + e.getMessage() + "``").complete().delete().queueAfter(10, TimeUnit.SECONDS);
			}
		});
	}

	public void search(@NotNull String query, TextChannel channel) {
		String trackURL;
		if (query.startsWith("<") && query.endsWith(">")) {
			trackURL = query.substring(1, query.length()-1);
		}else
			trackURL = query;

		audioPlayerManager.loadItem("ytsearch:" + trackURL, new AudioLoadResultHandler() {
			@Override
			public void trackLoaded(AudioTrack audioTrack) {
				new SelectionPanel(channel,	new BasicAudioPlaylist("Selected Track", Collections.singletonList(audioTrack), audioTrack, true));
				System.out.println("loaded track");
			}

			@Override
			public void playlistLoaded(AudioPlaylist audioPlaylist) {
				new SelectionPanel(channel, audioPlaylist);


			}

			@Override
			public void noMatches() {
			}

			@Override
			public void loadFailed(FriendlyException e) {
			}
		});
	}


	public void startCleanupLoop(long delay) {
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				if (guild.getAudioManager().isConnected() && guild.getAudioManager().getConnectedChannel().getMembers().size() < 2) {
					quit();
				}
			}
		}, delay, delay);
	}

	public void startPresenceLoop(long delay) {
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				if (player.isPaused()) {
					MusicBot.getInstance().getBot().getPresence().setActivity(Activity.playing("(PAUSED) " + player.getPlayingTrack().getInfo().title + " [" + MusicBot.getTimestamp(player.getPlayingTrack().getPosition()) + "/" + MusicBot.getTimestamp(player.getPlayingTrack().getDuration()) + "]"));

				}else if (player.getPlayingTrack() != null) {
					MusicBot.getInstance().getBot().getPresence().setActivity(Activity.playing(player.getPlayingTrack().getInfo().title + " [" + MusicBot.getTimestamp(player.getPlayingTrack().getPosition()) + "/" + MusicBot.getTimestamp(player.getPlayingTrack().getDuration()) + "]"));

				}else
					MusicBot.getInstance().getBot().getPresence().setActivity(null);
			}
		}, delay, delay);
	}

	@Override
	public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent event) {
		if (event.getMember().getUser().getIdLong() == MusicBot.getInstance().getBot().getSelfUser().getIdLong()) {
			quit();
		}
	}

	public AudioPlayer getPlayer() {
		return player;
	}

	public Guild getGuild() {
		return guild;
	}

	public TrackScheduler getTrackScheduler() {
		return trackScheduler;
	}

	public PanelManager getPanelManager() {
		return panelManager;
	}
}
