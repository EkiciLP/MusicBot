package net.tomatentum.musicbot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.tomatentum.musicbot.TomatenMusic;
import net.tomatentum.musicbot.music.messagemanagers.PanelManager;
import net.tomatentum.musicbot.music.messagemanagers.SearchOperation;
import org.jetbrains.annotations.NotNull;

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
	private Timer quitTimer;


	public GuildMusicManager(AudioPlayerManager playerManager, Guild guild) {
		TomatenMusic.getInstance().getBot().addEventListener(this);
		this.audioPlayerManager = playerManager;
		this.player = playerManager.createPlayer();
		this.guild = guild;
		this.trackScheduler = new TrackScheduler(player, this);
		this.audioPlayerSendHandler = new AudioPlayerSendHandler(player);
		this.panelManager = new PanelManager(this);
		player.addListener(trackScheduler);

		guild.getAudioManager().setSelfDeafened(true);
	}
	public void connect(VoiceChannel channel) {
		guild.getAudioManager().openAudioConnection(channel);
		guild.getAudioManager().setSendingHandler(audioPlayerSendHandler);
		startCleanupLoop();
	}

	public void quit() {
		System.out.println("Quit!");
		guild.getAudioManager().closeAudioConnection();
		trackScheduler.clear();
		trackScheduler.setRepeating(false);
		player.stopTrack();
		player.setPaused(false);
		player.setVolume(100);
		panelManager.update();
		cancelCleanupLoop();
	}

	public void setPaused(boolean paused) {
		player.setPaused(paused);
		panelManager.update();
	}

	public void skip(long seconds) {
		AudioTrack currenttrack = getPlayer().getPlayingTrack();

		currenttrack.setPosition((long) Math.min(currenttrack.getDuration()-0.5, currenttrack.getPosition()+(seconds*1000)));
	}

	public void rewind(long seconds) {
		AudioTrack currenttrack = getPlayer().getPlayingTrack();

		currenttrack.setPosition((long) Math.max(0, currenttrack.getPosition()-(seconds*1000)));
	}

	public void play(AudioTrack track) {

		trackScheduler.queue(track.makeClone());
		panelManager.update();
	}

	public void play(AudioPlaylist playlist) {

			playlist.getTracks().forEach(track -> trackScheduler.queue(track.makeClone()));
			panelManager.update();
	}


	public void searchPlay(String URL) {
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
				new SearchOperation(audioPlaylist, channel, TomatenMusic.getInstance().getAudioManager().getMusicManager(guild));

			}

			@Override
			public void noMatches() {
			}

			@Override
			public void loadFailed(FriendlyException e) {
			}
		});
	}



	private void startCleanupLoop() {

		if (quitTimer != null)
			return;

		quitTimer = new Timer();

		quitTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (panelManager.getMessage() == null) {
					return;
				}

				if (guild.getAudioManager().isConnected() && guild.getAudioManager().getConnectedChannel().getMembers().size() < 2 || player.getPlayingTrack() == null) {
					quit();
				}
			}
		}, 10000, 40000);
	}

	private void cancelCleanupLoop() {

		if (quitTimer == null)
			return;

		quitTimer.cancel();
		quitTimer.purge();
		quitTimer = null;
	}

	@Override
	public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent event) {
		if (event.getMember().getUser().getIdLong() == TomatenMusic.getInstance().getBot().getSelfUser().getIdLong()) {
			if (event.getGuild().equals(guild))
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

}
