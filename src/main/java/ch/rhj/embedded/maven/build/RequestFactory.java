package ch.rhj.embedded.maven.build;

import static java.util.stream.Collectors.toList;
import static org.apache.maven.execution.MavenExecutionRequest.LOGGING_LEVEL_INFO;
import static org.apache.maven.settings.SettingsUtils.convertFromSettingsProfile;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequestPopulator;
import org.apache.maven.settings.Profile;
import org.apache.maven.settings.Settings;
import org.codehaus.plexus.PlexusContainer;

import ch.rhj.embedded.maven.repository.MavenProperties;
import ch.rhj.embedded.maven.repository.ProjectArtifactRepository;
import ch.rhj.embedded.maven.repository.SettingsRepository;

@Named
public class RequestFactory
{
	private final MavenProperties mavenProperties;

	private final SettingsRepository settingsRepository;

	private final ProfilesFactory profilesFactory;

	private final RepositoriesFactory repositoriesFactory;

	private final ProjectArtifactRepository projectArtifactRepository;

	private final MavenExecutionRequestPopulator requestPopulator;

	@Inject
	public RequestFactory(PlexusContainer container) throws Exception
	{
		this.mavenProperties = container.lookup(MavenProperties.class);
		this.settingsRepository = container.lookup(SettingsRepository.class);
		this.profilesFactory = container.lookup(ProfilesFactory.class);
		this.repositoriesFactory = container.lookup(RepositoriesFactory.class);
		this.projectArtifactRepository = container.lookup(ProjectArtifactRepository.class);
		this.requestPopulator = container.lookup(MavenExecutionRequestPopulator.class);
	}

	public DefaultMavenExecutionRequest createExecutionRequest(Path pomPath, String[] goals) throws Exception
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

		ArtifactRepository localRepository = repositoriesFactory.localRepository(settings);
		List<ArtifactRepository> remoteRepositories = new ArrayList<>();
		List<ArtifactRepository> pluginArtifactRepositories = new ArrayList<>();

		remoteRepositories.add(projectArtifactRepository);
		remoteRepositories.addAll(repositoriesFactory.remoteRepositories(profiles));

		pluginArtifactRepositories.add(projectArtifactRepository);
		pluginArtifactRepositories.add(localRepository);
		pluginArtifactRepositories.addAll(remoteRepositories);

		request.setLocalRepository(localRepository);
		request.setRemoteRepositories(remoteRepositories);
		request.setPluginArtifactRepositories(pluginArtifactRepositories);

		requestPopulator.populateDefaults(request);

		return request;
	}
}
