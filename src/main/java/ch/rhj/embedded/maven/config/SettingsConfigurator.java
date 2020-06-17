package ch.rhj.embedded.maven.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuilder;
import org.apache.maven.settings.building.SettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuildingResult;

import ch.rhj.embedded.maven.context.MavenContext;

@Named
public class SettingsConfigurator
{
	public static final Path USER_SETTINGS_PATH = //
			Paths.get(System.getProperty("user.home"), ".m2", "settings.xml").toAbsolutePath();

	private final SettingsBuilder settingsBuilder;

	@Inject
	public SettingsConfigurator(SettingsBuilder settingsBuilder)
	{
		this.settingsBuilder = settingsBuilder;
	}

	public void configure(MavenContext context) throws Exception
	{
		SettingsBuildingRequest request = createSettingsBuildingRequest(context);
		SettingsBuildingResult result = settingsBuilder.build(request);

		validateSettingsBuildingResult(result);

		context.settings(result.getEffectiveSettings());
	}

	private void validateSettingsBuildingResult(SettingsBuildingResult result) throws Exception
	{
		Optional<Exception> exception = result.getProblems().stream() //
				.filter(p -> p.getException() != null) //
				.findFirst().map(p -> p.getException());

		if (exception.isPresent())
		{
			throw exception.get();
		}
	}

	private SettingsBuildingRequest createSettingsBuildingRequest(MavenContext context)
	{
		DefaultSettingsBuildingRequest request = new DefaultSettingsBuildingRequest();

		request.setSystemProperties(context.systemProperties());
		request.setUserProperties(context.userProperties());

		request.setUserSettingsFile(findSettingsPath(context).toFile());

		return request;
	}

	private Path findSettingsPath(MavenContext context)
	{
		Path path = context.basedir().resolve("settings.xml");

		if (Files.exists(path))
		{
			return path;
		}

		if (Files.exists(USER_SETTINGS_PATH))
		{
			return USER_SETTINGS_PATH;
		}

		return null;
	}
}
