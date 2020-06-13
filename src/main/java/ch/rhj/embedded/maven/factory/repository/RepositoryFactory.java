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
import org.apache.maven.settings.Settings;
import org.codehaus.plexus.PlexusContainer;

import ch.rhj.embedded.maven.build.ProjectRepository;
import ch.rhj.embedded.maven.factory.LayoutProvider;
import ch.rhj.embedded.maven.factory.ProfilesFactory;

@Named
public class RepositoryFactory
{
	private final PlexusContainer container;
	private final RepositorySystem repositorySystem;

	private final ProfilesFactory profileFactory;

	private final LayoutProvider layoutProvider;

	@Inject
	public RepositoryFactory(PlexusContainer container, RepositorySystem repositorySystem, ProfilesFactory profileFactory, LayoutProvider layoutProvider)
	{
		this.container = container;
		this.repositorySystem = repositorySystem;
		this.profileFactory = profileFactory;
		this.layoutProvider = layoutProvider;
	}

	public RepositoryResult createRepositories(Settings settings) throws Exception
	{
		ArtifactRepository localRepository = createLocalRepository(settings);
		ProjectRepository projectRepository = container.lookup(ProjectRepository.class);
		RepositoryResult result = new RepositoryResult(settings, localRepository, projectRepository);

		try
		{
			profileFactory.createSettingsProfiles(settings).stream() //
					.flatMap(p -> p.getRepositories().stream()) //
					.map(r -> fromSettingsRepository(r)) //
					.map(this::createRepository) //
					.forEach(r -> result.addRemoteRepository(r));
		}
		catch (RuntimeException e)
		{
			throw Exception.class.cast(e.getCause());
		}

		return result;
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
