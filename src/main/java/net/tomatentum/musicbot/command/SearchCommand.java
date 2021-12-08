package net.tomatentum.musicbot.command;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.tomatentum.musicbot.TomatenMusic;
import net.tomatentum.musicbot.utils.GuildCommand;
import net.tomatentum.musicbot.utils.Utils;

import java.util.concurrent.TimeUnit;

public class SearchCommand implements GuildCommand {
	@Override
	public void execute(Member member, TextChannel channel, Message message, String[] args) {
		message.delete().queue();
		Utils.checkSameChannel(member);


		if (args.length > 1) {
			StringBuilder URL = new StringBuilder();

			for (int i = 1; i < args.length; i++) {
				URL.append(args[i]).append(" ");
			}
			Utils.checkSameChannel(member);

			TomatenMusic.getInstance().getAudioManager().getMusicManager(channel.getGuild()).connect(Utils.findSuitableVoiceChannel(member));

			channel.sendMessage("🔍 Searching for: ``" + URL.toString() + "``").complete().delete().queueAfter(5, TimeUnit.SECONDS);


			TomatenMusic.getInstance().getAudioManager().getMusicManager(channel.getGuild()).search(URL.toString(), channel);

		}
	}
}
