package ch.rhj.embedded.maven.factory.repository;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.internal.aether.DefaultRepositorySystemSessionFactory;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.DefaultRepositorySystemSession;

import ch.rhj.embedded.maven.factory.ExecutionRequestFactory;

@Named
public class RepositorySessionFactory
{
	private final DefaultRepositorySystemSessionFactory delegate;
	private final ExecutionRequestFactory executionRequestFactory;

	@Inject
	public RepositorySessionFactory(DefaultRepositorySystemSessionFactory delegate, ExecutionRequestFactory executionRequestFactory)
	{
		this.delegate = delegate;
		this.executionRequestFactory = executionRequestFactory;
	}

	public DefaultRepositorySystemSession createRepositorySession(MavenExecutionRequest executionRequest)
	{
		return delegate.newRepositorySession(executionRequest);
	}

	public DefaultRepositorySystemSession createRepositorySession(MavenProject project, String... goals) throws Exception
	{
		MavenExecutionRequest executionRequest = executionRequestFactory.createExecutionRequest(project, goals);

		return createRepositorySession(executionRequest);
	}
}
