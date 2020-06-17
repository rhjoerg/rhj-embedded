package ch.rhj.embedded.maven.factory;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.execution.DefaultMavenExecutionResult;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.execution.MavenSession;
import org.codehaus.plexus.PlexusContainer;
import org.eclipse.aether.RepositorySystemSession;

@Named
public class MavenSessionFactory
{
	private final PlexusContainer container;

	@Inject
	public MavenSessionFactory(PlexusContainer container)
	{
		this.container = container;
	}

	@SuppressWarnings("deprecation")
	public MavenSession createMavenSession(MavenExecutionRequest executionRequest)
	{
		RepositorySystemSession repoSession = executionRequest.getProjectBuildingRequest().getRepositorySession();
		MavenExecutionResult result = new DefaultMavenExecutionResult();

		return new MavenSession(container, repoSession, executionRequest, result);
	}
}
