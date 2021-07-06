package net.tomatentum.musicbot.music;

import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sun.org.apache.bcel.internal.generic.INSTANCEOF;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.tomatentum.musicbot.MusicBot;

import java.awt.font.LineBreakMeasurer;
import java.time.OffsetDateTime;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class SelectionPanel extends ListenerAdapter {
	private final GuildMusicManager musicManager;
	private final AudioPlaylist audioPlaylist;
	private TextChannel channel;
	private EmbedBuilder builder;
	private Message message;


	public SelectionPanel(TextChannel channel, AudioPlaylist audioPlaylist) {

		MusicBot.getInstance().getBot().addEventListener(this);
		this.channel = channel;
		this.musicManager = MusicBot.getInstance().getAudioManager().getMusicManager(channel.getGuild());
		this.audioPlaylist = audioPlaylist;
		this.builder = new EmbedBuilder();

		if (audioPlaylist.getTracks().size() == 0) {
			channel.sendMessage("🛑 Nothing found!").queue();
			return;
		}

		StringBuilder description = new StringBuilder();
		int count = 1;
		for (int i = 0; i < 9 && i < audioPlaylist.getTracks().size(); i++) {
			AudioTrack track = audioPlaylist.getTracks().get(i);
			description.append(count).append(": ").append(track.getInfo().title).append(" [").append(MusicBot.getTimestamp(track.getDuration())).append("]\n");
			count++;
		}
		this.builder.setTitle(audioPlaylist.getName());

		this.builder.setTimestamp(OffsetDateTime.now());
		this.builder.setFooter("- Queue a track by reacting with the correct number");
		this.builder.setDescription(description.toString());

		message = channel.sendMessage(builder.build()).complete();
		message.delete().queueAfter(1, TimeUnit.MINUTES);
		switch (audioPlaylist.getTracks().size()) {
			case 1:
				message.addReaction("1️⃣").queue();
				break;
			case 2:
				message.addReaction("1️⃣").queue();
				message.addReaction("2️⃣").queue();
				break;
			case 3:
				message.addReaction("1️⃣").queue();
				message.addReaction("2️⃣").queue();
				message.addReaction("3️⃣").queue();
				break;
			case 4:
				message.addReaction("1️⃣").queue();
				message.addReaction("2️⃣").queue();
				message.addReaction("3️⃣").queue();
				message.addReaction("4️⃣").queue();
				break;
			case 5:
				message.addReaction("1️⃣").queue();
				message.addReaction("2️⃣").queue();
				message.addReaction("3️⃣").queue();
				message.addReaction("4️⃣").queue();
				message.addReaction("5️⃣").queue();
				break;
			case 6:
				message.addReaction("1️⃣").queue();
				message.addReaction("2️⃣").queue();
				message.addReaction("3️⃣").queue();
				message.addReaction("4️⃣").queue();
				message.addReaction("5️⃣").queue();
				message.addReaction("6️⃣").queue();
				break;
			case 7:
				message.addReaction("1️⃣").queue();
				message.addReaction("2️⃣").queue();
				message.addReaction("3️⃣").queue();
				message.addReaction("4️⃣").queue();
				message.addReaction("5️⃣").queue();
				message.addReaction("6️⃣").queue();
				message.addReaction("7️⃣").queue();
				break;
			case 8:
				message.addReaction("1️⃣").queue();
				message.addReaction("2️⃣").queue();
				message.addReaction("3️⃣").queue();
				message.addReaction("4️⃣").queue();
				message.addReaction("5️⃣").queue();
				message.addReaction("6️⃣").queue();
				message.addReaction("7️⃣").queue();
				message.addReaction("8️⃣").queue();
				break;
			default:
				message.addReaction("1️⃣").queue();
				message.addReaction("2️⃣").queue();
				message.addReaction("3️⃣").queue();
				message.addReaction("4️⃣").queue();
				message.addReaction("5️⃣").queue();
				message.addReaction("6️⃣").queue();
				message.addReaction("7️⃣").queue();
				message.addReaction("8️⃣").queue();
				message.addReaction("9️⃣").queue();
				break;
		}



	}

	@Override
	public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
		if (event.getUser().isBot()) {
			return;
		}

		if (event.getMessageIdLong() == message.getIdLong()) {
			event.getReaction().removeReaction(event.getUser()).queue();
			try {
				switch (event.getReactionEmote().getEmoji()) {
					case "1️⃣":
						musicManager.getTrackScheduler().queue(audioPlaylist.getTracks().get(0));
						break;
					case "2️⃣":
						musicManager.getTrackScheduler().queue(audioPlaylist.getTracks().get(1));
						break;
					case "3️⃣":
						musicManager.getTrackScheduler().queue(audioPlaylist.getTracks().get(2));
						break;
					case "4️⃣":
						musicManager.getTrackScheduler().queue(audioPlaylist.getTracks().get(3));
						break;
					case "5️⃣":
						musicManager.getTrackScheduler().queue(audioPlaylist.getTracks().get(4));
						break;
					case "6️⃣":
						musicManager.getTrackScheduler().queue(audioPlaylist.getTracks().get(5));
						break;
					case "7️⃣":
						musicManager.getTrackScheduler().queue(audioPlaylist.getTracks().get(6));
						break;
					case "8️⃣":
						musicManager.getTrackScheduler().queue(audioPlaylist.getTracks().get(7));
						break;
					case "9️⃣":
						musicManager.getTrackScheduler().queue(audioPlaylist.getTracks().get(8));
						break;
				}
			}catch (IndexOutOfBoundsException e) {

			}
		}
	}


}
