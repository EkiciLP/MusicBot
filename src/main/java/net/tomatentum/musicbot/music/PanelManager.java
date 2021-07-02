package net.tomatentum.musicbot.music;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.tomatentum.musicbot.MusicBot;

import java.awt.*;

public class PanelManager {

	private GuildMusicManager guildMusicManager;
	private EmbedBuilder builder;
	private Message message;
	private TextChannel channel;

	public PanelManager(GuildMusicManager musicManager) {
		this.guildMusicManager = musicManager;
		this.channel = MusicBot.getInstance().getBot().getTextChannelById(MusicBot.getInstance().getConfiguration().getLong("Panels." + musicManager.getGuild().getIdLong() + ".channelid"));
		this.message = channel.retrieveMessageById("Panels." + musicManager.getGuild().getIdLong() + ".messagesid").complete();
		this.builder = new EmbedBuilder(message.getEmbeds().get(0));
	}



	public void setPanel(TextChannel channel) {
		builder.setColor(Color.RED);
		builder.setAuthor("Stopped");
		builder.setTitle(" - Nothing Playing -");
		builder.setImage("https://i.imgur.com/9Q80W4c.png");
		builder.setFooter("Volume: " + guildMusicManager.getPlayer().getVolume() + "%");
		this.channel = channel;



		message = channel.sendMessage(builder.build()).complete();
	}

	public TextChannel getChannel() {
		return channel;
	}

	public Message getMessage() {
		return message;
	}
}
