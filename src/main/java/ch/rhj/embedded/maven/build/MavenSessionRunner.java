package ch.rhj.embedded.maven.build;

import java.util.function.Consumer;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.LegacySupport;
import org.apache.maven.project.MavenProject;
import org.apache.maven.session.scope.internal.SessionScope;

import ch.rhj.embedded.maven.factory.ExecutionRequestFactory;
import ch.rhj.embedded.maven.factory.MavenSessionFactory;

@Named
public class MavenSessionRunner
{
	private final MavenSessionFactory sessionFactory;
	private final ExecutionRequestFactory requestFactory;

	private final SessionScope sessionScope;
	private final LegacySupport legacySupport;

	@Inject
	public MavenSessionRunner(MavenSessionFactory sessionFactory, ExecutionRequestFactory requestFactory, SessionScope sessionScope,
			LegacySupport legacySupport)
	{
		this.sessionFactory = sessionFactory;
		this.requestFactory = requestFactory;
		this.sessionScope = sessionScope;
		this.legacySupport = legacySupport;
	}

	public void run(MavenExecutionRequest executionRequest, Consumer<MavenSession> consumer) throws Exception
	{
		MavenSession session = sessionFactory.createMavenSession(executionRequest);
		MavenSession previousSession = legacySupport.getSession();

		sessionScope.enter();
		sessionScope.seed(MavenSession.class, session);
		legacySupport.setSession(session);

		try
		{
			run(session, consumer);
		}
		finally
		{
			sessionScope.exit();
			legacySupport.setSession(previousSession);
		}
	}

	public void run(MavenProject project, Consumer<MavenSession> consumer, String... goals) throws Exception
	{
		MavenExecutionRequest executionRequest = requestFactory.createExecutionRequest(project, goals);

		run(executionRequest, consumer);
	}

	private void run(MavenSession session, Consumer<MavenSession> consumer) throws Exception
	{
		try
		{
			consumer.accept(session);
		}
		catch (RuntimeException e)
		{
			throw Exception.class.cast(e.getCause());
		}
	}
}
