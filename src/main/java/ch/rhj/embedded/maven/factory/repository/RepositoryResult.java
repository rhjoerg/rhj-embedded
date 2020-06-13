package ch.rhj.embedded.maven.factory.repository;

import static java.util.stream.Collectors.toList;
import static org.apache.maven.RepositoryUtils.toRepo;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.settings.Settings;
import org.eclipse.aether.repository.RemoteRepository;

import ch.rhj.embedded.maven.build.ProjectRepository;

public class RepositoryResult
{
	private final Settings settings;

	private final ArtifactRepository localRepository;
	private final ProjectRepository projectRepository;

	private final List<ArtifactRepository> remoteRepositories = new ArrayList<>();

	public RepositoryResult(Settings settings, ArtifactRepository localRepository, ProjectRepository projectRepository)
	{
		this.settings = settings;
		this.localRepository = localRepository;
		this.projectRepository = projectRepository;
	}

	public void addRemoteRepository(ArtifactRepository repository)
	{
		this.remoteRepositories.add(repository);
	}

	public Settings settings()
	{
		return settings;
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

		result.add(projectRepository);
		result.add(localRepository);
		result.addAll(remoteRepositories);

		return result;
	}

	public List<RemoteRepository> aetherRepositories()
	{
		return remoteRepositories().stream().map(r -> toRepo(r)).collect(toList());
	}
}
