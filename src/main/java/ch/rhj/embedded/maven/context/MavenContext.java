package ch.rhj.embedded.maven.context;

import java.nio.file.Path;
import java.util.Properties;

import org.apache.maven.settings.Settings;

public class MavenContext
{
	private final Path pomPath;
	private final Path basedir;
	private final String[] goals;

	private Properties systemProperties;
	private Properties userProperties;

	private Settings settings;
	private ContextProfiles profiles;

	public MavenContext(Path pomPath, String... goals)
	{
		this.pomPath = pomPath.toAbsolutePath().normalize();
		this.basedir = this.pomPath.getParent();
		this.goals = goals.clone();
	}

	public Path pomPath()
	{
		return pomPath;
	}

	public Path basedir()
	{
		return basedir;
	}

	public String[] goals()
	{
		return goals.clone();
	}

	public Properties systemProperties()
	{
		return systemProperties;
	}

	public void systemProperties(Properties systemProperties)
	{
		this.systemProperties = systemProperties;

		settings(null);
	}

	public Properties userProperties()
	{
		return userProperties;
	}

	public void userProperties(Properties userProperties)
	{
		this.userProperties = userProperties;

		settings(null);
	}

	public Settings settings()
	{
		return settings;
	}

	public void settings(Settings settings)
	{
		this.settings = settings;

		profiles(null);
	}

	public ContextProfiles profiles()
	{
		return profiles;
	}

	public void profiles(ContextProfiles profiles)
	{
		this.profiles = profiles;
	}
}
