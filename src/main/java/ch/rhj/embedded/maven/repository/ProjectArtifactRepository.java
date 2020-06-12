package ch.rhj.embedded.maven.repository;

import static org.apache.maven.artifact.repository.ArtifactRepositoryPolicy.CHECKSUM_POLICY_IGNORE;
import static org.apache.maven.artifact.repository.ArtifactRepositoryPolicy.UPDATE_POLICY_ALWAYS;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.Authentication;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.repository.Proxy;

@Named
@SuppressWarnings("deprecation")
public class ProjectArtifactRepository implements ArtifactRepository
{
	public static final String ID = "project";

	private final ProjectWorkspaceReader workspaceReader;

	private final Layout layout = new Layout();

	private final ArtifactRepositoryPolicy policy = new ArtifactRepositoryPolicy(true, UPDATE_POLICY_ALWAYS, CHECKSUM_POLICY_IGNORE);

	@Inject
	public ProjectArtifactRepository(ProjectWorkspaceReader workspaceReader)
	{
		this.workspaceReader = workspaceReader;
	}

	@Override
	public String pathOf(Artifact artifact)
	{
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public String pathOfRemoteRepositoryMetadata(ArtifactMetadata artifactMetadata)
	{
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public String pathOfLocalRepositoryMetadata(ArtifactMetadata metadata, ArtifactRepository repository)
	{
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public String getUrl()
	{
		return workspaceReader.getUrl();
	}

	@Override
	public void setUrl(String url)
	{
		throw new UnsupportedOperationException("url is read-only");
	}

	@Override
	public String getBasedir()
	{
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public String getProtocol()
	{
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public String getId()
	{
		return ID;
	}

	@Override
	public void setId(String id)
	{
		throw new UnsupportedOperationException("id is read-only");
	}

	@Override
	public ArtifactRepositoryPolicy getSnapshots()
	{
		return policy;
	}

	@Override
	public void setSnapshotUpdatePolicy(ArtifactRepositoryPolicy policy)
	{
		throw new UnsupportedOperationException("policy is read-only");
	}

	@Override
	public ArtifactRepositoryPolicy getReleases()
	{
		return policy;
	}

	@Override
	public void setReleaseUpdatePolicy(ArtifactRepositoryPolicy policy)
	{
		throw new UnsupportedOperationException("policy is read-only");
	}

	@Override
	public ArtifactRepositoryLayout getLayout()
	{
		return layout;
	}

	@Override
	public void setLayout(ArtifactRepositoryLayout layout)
	{
		throw new UnsupportedOperationException("layout is read-only");
	}

	@Override
	public String getKey()
	{
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	@Deprecated
	public boolean isUniqueVersion()
	{
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	@Deprecated
	public boolean isBlacklisted()
	{
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	@Deprecated
	public void setBlacklisted(boolean blackListed)
	{
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public Artifact find(Artifact artifact)
	{
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public List<String> findVersions(Artifact artifact)
	{
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public boolean isProjectAware()
	{
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public Authentication getAuthentication()
	{
		return null;
	}

	@Override
	public void setAuthentication(Authentication authentication)
	{
		if (authentication != null)
		{
			throw new UnsupportedOperationException("authentication is read-only");
		}
	}

	@Override
	public Proxy getProxy()
	{
		return null;
	}

	@Override
	public void setProxy(Proxy proxy)
	{
		if (proxy != null)
		{
			throw new UnsupportedOperationException("proxy is read-only");
		}
	}

	@Override
	public List<ArtifactRepository> getMirroredRepositories()
	{
		return List.of();
	}

	@Override
	public void setMirroredRepositories(List<ArtifactRepository> mirroredRepositories)
	{
		throw new UnsupportedOperationException("mirroredRepositories is read-only");
	}

	private class Layout implements ArtifactRepositoryLayout
	{
		@Override
		public String getId()
		{
			return "default";
		}

		@Override
		public String pathOf(Artifact artifact)
		{
			throw new UnsupportedOperationException("not yet implemented");
		}

		@Override
		public String pathOfLocalRepositoryMetadata(ArtifactMetadata metadata, ArtifactRepository repository)
		{
			throw new UnsupportedOperationException("not yet implemented");
		}

		@Override
		public String pathOfRemoteRepositoryMetadata(ArtifactMetadata metadata)
		{
			throw new UnsupportedOperationException("not yet implemented");
		}
	}
}
