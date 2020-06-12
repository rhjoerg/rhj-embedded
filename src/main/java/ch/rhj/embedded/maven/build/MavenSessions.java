package ch.rhj.embedded.maven.build;

import java.util.Stack;
import java.util.function.Consumer;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.LegacySupport;
import org.apache.maven.session.scope.internal.SessionScope;

@Named
public class MavenSessions
{
	private final SessionFactory factory;

	private final SessionScope scope;

	private final LegacySupport legacySupport;

	private final Stack<MavenSession> sessions = new Stack<>();

	@Inject
	public MavenSessions(SessionFactory factory, SessionScope scope, LegacySupport legacySupport)
	{
		this.factory = factory;
		this.scope = scope;
		this.legacySupport = legacySupport;
	}

	public MavenSession push(MavenExecutionRequest request)
	{
		MavenSession session = factory.newMavenSession(request);

		scope.enter();
		scope.seed(MavenSession.class, session);
		legacySupport.setSession(session);

		sessions.push(session);

		return session;
	}

	public void pop()
	{
		sessions.pop();
		scope.exit();

		legacySupport.setSession(sessions.isEmpty() ? null : sessions.peek());
	}

	public void run(MavenExecutionRequest request, Consumer<MavenSession> consumer)
	{
		MavenSession session = push(request);

		try
		{
			consumer.accept(session);
		}
		finally
		{
			pop();
		}
	}
}
