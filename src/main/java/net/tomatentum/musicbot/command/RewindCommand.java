package net.tomatentum.musicbot.command;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.tomatentum.musicbot.TomatenMusic;
import net.tomatentum.musicbot.music.GuildMusicManager;
import net.tomatentum.musicbot.utils.GuildCommand;
import net.tomatentum.musicbot.utils.Utils;

public class RewindCommand extends GuildCommand {

    public RewindCommand() {
        super("rewind", "Rewind the specified seconds", new OptionData(OptionType.INTEGER, "time", "Time in Seconds", true));
    }

    @Override
    public void execute(SlashCommandEvent command) {
        if (!Utils.checkSameChannel(command.getMember())) {
            command.reply("You cant do that!").setEphemeral(true).queue();
            return;
        }
        command.deferReply(true);
        command.getHook().setEphemeral(true);

        GuildMusicManager musicManager = TomatenMusic.getInstance().getAudioManager().getMusicManager(command.getGuild());

        int time = (int) command.getOption("time").getAsLong();

        musicManager.rewind(time);

        command.getHook().sendMessage(String.format("Rewinded %s seconds to %s", time, TomatenMusic.getTimestamp(musicManager.getPlayer().getPlayingTrack().getPosition()))).queue();

    }
}
