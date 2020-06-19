package ch.rhj.embedded.maven.config;

import static org.apache.maven.execution.MavenExecutionRequest.LOGGING_LEVEL_INFO;

import java.io.File;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequestPopulator;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Settings;

import ch.rhj.embedded.maven.context.ContextProfiles;
import ch.rhj.embedded.maven.context.ContextRepositories;
import ch.rhj.embedded.maven.context.MavenContext;

@Named
public class ExecutionRequestConfigurator implements MavenConfigurator
{
	private final MavenExecutionRequestPopulator requestPopulator;

	@Inject
	public ExecutionRequestConfigurator(MavenExecutionRequestPopulator requestPopulator)
	{
		this.requestPopulator = requestPopulator;
	}

	@Override
	public List<Integer> positions()
	{
		return ConfiguratorPositions.EXECUTION_REQUEST_CONFIGURATOR_POSITIONS;
	}

	@Override
	public void configure(MavenContext context) throws Exception
	{
		DefaultMavenExecutionRequest request = new DefaultMavenExecutionRequest();

		configureCommons(request, context);
		configureSettings(request, context.settings());
		configureProfiles(request, context.profiles());
		configureRepositories(request, context.repositories());
		configureProjectBuilding(request, context);

		requestPopulator.populateDefaults(request);

		context.executionRequest(request);
	}

	private void configureCommons(DefaultMavenExecutionRequest request, MavenContext context)
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

	private void configureSettings(DefaultMavenExecutionRequest request, Settings settings)
	{
		request.setInteractiveMode(settings.isInteractiveMode());
		request.setOffline(settings.isOffline());

		request.setMirrors(settings.getMirrors());
		request.setServers(settings.getServers());
		request.setProxies(settings.getProxies());

		request.setPluginGroups(settings.getPluginGroups());
	}

	private void configureProfiles(DefaultMavenExecutionRequest request, ContextProfiles profiles)
	{
		request.setProfiles(profiles.allProfiles());
		request.setActiveProfiles(profiles.activeProfileIds());
		request.setInactiveProfiles(profiles.inactiveProfileIds());
	}

	private void configureRepositories(DefaultMavenExecutionRequest request, ContextRepositories repositories)
	{
		request.setLocalRepository(repositories.localRepository());
		request.setRemoteRepositories(repositories.remoteRepositories());
		request.setPluginArtifactRepositories(repositories.pluginRepositories());
	}

	private void configureProjectBuilding(DefaultMavenExecutionRequest request, MavenContext context)
	{
		MavenProject project = context.project();

		if (project != null)
		{
			request.setProjectBuildingConfiguration(context.projectRequest());
			request.setSelectedProjects(List.of(project.getId()));
			request.setProjectPresent(true);
		}
	}
}
