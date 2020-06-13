package ch.rhj.embedded.maven.factory;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.execution.DefaultMavenExecutionResult;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.PlexusContainer;
import org.eclipse.aether.RepositorySystemSession;

@Named
public class MavenSessionFactory
{
	private final PlexusContainer container;
	private final RepositorySessionFactory repositorySessionFactory;
	private final ExecutionRequestFactory executionRequestFactory;

	@Inject
	public MavenSessionFactory(PlexusContainer container, RepositorySessionFactory repositorySessionFactory, ExecutionRequestFactory executionRequestFactory)
	{
		this.container = container;
		this.repositorySessionFactory = repositorySessionFactory;
		this.executionRequestFactory = executionRequestFactory;
	}

	@SuppressWarnings("deprecation")
	public MavenSession createMavenSession(MavenExecutionRequest executionRequest)
	{
		RepositorySystemSession repoSession = repositorySessionFactory.createRepositorySession(executionRequest);
		MavenExecutionResult result = new DefaultMavenExecutionResult();

		return new MavenSession(container, repoSession, executionRequest, result);
	}

	public MavenSession createMavenSession(MavenProject project, String... goals) throws Exception
	{
		MavenExecutionRequest executionRequest = executionRequestFactory.createExecutionRequest(project, goals);

		return createMavenSession(executionRequest);
	}
}
