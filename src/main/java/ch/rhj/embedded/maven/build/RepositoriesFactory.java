package ch.rhj.embedded.maven.build;

import static java.util.stream.Collectors.toList;
import static org.apache.maven.bridge.MavenRepositorySystem.fromSettingsRepository;

import java.io.File;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.artifact.InvalidRepositoryException;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.model.Repository;
import org.apache.maven.repository.RepositorySystem;
import org.apache.maven.settings.Profile;
import org.apache.maven.settings.Settings;

@Named
public class RepositoriesFactory
{
	private final RepositorySystem repositorySystem;

	@Inject
	public RepositoriesFactory(RepositorySystem repositorySystem)
	{
		this.repositorySystem = repositorySystem;
	}

	public ArtifactRepository localRepository(Settings settings) throws Exception
	{
		String name = settings.getLocalRepository();

		if (name == null)
		{
			return repositorySystem.createDefaultLocalRepository();
		}
		else
		{
			return repositorySystem.createLocalRepository(new File(name));
		}
	}

	public List<ArtifactRepository> remoteRepositories(List<Profile> profiles)
	{
		return profiles.stream() //
				.flatMap(p -> p.getRepositories().stream()) //
				.map(r -> fromSettingsRepository(r)) //
				.map(this::artifactRepository) //
				.filter(r -> r != null) //
				.collect(toList());
	}

	private ArtifactRepository artifactRepository(Repository repository)
	{
		try
		{
			return repositorySystem.buildArtifactRepository(repository);
		}
		catch (InvalidRepositoryException e)
		{
			return null;
		}
	}
}
