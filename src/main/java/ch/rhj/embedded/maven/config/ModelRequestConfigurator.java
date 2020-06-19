package ch.rhj.embedded.maven.config;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.model.building.DefaultModelBuildingRequest;
import org.codehaus.plexus.logging.Logger;

import ch.rhj.embedded.maven.context.ContextProfiles;
import ch.rhj.embedded.maven.context.MavenContext;

@Named
public class ModelRequestConfigurator implements MavenConfigurator
{
	private final Logger logger;

	@Inject
	public ModelRequestConfigurator(Logger logger)
	{
		this.logger = logger;
	}

	@Override
	public List<Integer> positions()
	{
		return ConfiguratorPositions.MODEL_REQUEST_CONFIGURATOR_POSITIONS;
	}

	@Override
	public void configure(MavenContext context) throws Exception
	{
		DefaultModelBuildingRequest request = new DefaultModelBuildingRequest();
		ContextProfiles profiles = context.profiles();

		request.setPomFile(context.pomPath().toFile());
		request.setProcessPlugins(processPlugins(context));

		request.setProfiles(profiles.allAsModelProfiles());
		request.setActiveProfileIds(profiles.activeProfileIds());
		request.setInactiveProfileIds(profiles.inactiveProfileIds());

		request.setSystemProperties(context.systemProperties());
		request.setUserProperties(context.userProperties());

		request.setModelResolver(context.modelResolver());

		context.modelRequest(request);
	}

	private boolean processPlugins(MavenContext context)
	{
		logger.warn("processPlugins determination not yet implemented");

		return false;
	}
}
