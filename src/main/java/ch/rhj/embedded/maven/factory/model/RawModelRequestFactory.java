package ch.rhj.embedded.maven.factory.model;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.model.building.DefaultModelBuildingRequest;
import org.apache.maven.model.resolution.ModelResolver;
import org.eclipse.aether.RepositorySystemSession;

import ch.rhj.embedded.maven.context.ContextProfiles;
import ch.rhj.embedded.maven.context.MavenContext;
import ch.rhj.embedded.maven.factory.repository.RepositoryFactory;
import ch.rhj.embedded.maven.factory.repository.RepositoryResult;

@Named
public class RawModelRequestFactory
{
	private final RepositoryFactory repositoryFactory;
	private final ModelResolverFactory resolverFactory;

	@Inject
	public RawModelRequestFactory(RepositoryFactory repositoryFactory, ModelResolverFactory resolverFactory)
	{
		this.repositoryFactory = repositoryFactory;
		this.resolverFactory = resolverFactory;
	}

	public DefaultModelBuildingRequest createRequest(RepositorySystemSession session, MavenContext context) throws Exception
	{
		DefaultModelBuildingRequest request = new DefaultModelBuildingRequest();

		request.setPomFile(context.pomPath().toFile());
		request.setProcessPlugins(false);

		request.setSystemProperties(context.systemProperties());
		request.setUserProperties(context.userProperties());

		ContextProfiles profiles = context.profiles();

		request.setProfiles(profiles.allAsModelProfiles());
		request.setActiveProfileIds(profiles.activeProfileIds());

		RepositoryResult repositories = repositoryFactory.createRepositories(context, false);
		ModelResolver modelResolver = resolverFactory.createResolver(session, repositories);

		request.setModelResolver(modelResolver);

		return request;
	}
}
