package net.tomatentum.musicbot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.BasicAudioPlaylist;
import net.dv8tion.jda.api.audio.hooks.ConnectionStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.tomatentum.musicbot.MusicBot;
import net.tomatentum.musicbot.music.messagemanagers.PanelManager;
import net.tomatentum.musicbot.music.messagemanagers.SearchOperation;
import net.tomatentum.musicbot.utils.SelectionPanel;
import org.jetbrains.annotations.NotNull;

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
	private VoiceChannel latestChannel;



	public GuildMusicManager(AudioPlayerManager playerManager, Guild guild) {
		MusicBot.getInstance().getBot().addEventListener(this);
		this.audioPlayerManager = playerManager;
		this.player = playerManager.createPlayer();
		this.guild = guild;
		this.trackScheduler = new TrackScheduler(player, this);
		this.audioPlayerSendHandler = new AudioPlayerSendHandler(player);
		this.panelManager = new PanelManager(this);
		player.addListener(trackScheduler);

		this.startCleanupLoop(60000);
		guild.getAudioManager().setSelfDeafened(true);
	}
	public void connect(VoiceChannel channel) {
		guild.getAudioManager().openAudioConnection(channel);
		guild.getAudioManager().setSendingHandler(audioPlayerSendHandler);
		latestChannel = channel;

	}

	public void quit() {
		guild.getAudioManager().closeAudioConnection();
		guild.getAudioManager().setSendingHandler(null);
		trackScheduler.clear();
		trackScheduler.setRepeating(false);
		player.stopTrack();
		player.setPaused(false);
		player.setVolume(100);
		panelManager.update();
	}

	public void setPaused(boolean paused) {
		if (paused) {
			player.setPaused(true);
		}else {
			player.setPaused(false);
		}
		panelManager.update();
	}

	public void play(AudioTrack track) {
		trackScheduler.queue(track.makeClone());
		panelManager.update();
	}



	public void play(AudioPlaylist playlist) {


		if (playlist.getTracks().size() <= 20) {
			playlist.getTracks().forEach(track -> trackScheduler.queue(track.makeClone()));
			panelManager.update();
		}else
			panelManager.getChannel().sendMessage("⛔ Playlist too long").complete().delete().queueAfter(5, TimeUnit.SECONDS);
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
				play(audioTrack);
			}

			@Override
			public void playlistLoaded(AudioPlaylist audioPlaylist) {
				play(audioPlaylist);
			}

			@Override
			public void noMatches() {
				audioPlayerManager.loadItem("ytsearch:" + trackURL, new AudioLoadResultHandler() {
					@Override
					public void trackLoaded(AudioTrack audioTrack) {
						play(audioTrack);
					}

					@Override
					public void playlistLoaded(AudioPlaylist audioPlaylist) {
						play(audioPlaylist.getTracks().get(0));
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
			}

			@Override
			public void playlistLoaded(AudioPlaylist audioPlaylist) {
				new SearchOperation(audioPlaylist, channel, MusicBot.getInstance().getAudioManager().getMusicManager(guild));

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
				if (panelManager.getMessage() == null) {
					return;
				}

				if (guild.getAudioManager().isConnected() && guild.getAudioManager().getConnectedChannel().getMembers().size() < 2 || player.getPlayingTrack() == null) {
					quit();
				}
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

	public AudioPlayerManager getAudioPlayerManager() {
		return audioPlayerManager;
	}

	public VoiceChannel getLatestChannel() {
		return latestChannel;
	}
}
