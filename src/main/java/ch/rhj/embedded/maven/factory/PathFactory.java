package ch.rhj.embedded.maven.factory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.inject.Named;

@Named
public class PathFactory
{
	private static final Path GLOBAL_SETTINGS_PATH = //
			Paths.get(System.getProperty("user.home"), ".m2", "settings.xml").toAbsolutePath();

	public Path createBasePath(Path pomPath)
	{
		return pomPath.getParent().toAbsolutePath().normalize();
	}

	public Path createUserPropertiesPath(Path pomPath)
	{
		return createBasePath(pomPath).resolve("pom.properties");
	}

	public Path createGlobalSettingsPath()
	{
		return GLOBAL_SETTINGS_PATH;
	}

	public Path createUserSettingsPath(Path pomPath)
	{
		Path basePath = createBasePath(pomPath);
		Path path = basePath.resolve("settings.xml");

		if (Files.exists(path))
		{
			return path;
		}

		path = basePath.resolve(Paths.get(".m2", "settings"));

		if (Files.exists(path))
		{
			return path;
		}

		return null;
	}
}
