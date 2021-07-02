package net.tomatentum.musicbot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.Guild;
import net.tomatentum.musicbot.MusicBot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
		if (player.getPlayingTrack() == null) {
			player.playTrack(audioTrack);
		}else
			queue.offer(audioTrack);
	}

	public void clear() {
		queue.clear();
		player.stopTrack();
	}

	public AudioTrack nextTrack() throws IllegalArgumentException {
		if (queue.isEmpty()) {
			throw new IllegalArgumentException("Queue is empty");
		}else {
			AudioTrack audioTrack = queue.poll();
			player.playTrack(audioTrack);
			return audioTrack;
		}
	}

	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		if (endReason.mayStartNext) {
			if (repeating) {
				player.playTrack(track.makeClone());
			}else {
				if (!queue.isEmpty()) {
					player.playTrack(queue.poll());
				}else {
					musicManager.quit();
				}
			}

		}
	}

	public void shuffle() {
		List<AudioTrack> audioTracks = new ArrayList<>(queue);
		Collections.shuffle(audioTracks);
		this.queue = new LinkedBlockingQueue<>(audioTracks);
	}

	public String getQueueString() {
		StringBuilder builder = new StringBuilder();
		int count = 1;
		for (AudioTrack track : queue) {
			builder.append(count).append(": ").append(track.getInfo().title).append(" [").append(MusicBot.getTimestamp(track.getDuration())).append("]");
		}
		return builder.toString();
	}

	public void setRepeating(boolean repeating) {
		this.repeating = repeating;
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
