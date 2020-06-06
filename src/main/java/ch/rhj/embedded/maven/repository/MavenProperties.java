package ch.rhj.embedded.maven.repository;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.stream.Stream;

import javax.inject.Named;

@Named
public class MavenProperties
{
	public static final Path GLOBAL_SETTINGS = Paths.get(System.getProperty("user.home"), ".m2", "settings.xml");

	public File globalSettingsFile()
	{
		return Files.exists(GLOBAL_SETTINGS) ? GLOBAL_SETTINGS.toFile() : null;
	}

	public File userSettingsFile(Path pomPath)
	{
		Path directory = pomPath.getParent().toAbsolutePath().normalize();

		Path path1 = directory.resolve("settings.xml");
		Path path2 = directory.resolve(".m2").resolve("settings.xml");
		Path path = Stream.of(path1, path2).filter(p -> Files.exists(p)).findFirst().orElse(null);

		return path == null ? null : path.toFile();
	}

	public Properties systemProperties()
	{
		Properties result = new Properties();
		Properties systemProperties = System.getProperties();

		synchronized (systemProperties)
		{
			result.putAll(systemProperties);
		}

		System.getenv().forEach((key, value) -> result.setProperty("env." + key.toUpperCase(), value));

		return result;
	}

	public Properties userProperties(Path pomPath) throws IOException
	{
		Properties result = new Properties();
		Path propertiesPath = pomPath.getParent().resolve("pom.properties");

		if (Files.exists(propertiesPath))
		{
			try (InputStream input = Files.newInputStream(propertiesPath))
			{
				result.load(input);
			}
		}

		return result;
	}
}
