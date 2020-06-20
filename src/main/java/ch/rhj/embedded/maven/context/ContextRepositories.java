package ch.rhj.embedded.maven.context;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.RepositoryUtils;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.eclipse.aether.repository.RemoteRepository;

import ch.rhj.embedded.maven.repository.ProjectRepository;

public class ContextRepositories
{
	private final ArtifactRepository localRepository;
	private final ProjectRepository projectRepository;

	private final List<ArtifactRepository> remoteRepositories = new ArrayList<>();

	public ContextRepositories(ArtifactRepository localRepository, ProjectRepository projectRepository)
	{
		this.localRepository = localRepository;
		this.projectRepository = projectRepository;
	}

	public void addRemoteRepository(ArtifactRepository repository)
	{
		this.remoteRepositories.add(repository);
	}

	public ArtifactRepository localRepository()
	{
		return localRepository;
	}

	public List<ArtifactRepository> remoteRepositories()
	{
		List<ArtifactRepository> result = new ArrayList<>();

		result.add(projectRepository);
		result.addAll(remoteRepositories);

		return result;
	}

	public List<ArtifactRepository> pluginRepositories()
	{
		List<ArtifactRepository> result = new ArrayList<>();

		result.addAll(remoteRepositories);
		result.add(projectRepository);
		result.add(localRepository);
		result.addAll(remoteRepositories);

		return result;
	}

	public List<RemoteRepository> aetherRepositories()
	{
		return remoteRepositories().stream().map(r -> RepositoryUtils.toRepo(r)).collect(toList());
	}
}
