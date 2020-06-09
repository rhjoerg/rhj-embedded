package ch.rhj.embedded.maven;

import static java.util.stream.Collectors.toList;
import static org.apache.maven.execution.MavenExecutionRequest.LOGGING_LEVEL_INFO;
import static org.apache.maven.settings.SettingsUtils.convertFromSettingsProfile;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.Maven;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequestPopulator;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.settings.Profile;
import org.apache.maven.settings.Settings;
import org.codehaus.plexus.PlexusContainer;

import ch.rhj.embedded.maven.build.ProfilesFactory;
import ch.rhj.embedded.maven.build.RepositoriesFactory;
import ch.rhj.embedded.maven.repository.MavenProperties;
import ch.rhj.embedded.maven.repository.SettingsRepository;

@Named
public class Builder
{
	private final Maven maven;

	private final MavenProperties mavenProperties;

	private final SettingsRepository settingsRepository;

	private final MavenExecutionRequestPopulator requestPopulator;

	private final ProfilesFactory profilesFactory;

	private final RepositoriesFactory repositoriesFactory;

	@Inject
	public Builder(PlexusContainer container) throws Exception
	{
		this.maven = container.lookup(Maven.class);
		this.mavenProperties = container.lookup(MavenProperties.class);
		this.settingsRepository = container.lookup(SettingsRepository.class);
		this.requestPopulator = container.lookup(MavenExecutionRequestPopulator.class);
		this.profilesFactory = container.lookup(ProfilesFactory.class);
		this.repositoriesFactory = container.lookup(RepositoriesFactory.class);
	}

	public void build(Path pomPath, String... goals) throws Exception
	{
		MavenExecutionRequest request = request(pomPath, goals);
		MavenExecutionResult result = maven.execute(request);
		Exception exception = exception(result);

		if (exception != null)
		{
			throw exception;
		}
	}

	public DefaultMavenExecutionRequest request(Path pomPath, String[] goals) throws Exception
	{
		DefaultMavenExecutionRequest request = new DefaultMavenExecutionRequest();
		Settings settings = settingsRepository.get(pomPath);
		List<Profile> profiles = profilesFactory.activeProfiles(settings);
		File basedir = pomPath.getParent().toFile();

		request.setInteractiveMode(settings.isInteractiveMode());
		request.setLoggingLevel(LOGGING_LEVEL_INFO);
		request.setOffline(settings.isOffline());

		request.setSystemProperties(mavenProperties.systemProperties());
		request.setUserProperties(mavenProperties.userProperties(pomPath));

		request.setMirrors(settings.getMirrors());
		request.setServers(settings.getServers());
		request.setProxies(settings.getProxies());

		request.setPom(pomPath.toFile());
		request.setBaseDirectory(basedir);
		request.setMultiModuleProjectDirectory(basedir);

		request.setPluginGroups(settings.getPluginGroups());

		request.setGoals(List.of(goals));

		request.setProfiles(profiles.stream().map(p -> convertFromSettingsProfile(p)).collect(toList()));
		request.setActiveProfiles(profiles.stream().map(p -> p.getId()).collect(toList()));

		List<ArtifactRepository> pluginArtifactRepositories = new ArrayList<>();

		request.setLocalRepository(repositoriesFactory.localRepository(settings));
		pluginArtifactRepositories.add(request.getLocalRepository());

		request.setRemoteRepositories(repositoriesFactory.remoteRepositories(profiles));
		pluginArtifactRepositories.addAll(request.getRemoteRepositories());

		request.setPluginArtifactRepositories(pluginArtifactRepositories);

		requestPopulator.populateDefaults(request);

		return request;
	}

	private Exception exception(MavenExecutionResult result)
	{
		List<Throwable> exceptions = result.getExceptions();

		exceptions.forEach(e -> e.printStackTrace());

		return exceptions.stream().filter(e -> e instanceof Exception).map(e -> (Exception) e).findFirst().orElse(null);
	}
}
