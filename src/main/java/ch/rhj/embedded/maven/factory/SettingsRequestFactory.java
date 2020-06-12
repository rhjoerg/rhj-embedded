package ch.rhj.embedded.maven.factory;

import java.nio.file.Path;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;

@Named
public class SettingsRequestFactory
{
	private final PropertiesFactory propertiesFactory;
	private final PathFactory pathFactory;

	@Inject
	public SettingsRequestFactory(PropertiesFactory propertiesFactory, PathFactory pathFactory)
	{
		this.propertiesFactory = propertiesFactory;
		this.pathFactory = pathFactory;
	}

	public DefaultSettingsBuildingRequest createSettingsRequest(Path pomPath) throws Exception
	{
		DefaultSettingsBuildingRequest request = new DefaultSettingsBuildingRequest();

		populate(pomPath, request);

		return request;
	}

	private void populate(Path pomPath, DefaultSettingsBuildingRequest request) throws Exception
	{
		populateProperties(pomPath, request);
		populateFile(pomPath, request);
	}

	private void populateProperties(Path pomPath, DefaultSettingsBuildingRequest request) throws Exception
	{
		request.setSystemProperties(propertiesFactory.createSystemProperties());
		request.setUserProperties(propertiesFactory.createUserProperties(pomPath));
	}

	private void populateFile(Path pomPath, DefaultSettingsBuildingRequest request)
	{
		Path userSettingsPath = pathFactory.createUserSettingsPath(pomPath);

		if (userSettingsPath != null)
		{
			request.setUserSettingsFile(userSettingsPath.toFile());
		}
		else
		{
			Path globalSettingsPath = pathFactory.createGlobalSettingsPath();

			request.setGlobalSettingsFile(globalSettingsPath.toFile());
		}
	}
}
