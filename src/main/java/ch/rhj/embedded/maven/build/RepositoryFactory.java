package ch.rhj.embedded.maven.build;

import static org.apache.maven.artifact.repository.ArtifactRepositoryPolicy.CHECKSUM_POLICY_IGNORE;
import static org.apache.maven.artifact.repository.ArtifactRepositoryPolicy.UPDATE_POLICY_ALWAYS;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.MavenArtifactRepository;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.repository.RepositorySystem;

@Named
public class RepositoryFactory
{
	private static final ArtifactRepositoryPolicy REPOSITORY_POLICY = //
			new ArtifactRepositoryPolicy(true, UPDATE_POLICY_ALWAYS, CHECKSUM_POLICY_IGNORE);

	@SuppressWarnings("unused")
	private final RepositorySystem repositorySystem;

	private final Map<String, ArtifactRepositoryLayout> layouts;

	@Inject
	public RepositoryFactory(RepositorySystem repositorySystem, Map<String, ArtifactRepositoryLayout> layouts)
	{
		this.repositorySystem = repositorySystem;
		this.layouts = layouts;
	}

	public String toUrl(Path repositoryPath)
	{
		try
		{
			return repositoryPath.toUri().toURL().toString();
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public ArtifactRepositoryLayout getDefaultLayout()
	{
		return layouts.get("default");
	}

	public MavenArtifactRepository createRepository(String id, Path repositoryPath, ArtifactRepositoryLayout layout)
	{
		String url = toUrl(repositoryPath);

		return new MavenArtifactRepository(id, url, layout, REPOSITORY_POLICY, REPOSITORY_POLICY);
	}

	public MavenArtifactRepository createRepository(String id, Path repositoryPath)
	{
		return createRepository(id, repositoryPath, getDefaultLayout());
	}
}
