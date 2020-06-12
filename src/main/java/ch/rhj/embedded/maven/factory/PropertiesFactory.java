package ch.rhj.embedded.maven.factory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class PropertiesFactory
{
	private final PathFactory pathFactory;

	@Inject
	public PropertiesFactory(PathFactory pathFactory)
	{
		this.pathFactory = pathFactory;
	}

	public Properties createSystemProperties()
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

	public Properties createUserProperties(Path pomPath) throws IOException
	{
		Properties properties = new Properties();
		Path path = pathFactory.createUserPropertiesPath(pomPath);

		if (Files.exists(path))
		{
			try (InputStream input = Files.newInputStream(path))
			{
				properties.load(input);
			}
		}

		return properties;
	}
}
