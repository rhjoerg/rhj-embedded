package ch.rhj.embedded.maven.factory.model;

import static java.util.stream.Collectors.toList;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.model.Profile;
import org.apache.maven.model.building.DefaultModelBuildingRequest;
import org.apache.maven.model.resolution.ModelResolver;
import org.eclipse.aether.RepositorySystemSession;

import ch.rhj.embedded.maven.context.MavenContext;
import ch.rhj.embedded.maven.factory.ProfilesFactory;
import ch.rhj.embedded.maven.factory.repository.RepositoryFactory;
import ch.rhj.embedded.maven.factory.repository.RepositoryResult;

@Named
public class ModelRequestFactory
{
	private final RepositoryFactory repositoryFactory;
	private final ProfilesFactory profilesFactory;
	private final ModelResolverFactory resolverFactory;

	@Inject
	public ModelRequestFactory(RepositoryFactory repositoryFactory, ProfilesFactory profilesFactory, ModelResolverFactory resolverFactory)
	{
		this.repositoryFactory = repositoryFactory;
		this.profilesFactory = profilesFactory;
		this.resolverFactory = resolverFactory;
	}

	public DefaultModelBuildingRequest createRequest(RepositorySystemSession session, MavenContext context) throws Exception
	{
		DefaultModelBuildingRequest request = new DefaultModelBuildingRequest();
		RepositoryResult repositories = repositoryFactory.createRepositories(context, true);
		ModelResolver resolver = resolverFactory.createResolver(session, repositories);

		List<Profile> activeProfiles = profilesFactory.createModelProfiles(context);
		List<String> activeProfileIds = activeProfiles.stream().map(p -> p.getId()).collect(toList());

		request.setPomFile(context.pomPath().toFile());
		request.setProcessPlugins(true);

		request.setProfiles(activeProfiles);
		request.setActiveProfileIds(activeProfileIds);

		request.setSystemProperties(context.systemProperties());
		request.setUserProperties(context.userProperties());

		request.setModelResolver(resolver);

		return request;
	}
}
