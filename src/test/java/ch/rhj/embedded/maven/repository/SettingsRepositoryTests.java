package ch.rhj.embedded.maven.repository;

import static ch.rhj.embedded.maven.MavenTestsConstants.EMBEDDED_POM;
import static ch.rhj.embedded.maven.MavenTestsConstants.PLUGIN_POM;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.inject.Inject;

import org.apache.maven.settings.Settings;
import org.junit.jupiter.api.Test;

import ch.rhj.embedded.maven.WithMaven;

@WithMaven
public class SettingsRepositoryTests
{
	@Inject
	private SettingsRepository settingsRepository;

	@Test
	public void testEmbedded() throws Exception
	{
		Settings settings = settingsRepository.get(EMBEDDED_POM);

		assertTrue(settings.getActiveProfiles().size() > 0);
	}

	@Test
	public void testPlugin() throws Exception
	{
		Settings settings = settingsRepository.get(PLUGIN_POM);

		assertTrue(settings.getActiveProfiles().size() > 0);
	}
}
