package ch.rhj.embedded.maven.factory.model;

import static java.util.stream.Collectors.toList;

import java.nio.file.Path;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.model.Profile;
import org.apache.maven.model.building.DefaultModelBuildingRequest;
import org.apache.maven.model.resolution.ModelResolver;
import org.apache.maven.settings.Settings;
import org.eclipse.aether.RepositorySystemSession;

import ch.rhj.embedded.maven.factory.ProfilesFactory;
import ch.rhj.embedded.maven.factory.PropertiesFactory;
import ch.rhj.embedded.maven.factory.SettingsFactory;
import ch.rhj.embedded.maven.factory.repository.RepositoryFactory;
import ch.rhj.embedded.maven.factory.repository.RepositoryResult;

@Named
public class ModelRequestFactory
{
	private final SettingsFactory settingsFactory;
	private final RepositoryFactory repositoryFactory;
	private final ProfilesFactory profilesFactory;
	private final PropertiesFactory propertiesFactory;
	private final ModelResolverFactory resolverFactory;

	@Inject
	public ModelRequestFactory(SettingsFactory settingsFactory, RepositoryFactory repositoryFactory, ProfilesFactory profilesFactory,
			PropertiesFactory propertiesFactory, ModelResolverFactory resolverFactory)
	{
		this.settingsFactory = settingsFactory;
		this.repositoryFactory = repositoryFactory;
		this.profilesFactory = profilesFactory;
		this.propertiesFactory = propertiesFactory;
		this.resolverFactory = resolverFactory;
	}

	public DefaultModelBuildingRequest createRequest(RepositorySystemSession session, Path pomPath) throws Exception
	{
		DefaultModelBuildingRequest request = new DefaultModelBuildingRequest();
		Settings settings = settingsFactory.createSettings(pomPath);
		RepositoryResult repositories = repositoryFactory.createRepositories(settings);
		ModelResolver resolver = resolverFactory.createResolver(session, repositories);

		List<Profile> activeProfiles = profilesFactory.createModelProfiles(settings);
		List<String> activeProfileIds = activeProfiles.stream().map(p -> p.getId()).collect(toList());

		request.setPomFile(pomPath.toFile());
		request.setProcessPlugins(true);

		request.setProfiles(activeProfiles);
		request.setActiveProfileIds(activeProfileIds);

		request.setSystemProperties(propertiesFactory.createSystemProperties());
		request.setUserProperties(propertiesFactory.createUserProperties(pomPath));

		request.setModelResolver(resolver);

		return request;
	}
}
