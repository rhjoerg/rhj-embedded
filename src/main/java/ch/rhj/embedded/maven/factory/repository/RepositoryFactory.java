package ch.rhj.embedded.maven.factory.repository;

import static org.apache.maven.bridge.MavenRepositorySystem.fromSettingsRepository;

import java.io.File;
import java.nio.file.Path;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.MavenArtifactRepository;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.model.Repository;
import org.apache.maven.repository.RepositorySystem;
import org.apache.maven.settings.Profile;
import org.apache.maven.settings.Settings;
import org.codehaus.plexus.PlexusContainer;

import ch.rhj.embedded.maven.build.ProjectRepository;
import ch.rhj.embedded.maven.context.MavenContext;
import ch.rhj.embedded.maven.factory.LayoutProvider;

@Named
public class RepositoryFactory
{
	private final PlexusContainer container;
	private final RepositorySystem repositorySystem;

	private final LayoutProvider layoutProvider;

	@Inject
	public RepositoryFactory(PlexusContainer container, RepositorySystem repositorySystem, LayoutProvider layoutProvider)
	{
		this.container = container;
		this.repositorySystem = repositorySystem;
		this.layoutProvider = layoutProvider;
	}

	public RepositoryResult createRepositories(MavenContext context, boolean withProjectRepository) throws Exception
	{
		Settings settings = context.settings();
		ArtifactRepository localRepository = createLocalRepository(settings);
		ProjectRepository projectRepository = withProjectRepository ? container.lookup(ProjectRepository.class) : null;
		RepositoryResult result = new RepositoryResult(settings, localRepository, projectRepository);

		try
		{
			context.profiles().activeAsSettingsProfiles().forEach(profile -> addRemoteRepositories(profile, result));
		}
		catch (RuntimeException e)
		{
			throw Exception.class.cast(e.getCause());
		}

		return result;
	}

	private void addRemoteRepositories(Profile profile, RepositoryResult result)
	{
		if (profile.getRepositories() == null)
		{
			return;
		}

		profile.getRepositories().stream() //
				.map(r -> fromSettingsRepository(r)) //
				.map(this::createRepository) //
				.forEach(r -> result.addRemoteRepository(r));
	}

	private ArtifactRepository createLocalRepository(Settings settings) throws Exception
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

	private ArtifactRepository createRepository(Repository repository)
	{
		try
		{
			return repositorySystem.buildArtifactRepository(repository);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public MavenArtifactRepository createRepository(String id, Path repositoryPath, ArtifactRepositoryLayout layout, ArtifactRepositoryPolicy snapshots,
			ArtifactRepositoryPolicy releases) throws Exception
	{
		String url = repositoryPath.toAbsolutePath().normalize().toUri().toURL().toString();

		return new MavenArtifactRepository(id, url, layout, snapshots, releases);
	}

	public MavenArtifactRepository createRepository(String id, Path repositoryPath) throws Exception
	{
		ArtifactRepositoryLayout layout = layoutProvider.getDefaultLayout();
		ArtifactRepositoryPolicy policy = RepositoryPolicies.createDefaultPolicy();

		return createRepository(id, repositoryPath, layout, policy, policy);
	}
}
