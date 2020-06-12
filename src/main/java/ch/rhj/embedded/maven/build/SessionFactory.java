package ch.rhj.embedded.maven.build;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.execution.DefaultMavenExecutionResult;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.internal.aether.DefaultRepositorySystemSessionFactory;
import org.codehaus.plexus.PlexusContainer;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystemSession;

@Named
public class SessionFactory
{
	private final PlexusContainer container;

	private final DefaultRepositorySystemSessionFactory sessionFactory;

	@Inject
	public SessionFactory(PlexusContainer container) throws Exception
	{
		this.container = container;
		this.sessionFactory = container.lookup(DefaultRepositorySystemSessionFactory.class);
	}

	public DefaultRepositorySystemSession newRepositorySession(MavenExecutionRequest request)
	{
		return sessionFactory.newRepositorySession(request);
	}

	@SuppressWarnings("deprecation")
	public MavenSession newMavenSession(MavenExecutionRequest request)
	{
		RepositorySystemSession repoSession = newRepositorySession(request);
		MavenExecutionResult result = new DefaultMavenExecutionResult();

		return new MavenSession(container, repoSession, request, result);
	}
}
