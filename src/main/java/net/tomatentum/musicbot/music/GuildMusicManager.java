package net.tomatentum.musicbot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.tomatentum.musicbot.MusicBot;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class GuildMusicManager {
	private AudioPlayer player;
	private Guild guild;
	private TrackScheduler trackScheduler;
	private AudioPlayerSendHandler audioPlayerSendHandler;
	private AudioPlayerManager audioPlayerManager;
	private PanelManager panelManager;


	public GuildMusicManager(AudioPlayerManager playerManager, Guild guild) {
		this.guild = guild;
		this.player = playerManager.createPlayer();
		this.trackScheduler = new TrackScheduler(player, this);
		this.audioPlayerSendHandler = new AudioPlayerSendHandler(player);
		this.panelManager = new PanelManager(this);
		player.addListener(trackScheduler);

		this.startCleanupLoop(20000);
		this.startPresenceLoop(6000);
	}
	public void connect(VoiceChannel channel) {
		guild.getAudioManager().openAudioConnection(channel);
		guild.getAudioManager().setSendingHandler(audioPlayerSendHandler);

	}

	public void quit() {
		guild.getAudioManager().closeAudioConnection();
		guild.getAudioManager().setSendingHandler(null);
		trackScheduler.clear();
		trackScheduler.setRepeating(false);
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
				if (player.getPlayingTrack() != null) {
					MusicBot.getInstance().getBot().getPresence().setActivity(Activity.playing(player.getPlayingTrack().getInfo().title + " [" + MusicBot.getTimestamp(player.getPlayingTrack().getPosition()) + "/" + MusicBot.getTimestamp(player.getPlayingTrack().getDuration()) + "]"));
				}else
					MusicBot.getInstance().getBot().getPresence().setActivity(null);
			}
		}, delay, delay);
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
}
