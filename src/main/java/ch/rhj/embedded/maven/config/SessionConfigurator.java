package ch.rhj.embedded.maven.config;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.execution.DefaultMavenExecutionResult;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.execution.MavenSession;
import org.codehaus.plexus.PlexusContainer;
import org.eclipse.aether.RepositorySystemSession;

import ch.rhj.embedded.maven.context.MavenContext;

@Named
public class SessionConfigurator implements MavenConfigurator
{
	private final PlexusContainer container;

	@Inject
	public SessionConfigurator(PlexusContainer container)
	{
		this.container = container;
	}

	@Override
	public List<Integer> positions()
	{
		return ConfiguratorPositions.SESSION_CONFIGURATOR_POSITIONS;
	}

	@Override
	public void configure(MavenContext context) throws Exception
	{
		RepositorySystemSession repositorySession = context.repositorySession();
		MavenExecutionRequest request = context.executionRequest();
		MavenExecutionResult result = new DefaultMavenExecutionResult();

		@SuppressWarnings("deprecation")
		MavenSession mavenSession = new MavenSession(container, repositorySession, request, result);

		context.mavenSession(mavenSession);
	}

}
