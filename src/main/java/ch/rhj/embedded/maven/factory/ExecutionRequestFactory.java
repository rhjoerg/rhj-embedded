package ch.rhj.embedded.maven.factory;

import static java.util.stream.Collectors.toList;
import static org.apache.maven.execution.MavenExecutionRequest.LOGGING_LEVEL_INFO;
import static org.apache.maven.settings.SettingsUtils.convertFromSettingsProfile;

import java.io.File;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequestPopulator;
import org.apache.maven.settings.Profile;
import org.apache.maven.settings.Settings;

import ch.rhj.embedded.maven.context.MavenContext;
import ch.rhj.embedded.maven.factory.repository.RepositoryFactory;
import ch.rhj.embedded.maven.factory.repository.RepositoryResult;

@Named
public class ExecutionRequestFactory
{
	private final ProfilesFactory profilesFactory;
	private final RepositoryFactory repositoryFactory;
	private final MavenExecutionRequestPopulator requestPopulator;

	@Inject
	public ExecutionRequestFactory(ProfilesFactory profilesFactory, RepositoryFactory repositoryFactory, MavenExecutionRequestPopulator requestPopulator)
	{
		this.profilesFactory = profilesFactory;
		this.repositoryFactory = repositoryFactory;
		this.requestPopulator = requestPopulator;
	}

	public DefaultMavenExecutionRequest createExecutionRequest(MavenContext context) throws Exception
	{
		DefaultMavenExecutionRequest request = new DefaultMavenExecutionRequest();

		populate(request, context);

		return request;
	}

	private void populate(DefaultMavenExecutionRequest request, MavenContext context) throws Exception
	{
		Settings settings = context.settings();
		List<Profile> profiles = profilesFactory.createSettingsProfiles(context);

		populateCommons(request, context);
		populateFromSettings(request, settings);
		populateFromProfiles(request, profiles);
		populateRepositories(request, context);

		requestPopulator.populateDefaults(request);
	}

	private void populateCommons(DefaultMavenExecutionRequest request, MavenContext context) throws Exception
	{
		File basedir = context.basedir().toFile();

		request.setLoggingLevel(LOGGING_LEVEL_INFO);

		request.setSystemProperties(context.systemProperties());
		request.setUserProperties(context.userProperties());

		request.setPom(context.pomPath().toFile());
		request.setBaseDirectory(basedir);
		request.setMultiModuleProjectDirectory(basedir);

		request.setGoals(List.of(context.goals()));
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

	private void populateRepositories(DefaultMavenExecutionRequest request, MavenContext context) throws Exception
	{
		RepositoryResult repositories = repositoryFactory.createRepositories(context, true);

		request.setLocalRepository(repositories.localRepository());
		request.setRemoteRepositories(repositories.remoteRepositories());
		request.setPluginArtifactRepositories(repositories.pluginRepositories());
	}
}
