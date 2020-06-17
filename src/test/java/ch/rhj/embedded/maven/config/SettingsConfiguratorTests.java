package ch.rhj.embedded.maven.config;

import static ch.rhj.embedded.maven.MavenTestsConstants.EMBEDDED_POM;
import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.inject.Inject;

import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.junit.jupiter.api.Test;

import ch.rhj.embedded.maven.WithMaven;
import ch.rhj.embedded.maven.context.MavenContext;

@WithMaven
public class SettingsConfiguratorTests
{
	@Inject
	private PropertiesConfigurator propertiesConfigurator;

	@Inject
	private SettingsConfigurator settingsConfigurator;

	@Test
	public void test() throws Exception
	{
		MavenContext context = new MavenContext(EMBEDDED_POM);

		propertiesConfigurator.configure(context);
		settingsConfigurator.configure(context);

		Settings settings = context.settings();
		Server server = settings.getServer("github");

		assertEquals(System.getenv("GITHUB_ACTOR"), server.getUsername());
		assertEquals(System.getenv("GITHUB_TOKEN"), server.getPassphrase());
	}
}
