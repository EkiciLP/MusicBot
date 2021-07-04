package net.tomatentum.musicbot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.tomatentum.musicbot.command.commands.PanelCommand;
import net.tomatentum.musicbot.command.commands.VolumeCommand;
import net.tomatentum.musicbot.command.utils.CommandManager;
import net.tomatentum.musicbot.music.AudioManager;
import net.tomatentum.musicbot.music.MessageReceivePlayHandler;
import net.tomatentum.musicbot.music.ReactionManager;
import org.bspfsystems.yamlconfiguration.file.YamlConfiguration;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class MusicBot {

	private YamlConfiguration configuration;
	private File configFile;
	private JDA bot;
	private CommandManager cmdmanager;
	private AudioManager audioManager;

	private static MusicBot Instance;


	public MusicBot() throws LoginException {

		cmdmanager = new CommandManager();
		cmdmanager.registerCommand("panel", new PanelCommand());
		cmdmanager.registerCommand("volume", new VolumeCommand());



		Instance = this;
		configFile = new File("config.yml");
		initYAMLConfig(configFile);

		JDABuilder builder = JDABuilder.createLight(configuration.getString("TOKEN"));
		builder.enableCache(CacheFlag.VOICE_STATE);
		builder.setMemberCachePolicy(MemberCachePolicy.ALL);
		builder.enableIntents(GatewayIntent.GUILD_MEMBERS);
		builder.enableIntents(GatewayIntent.GUILD_VOICE_STATES);


		builder.setStatus(OnlineStatus.ONLINE);
		builder.addEventListeners(cmdmanager);
		builder.addEventListeners(new ReactionManager());
		builder.addEventListeners(new MessageReceivePlayHandler());
		this.bot = builder.build();

		this.audioManager = new AudioManager(this);

	}

	public static void main(String[] args) {
		try {
			new MusicBot();
		} catch (LoginException e) {
			e.printStackTrace();
		}
	}

	private void initYAMLConfig(File file) {
		try {
			boolean isnew = false;
			if (!file.exists()) {
				isnew = true;
				file.createNewFile();
			}
			 this.configuration = YamlConfiguration.loadConfiguration(file);
			if (isnew) {
				configuration.set("TOKEN", null);
				try {
					configuration.save(configFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}catch (IOException e) {
			e.printStackTrace();
			try {
				Thread.sleep(5000);
			} catch (InterruptedException interruptedException) {
				interruptedException.printStackTrace();
			}
		}
	}

	public static String getTimestamp(long milliseconds)
	{

		int seconds = (int) (milliseconds / 1000) % 60 ;
		int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
		int hours   = (int) ((milliseconds / (1000 * 60 * 60)) % 24);

		if (hours > 0)
			return String.format("%02d:%02d:%02d", hours, minutes, seconds);
		else
			return String.format("%02d:%02d", minutes, seconds);
	}


	public JDA getBot() {
		return bot;
	}

	public static MusicBot getInstance() {
		return Instance;
	}

	public YamlConfiguration getConfiguration() {
		return configuration;
	}

	public File getConfigFile() {
		return configFile;
	}

	public AudioManager getAudioManager() {
		return audioManager;
	}
}
