package ch.rhj.embedded.maven.factory;

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
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Profile;
import org.apache.maven.settings.Settings;
import org.codehaus.plexus.PlexusContainer;

import ch.rhj.embedded.maven.build.ProjectRepository;

@Named
public class ExecutionRequestFactory
{
	private final PlexusContainer container;
	private final PropertiesFactory propertiesFactory;
	private final PathFactory pathFactory;
	private final SettingsFactory settingsFactory;
	private final ProfilesFactory profilesFactory;
	private final RepositoryFactory repositoryFactory;
	private final MavenExecutionRequestPopulator requestPopulator;

	@Inject
	public ExecutionRequestFactory(PlexusContainer container, PropertiesFactory propertiesFactory, PathFactory pathFactory, SettingsFactory settingsFactory,
			ProfilesFactory profilesFactory, RepositoryFactory repositoryFactory, MavenExecutionRequestPopulator requestPopulator)
	{
		this.container = container;
		this.propertiesFactory = propertiesFactory;
		this.pathFactory = pathFactory;
		this.settingsFactory = settingsFactory;
		this.profilesFactory = profilesFactory;
		this.repositoryFactory = repositoryFactory;
		this.requestPopulator = requestPopulator;
	}

	public DefaultMavenExecutionRequest createExecutionRequest(Path pomPath, String... goals) throws Exception
	{
		DefaultMavenExecutionRequest request = new DefaultMavenExecutionRequest();

		pomPath = pomPath.toAbsolutePath().normalize();
		populate(request, pomPath, goals);

		return request;
	}

	public DefaultMavenExecutionRequest createExecutionRequest(MavenProject project, String... goals) throws Exception
	{
		return createExecutionRequest(project.getFile().toPath(), goals);
	}

	private void populate(DefaultMavenExecutionRequest request, Path pomPath, String[] goals) throws Exception
	{
		Settings settings = settingsFactory.createSettings(pomPath);
		List<Profile> profiles = profilesFactory.activeProfiles(settings);

		populateCommons(request, pomPath, goals);
		populateFromSettings(request, settings);
		populateFromProfiles(request, profiles);
		populateRepositories(request, settings, profiles);

		requestPopulator.populateDefaults(request);
	}

	private void populateCommons(DefaultMavenExecutionRequest request, Path pomPath, String[] goals) throws Exception
	{
		File basedir = pathFactory.createBasePath(pomPath).toFile();

		request.setLoggingLevel(LOGGING_LEVEL_INFO);

		request.setSystemProperties(propertiesFactory.createSystemProperties());
		request.setUserProperties(propertiesFactory.createUserProperties(pomPath));

		request.setPom(pomPath.toFile());
		request.setBaseDirectory(basedir);
		request.setMultiModuleProjectDirectory(basedir);

		request.setGoals(List.of(goals));
	}

	private void populateFromSettings(DefaultMavenExecutionRequest request, Settings settings)
	{
		request.setInteractiveMode(settings.isInteractiveMode());
		request.setOffline(settings.isOffline());

		request.setMirrors(settings.getMirrors());
		request.setServers(settings.getServers());
		request.setProxies(settings.getProxies());

		request.setPluginGroups(settings.getPluginGroups());
	}

	private void populateFromProfiles(DefaultMavenExecutionRequest request, List<Profile> profiles)
	{
		request.setProfiles(profiles.stream().map(p -> convertFromSettingsProfile(p)).collect(toList()));
		request.setActiveProfiles(profiles.stream().map(p -> p.getId()).collect(toList()));
	}

	private void populateRepositories(DefaultMavenExecutionRequest request, Settings settings, List<Profile> profiles) throws Exception
	{
		ArtifactRepository localRepository = repositoryFactory.createLocalRepository(settings);
		List<ArtifactRepository> remoteRepositories = new ArrayList<>();
		List<ArtifactRepository> pluginArtifactRepositories = new ArrayList<>();
		ProjectRepository projectRepository = container.lookup(ProjectRepository.class);

		remoteRepositories.add(projectRepository);
		remoteRepositories.addAll(repositoryFactory.createRepositories(profiles));

		pluginArtifactRepositories.add(projectRepository);
		pluginArtifactRepositories.add(localRepository);
		pluginArtifactRepositories.addAll(remoteRepositories);

		request.setLocalRepository(localRepository);
		request.setRemoteRepositories(remoteRepositories);
		request.setPluginArtifactRepositories(pluginArtifactRepositories);
	}
}
