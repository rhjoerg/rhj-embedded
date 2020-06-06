package ch.rhj.embedded.maven.repository;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuilder;
import org.apache.maven.settings.building.SettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuildingResult;

@Named
public class SettingsRepository
{
	private final SettingsBuilder settingsBuilder;

	private final MavenProperties mavenProperties;

	private final Map<Path, Settings> settingsCache = new HashMap<>();

	@Inject
	public SettingsRepository(SettingsBuilder settingsBuilder, MavenProperties mavenProperties)
	{
		this.settingsBuilder = settingsBuilder;
		this.mavenProperties = mavenProperties;
	}

	public synchronized Settings get(Path pomPath) throws Exception
	{
		pomPath = pomPath.toAbsolutePath().normalize();

		Settings settings = settingsCache.get(pomPath);

		if (settings == null)
		{
			settings = createSettings(pomPath);
			settingsCache.put(pomPath, settings);
		}

		return settings;
	}

	private Settings createSettings(Path pomPath) throws Exception
	{
		DefaultSettingsBuildingRequest request = new DefaultSettingsBuildingRequest();

		request.setGlobalSettingsFile(mavenProperties.globalSettingsFile());
		request.setUserSettingsFile(mavenProperties.userSettingsFile(pomPath));
		request.setSystemProperties(mavenProperties.systemProperties());
		request.setUserProperties(mavenProperties.userProperties(pomPath));

		return createSettings(request);
	}

	private Settings createSettings(SettingsBuildingRequest request) throws Exception
	{
		SettingsBuildingResult result = settingsBuilder.build(request);
		Optional<Exception> exception = result.getProblems().stream().filter(p -> p.getException() != null).findFirst().map(p -> p.getException());

		if (exception.isPresent())
		{
			throw exception.get();
		}

		return result.getEffectiveSettings();
	}
}
