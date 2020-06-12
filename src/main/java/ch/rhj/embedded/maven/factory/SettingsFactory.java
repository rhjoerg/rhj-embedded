package ch.rhj.embedded.maven.factory;

import java.nio.file.Path;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuilder;
import org.apache.maven.settings.building.SettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuildingResult;

@Named
public class SettingsFactory
{
	private final SettingsBuilder settingsBuilder;

	private final SettingsRequestFactory requestFactory;

	@Inject
	public SettingsFactory(SettingsBuilder settingsBuilder, SettingsRequestFactory requestFactory)
	{
		this.settingsBuilder = settingsBuilder;
		this.requestFactory = requestFactory;
	}

	public Settings createSettings(SettingsBuildingRequest request) throws Exception
	{
		SettingsBuildingResult result = settingsBuilder.build(request);

		validate(result);

		return result.getEffectiveSettings();
	}

	public Settings createSettings(Path pomPath) throws Exception
	{
		DefaultSettingsBuildingRequest request = requestFactory.createSettingsRequest(pomPath);

		return createSettings(request);
	}

	private void validate(SettingsBuildingResult result) throws Exception
	{
		Optional<Exception> exception = result.getProblems().stream() //
				.filter(p -> p.getException() != null) //
				.findFirst().map(p -> p.getException());

		if (exception.isPresent())
		{
			throw exception.get();
		}
	}
}