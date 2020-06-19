package ch.rhj.embedded.maven;

import java.nio.file.Path;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.Maven;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionResult;

import ch.rhj.embedded.maven.context.MavenContext;
import ch.rhj.embedded.maven.context.MavenContextFactory;

@Named
public class Builder
{
	private final Maven maven;

	private final MavenContextFactory mavenContextFactory;

	@Inject
	public Builder(Maven maven, MavenContextFactory mavenContextFactory) throws Exception
	{
		this.maven = maven;
		this.mavenContextFactory = mavenContextFactory;
	}

	public void build(Path pomPath, String... goals) throws Exception
	{
		MavenContext context = mavenContextFactory.createContext(pomPath, goals);
		MavenExecutionRequest request = context.executionRequest();
		MavenExecutionResult result = maven.execute(request);
		Exception exception = exception(result);

		if (exception != null)
		{
			throw exception;
		}
	}

	private Exception exception(MavenExecutionResult result)
	{
		List<Throwable> exceptions = result.getExceptions();

		exceptions.forEach(e -> e.printStackTrace());

		return exceptions.stream().filter(e -> e instanceof Exception).map(e -> (Exception) e).findFirst().orElse(null);
	}
}
