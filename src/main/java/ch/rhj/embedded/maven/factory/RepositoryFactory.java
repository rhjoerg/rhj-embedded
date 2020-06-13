package ch.rhj.embedded.maven.factory;

import static java.util.stream.Collectors.toList;
import static org.apache.maven.artifact.repository.ArtifactRepositoryPolicy.CHECKSUM_POLICY_IGNORE;
import static org.apache.maven.artifact.repository.ArtifactRepositoryPolicy.UPDATE_POLICY_ALWAYS;
import static org.apache.maven.bridge.MavenRepositorySystem.fromSettingsRepository;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.artifact.InvalidRepositoryException;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.MavenArtifactRepository;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.model.Repository;
import org.apache.maven.repository.RepositorySystem;
import org.apache.maven.settings.Profile;
import org.apache.maven.settings.Settings;

@Named
public class RepositoryFactory
{
	private final RepositorySystem repositorySystem;
	private final LayoutProvider layoutProvider;

	@Inject
	public RepositoryFactory(RepositorySystem repositorySystem, LayoutProvider layoutProvider)
	{
		this.repositorySystem = repositorySystem;
		this.layoutProvider = layoutProvider;
	}

	public String createUrl(Path repositoryPath) throws Exception
	{
		return repositoryPath.toAbsolutePath().normalize().toUri().toURL().toString();
	}

	public ArtifactRepositoryPolicy createPolicy()
	{
		return new ArtifactRepositoryPolicy(true, UPDATE_POLICY_ALWAYS, CHECKSUM_POLICY_IGNORE);
	}

	public ArtifactRepository createLocalRepository(Settings settings) throws Exception
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

	public ArtifactRepository createRepository(Repository repository)
	{
		try
		{
			return repositorySystem.buildArtifactRepository(repository);
		}
		catch (InvalidRepositoryException e)
		{
			throw new RuntimeException(e);
		}
	}

	public List<ArtifactRepository> createRepositories(List<Profile> profiles)
	{
		return profiles.stream() //
				.flatMap(p -> p.getRepositories().stream()) //
				.map(r -> fromSettingsRepository(r)) //
				.map(this::createRepository) //
				.collect(toList());
	}

	public MavenArtifactRepository createRepository(String id, Path repositoryPath, ArtifactRepositoryLayout layout, ArtifactRepositoryPolicy snapshots,
			ArtifactRepositoryPolicy releases) throws Exception
	{
		String url = createUrl(repositoryPath);

		return new MavenArtifactRepository(id, url, layout, snapshots, releases);
	}

	public MavenArtifactRepository createRepository(String id, Path repositoryPath) throws Exception
	{
		ArtifactRepositoryLayout layout = layoutProvider.getDefaultLayout();
		ArtifactRepositoryPolicy policy = createPolicy();

		return createRepository(id, repositoryPath, layout, policy, policy);
	}
}
