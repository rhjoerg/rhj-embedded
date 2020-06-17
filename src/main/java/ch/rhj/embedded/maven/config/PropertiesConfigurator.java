package ch.rhj.embedded.maven.config;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import javax.inject.Named;

import ch.rhj.embedded.maven.context.MavenContext;

@Named
public class PropertiesConfigurator
{
	public void configure(MavenContext context) throws Exception
	{
		context.systemProperties(createSystemProperties());
		context.userProperties(createUserProperties(context));
	}

	private Properties createSystemProperties()
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

	private Properties createUserProperties(MavenContext context) throws Exception
	{
		Properties result = new Properties();
		Path path = context.basedir().resolve("pom.properties");

		if (Files.exists(path))
		{
			try (InputStream input = Files.newInputStream(path))
			{
				result.load(input);
			}
		}

		return result;
	}
}
