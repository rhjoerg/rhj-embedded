package ch.rhj.embedded.maven.config;

import static org.apache.maven.bridge.MavenRepositorySystem.fromSettingsRepository;

import java.io.File;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.Authentication;
import org.apache.maven.model.Repository;
import org.apache.maven.repository.RepositorySystem;
import org.apache.maven.settings.Profile;
import org.apache.maven.settings.Settings;

import ch.rhj.embedded.maven.build.ProjectRepository;
import ch.rhj.embedded.maven.context.ContextAuthentications;
import ch.rhj.embedded.maven.context.ContextRepositories;
import ch.rhj.embedded.maven.context.MavenContext;

@Named
public class RepositoriesConfigurator implements MavenConfigurator
{
	private final RepositorySystem repositorySystem;
	private final ProjectRepository projectRepository;

	@Inject
	public RepositoriesConfigurator(RepositorySystem repositorySystem, ProjectRepository projectRepository)
	{
		this.repositorySystem = repositorySystem;
		this.projectRepository = projectRepository;
	}

	@Override
	public List<Integer> positions()
	{
		return ConfiguratorPositions.REPOSITORIES_CONFIGURATOR_POSITIONS;
	}

	@Override
	public void configure(MavenContext context) throws Exception
	{
		Settings settings = context.settings();
		ContextAuthentications authentications = context.authentications();
		ArtifactRepository localRepository = createLocalRepository(settings);
		ContextRepositories repositories = new ContextRepositories(localRepository, projectRepository);

		try
		{
			context.profiles().activeAsSettingsProfiles() //
					.forEach(profile -> addRemoteRepositories(profile, authentications, repositories));
		}
		catch (RuntimeException e)
		{
			throw Exception.class.cast(e.getCause());
		}

		context.repositories(repositories);
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

	private void addRemoteRepositories(Profile profile, ContextAuthentications authentications, ContextRepositories repositories)
	{
		if (profile.getRepositories() == null)
		{
			return;
		}

		profile.getRepositories().stream() //
				.map(r -> fromSettingsRepository(r)) //
				.map(r -> createRepository(r, authentications)) //
				.forEach(r -> repositories.addRemoteRepository(r));
	}

	private ArtifactRepository createRepository(Repository repository, ContextAuthentications authentications)
	{
		try
		{
			ArtifactRepository result = repositorySystem.buildArtifactRepository(repository);
			Authentication authentication = authentications.authentication(repository.getId());

			result.setAuthentication(authentication);

			return result;
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
