package ch.rhj.embedded.maven.util;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.LegacySupport;
import org.apache.maven.session.scope.internal.SessionScope;

@Named
public class SessionRunner
{
	private final SessionScope sessionScope;
	private final LegacySupport legacySupport;

	@Inject
	public SessionRunner(SessionScope sessionScope, LegacySupport legacySupport)
	{
		this.sessionScope = sessionScope;
		this.legacySupport = legacySupport;
	}

	public void runInMavenSession(MavenSession session, Runnable runnable) throws Exception
	{
		MavenSession previousSession = legacySupport.getSession();

		sessionScope.enter();
		sessionScope.seed(MavenSession.class, session);
		legacySupport.setSession(session);

		try
		{
			run(runnable);
		}
		finally
		{
			sessionScope.exit();
			legacySupport.setSession(previousSession);
		}
	}

	private void run(Runnable runnable) throws Exception
	{
		try
		{
			runnable.run();
		}
		catch (RuntimeException e)
		{
			throw Exception.class.cast(e.getCause());
		}
	}
}
