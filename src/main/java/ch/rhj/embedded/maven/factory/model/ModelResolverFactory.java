package ch.rhj.embedded.maven.factory.model;

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

import ch.rhj.embedded.maven.factory.repository.RepositoryResult;

@Named
public class ModelResolverFactory
{
	private final RepositorySystem repositorySystem;
	private final RemoteRepositoryManager repositoryManager;

	@Inject
	public ModelResolverFactory(RepositorySystem repositorySystem, RemoteRepositoryManager repositoryManager)
	{
		this.repositorySystem = repositorySystem;
		this.repositoryManager = repositoryManager;
	}

	public ProjectModelResolver createResolver(RepositorySystemSession session, RepositoryResult repositories)
	{
		RequestTrace trace = new RequestTrace(null);
		List<RemoteRepository> remoteRepositories = repositories.aetherRepositories();

		return new ProjectModelResolver(session, trace, repositorySystem, repositoryManager, remoteRepositories, REQUEST_DOMINANT, null);
	}
}
