package net.tomatentum.musicbot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.Guild;
import net.tomatentum.musicbot.MusicBot;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {
	private BlockingQueue<AudioTrack> queue;
	private AudioPlayer player;
	private GuildMusicManager musicManager;
	private boolean repeating;

	public TrackScheduler(AudioPlayer player, GuildMusicManager musicManager) {
		this.musicManager = musicManager;
		this.player = player;
		this.queue = new LinkedBlockingQueue<>();
	}

	public void queue(AudioTrack audioTrack) {
		if (musicManager.getLatestChannel() != null)
			musicManager.connect(musicManager.getLatestChannel());


		if (player.getPlayingTrack() == null) {
			player.playTrack(audioTrack);
		}else {
			queue.offer(audioTrack);
		}
	}

	public void clear() {
		queue.clear();

		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				musicManager.getPanelManager().update();

			}
		}, 500);
	}

	public AudioTrack nextTrack() throws IllegalArgumentException {
		if (queue.isEmpty()) {
			throw new IllegalArgumentException("Queue is empty");
		}else {
			AudioTrack audioTrack = queue.poll();
			player.playTrack(audioTrack);
			musicManager.getPanelManager().update();

			return audioTrack;
		}
	}

	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		System.out.println("track ended " + track.getInfo().title);
		if (endReason.mayStartNext) {
			if (repeating) {
				System.out.println("repeated!");
				AudioTrack audioTrack = track.makeClone();
				player.playTrack(track.makeClone());
				musicManager.getPanelManager().update();

			} else {
				if (!queue.isEmpty()) {
					System.out.println("queued");
					AudioTrack audioTrack = queue.poll();
					player.playTrack(audioTrack);
					musicManager.getPanelManager().update();
				} else
					musicManager.getPanelManager().update();
			}
		}
	}

	public void shuffle() {
		List<AudioTrack> audioTracks = new ArrayList<>(queue);
		Collections.shuffle(audioTracks);
		this.queue = new LinkedBlockingQueue<>(audioTracks);
		musicManager.getPanelManager().update();

	}

	public String getQueueString() {
		StringBuilder builder = new StringBuilder();
		int count = 1;
		for (AudioTrack track : queue) {
			builder.append(count).append(": ").append(track.getInfo().title).append(" [").append(MusicBot.getTimestamp(track.getDuration())).append("]\n");
			count++;
		}
		return builder.toString();
	}

	public void setRepeating(boolean repeating) {
		this.repeating = repeating;
		musicManager.getPanelManager().update();
	}

	public boolean isRepeating() {
		return repeating;
	}

	public AudioPlayer getPlayer() {
		return player;
	}

	public BlockingQueue<AudioTrack> getQueue() {
		return queue;
	}
}
