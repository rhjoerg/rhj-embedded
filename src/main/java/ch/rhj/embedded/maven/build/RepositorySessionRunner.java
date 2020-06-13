package ch.rhj.embedded.maven.build;

import java.nio.file.Path;
import java.util.function.Consumer;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.execution.MavenExecutionRequest;
import org.eclipse.aether.DefaultRepositorySystemSession;

import ch.rhj.embedded.maven.factory.ExecutionRequestFactory;
import ch.rhj.embedded.maven.factory.RepositorySessionFactory;

@Named
public class RepositorySessionRunner
{
	private final RepositorySessionFactory sessionFactory;
	private final ExecutionRequestFactory requestFactory;

	@Inject
	public RepositorySessionRunner(RepositorySessionFactory sessionFactory, ExecutionRequestFactory requestFactory)
	{
		this.sessionFactory = sessionFactory;
		this.requestFactory = requestFactory;
	}

	public void run(Consumer<DefaultRepositorySystemSession> consumer, MavenExecutionRequest executionRequest) throws Exception
	{
		DefaultRepositorySystemSession session = sessionFactory.createRepositorySession(executionRequest);

		try
		{
			consumer.accept(session);
		}
		catch (RuntimeException e)
		{
			throw Exception.class.cast(e.getCause());
		}
	}

	public void run(Consumer<DefaultRepositorySystemSession> consumer, Path pomPath, String... goals) throws Exception
	{
		MavenExecutionRequest executionRequest = requestFactory.createExecutionRequest(pomPath, goals);

		run(consumer, executionRequest);
	}
}
