package ch.rhj.embedded.maven.config;

import static org.apache.maven.project.ProjectBuildingRequest.RepositoryMerging.REQUEST_DOMINANT;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.project.ProjectModelResolver;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.RequestTrace;
import org.eclipse.aether.impl.RemoteRepositoryManager;
import org.eclipse.aether.repository.RemoteRepository;

import ch.rhj.embedded.maven.context.MavenContext;

@Named
public class ModelResolverConfigurator implements MavenConfigurator
{
	private final RepositorySystem repositorySystem;
	private final RemoteRepositoryManager repositoryManager;

	@Inject
	public ModelResolverConfigurator(RepositorySystem repositorySystem, RemoteRepositoryManager repositoryManager)
	{
		this.repositorySystem = repositorySystem;
		this.repositoryManager = repositoryManager;
	}

	@Override
	public List<Integer> positions()
	{
		return ConfiguratorPositions.MODEL_RESOLVER_CONFIGURATOR_POSITIONS;
	}

	@Override
	public void configure(MavenContext context) throws Exception
	{
		RequestTrace trace = new RequestTrace(null);
		List<RemoteRepository> remoteRepositories = context.repositories().aetherRepositories();
		RepositorySystemSession session = context.repositorySession();

		ProjectModelResolver resolver = new ProjectModelResolver( //
				session, trace, repositorySystem, repositoryManager, remoteRepositories, REQUEST_DOMINANT, null);

		context.modelResolver(resolver);
	}

}
