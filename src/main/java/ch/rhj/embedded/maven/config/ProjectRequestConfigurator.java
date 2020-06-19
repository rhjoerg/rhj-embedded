package ch.rhj.embedded.maven.config;

import static org.apache.maven.model.building.ModelBuildingRequest.VALIDATION_LEVEL_STRICT;
import static org.apache.maven.project.ProjectBuildingRequest.RepositoryMerging.POM_DOMINANT;

import java.util.List;

import javax.inject.Named;

import org.apache.maven.project.DefaultProjectBuildingRequest;

import ch.rhj.embedded.maven.context.ContextProfiles;
import ch.rhj.embedded.maven.context.ContextRepositories;
import ch.rhj.embedded.maven.context.MavenContext;

@Named
public class ProjectRequestConfigurator implements MavenConfigurator
{
	@Override
	public List<Integer> positions()
	{
		return ConfiguratorPositions.PROJECT_REQUEST_CONFIGURATOR_POSITIONS;
	}

	@Override
	public void configure(MavenContext context) throws Exception
	{
		DefaultProjectBuildingRequest request = new DefaultProjectBuildingRequest();
		ContextProfiles profiles = context.profiles();
		ContextRepositories repositories = context.repositories();

		request.setProfiles(profiles.allAsModelProfiles());
		request.setActiveProfileIds(profiles.activeProfileIds());
		request.setInactiveProfileIds(profiles.inactiveProfileIds());

		request.setSystemProperties(context.systemProperties());
		request.setUserProperties(context.userProperties());

		request.setLocalRepository(repositories.localRepository());
		request.setRemoteRepositories(repositories.remoteRepositories());
		request.setPluginArtifactRepositories(repositories.pluginRepositories());
		request.setRepositorySession(context.repositorySession());
		request.setRepositoryMerging(POM_DOMINANT);

		request.setResolveDependencies(true);
		request.setProcessPlugins(true);
		request.setValidationLevel(VALIDATION_LEVEL_STRICT);

		context.projectRequest(request);
	}
}
