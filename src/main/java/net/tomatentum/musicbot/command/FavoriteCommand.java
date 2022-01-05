package net.tomatentum.musicbot.command;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.tomatentum.musicbot.TomatenMusic;
import net.tomatentum.musicbot.favourites.FavoriteSongManager;
import net.tomatentum.musicbot.utils.GuildCommand;
import net.tomatentum.musicbot.utils.Utils;

import java.util.concurrent.TimeUnit;

public class FavoriteCommand extends GuildCommand {
	protected FavoriteCommand() {
		super("fav", "Shows your favorites panel", null);
	}

	@Override
	public void execute(SlashCommandEvent command) {
		Utils.checkSameChannel(command.getMember());


		TomatenMusic.getInstance().getAudioManager().getMusicManager(command.getGuild()).connect(Utils.findSuitableVoiceChannel(command.getMember()));

		FavoriteSongManager favoriteSongManager = TomatenMusic.getInstance().getAudioManager().getFavoriteSongManager(command.getMember());
		favoriteSongManager.showPanel(command.getTextChannel());

	}
}
