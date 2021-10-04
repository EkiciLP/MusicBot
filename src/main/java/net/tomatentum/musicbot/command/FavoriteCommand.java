package net.tomatentum.musicbot.command;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.tomatentum.musicbot.TomatenMusic;
import net.tomatentum.musicbot.music.FavoriteSongManager;
import net.tomatentum.musicbot.utils.GuildCommand;
import net.tomatentum.musicbot.utils.Utils;

import java.util.concurrent.TimeUnit;

public class FavoriteCommand implements GuildCommand {
	@Override
	public void execute(Member member, TextChannel channel, Message message, String[] args) {
		message.delete().queueAfter(1, TimeUnit.SECONDS);

		Utils.checkSameChannel(member);

		TomatenMusic.getInstance().getAudioManager().getMusicManager(channel.getGuild()).connect(Utils.findSuitableVoiceChannel(member));

		FavoriteSongManager favoriteSongManager = TomatenMusic.getInstance().getAudioManager().getFavoriteSongManager(member);
		favoriteSongManager.showPanel(channel);

	}
}
