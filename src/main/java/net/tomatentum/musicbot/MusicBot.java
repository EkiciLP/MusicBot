package net.tomatentum.musicbot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.tomatentum.musicbot.command.utils.CommandManager;
import org.bspfsystems.yamlconfiguration.file.YamlConfiguration;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;

public class MusicBot {

	private YamlConfiguration configuration;
	private File configFile;
	private JDA bot;
	private CommandManager cmdmanager;

	private static MusicBot Instance;


	public MusicBot() throws LoginException {
		Instance = this;
		configFile = new File("config.yml");
		configuration = initYAMLConfig(configFile);
		configuration.set("TOKEN", null);
		try {
			configuration.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

		JDABuilder builder = JDABuilder.createLight(configuration.getString("TOKEN"));
		builder.setStatus(OnlineStatus.ONLINE);
		this.bot = builder.build();

		cmdmanager = new CommandManager();





	}

	public static void main(String[] args) {
		try {
			new MusicBot();
		} catch (LoginException e) {
			e.printStackTrace();
		}
	}

	private YamlConfiguration initYAMLConfig(File file) {
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			return YamlConfiguration.loadConfiguration(file);
		}catch (IOException ignored) {}
		return null;
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
}
