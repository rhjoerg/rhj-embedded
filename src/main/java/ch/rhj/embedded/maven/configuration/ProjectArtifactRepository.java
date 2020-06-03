package ch.rhj.embedded.maven.configuration;

import static org.apache.maven.artifact.repository.ArtifactRepositoryPolicy.CHECKSUM_POLICY_IGNORE;
import static org.apache.maven.artifact.repository.ArtifactRepositoryPolicy.UPDATE_POLICY_ALWAYS;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.Authentication;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.apache.maven.repository.Proxy;

@Named
@SuppressWarnings("deprecation")
public class ProjectArtifactRepository implements ArtifactRepository
{
	public final static String ID = "project-repository";
	public final static ArtifactRepositoryLayout LAYOUT = new DefaultRepositoryLayout();
	public final static ArtifactRepositoryPolicy POLICY = new ArtifactRepositoryPolicy(true, UPDATE_POLICY_ALWAYS, CHECKSUM_POLICY_IGNORE);

	public final String url;

	@Inject
	public ProjectArtifactRepository(Models models) throws IOException
	{
		url = "project:" + models.read(Paths.get("pom.xml"), true).getArtifactId();
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
		return url;
	}

	@Override
	public void setUrl(String url)
	{
		throw new UnsupportedOperationException("not yet implemented");
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
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public ArtifactRepositoryPolicy getSnapshots()
	{
		return POLICY;
	}

	@Override
	public void setSnapshotUpdatePolicy(ArtifactRepositoryPolicy policy)
	{
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public ArtifactRepositoryPolicy getReleases()
	{
		return POLICY;
	}

	@Override
	public void setReleaseUpdatePolicy(ArtifactRepositoryPolicy policy)
	{
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public ArtifactRepositoryLayout getLayout()
	{
		return LAYOUT;
	}

	@Override
	public void setLayout(ArtifactRepositoryLayout layout)
	{
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public String getKey()
	{
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public boolean isUniqueVersion()
	{
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public boolean isBlacklisted()
	{
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
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
	public void setAuthentication(Authentication authentication)
	{
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public Authentication getAuthentication()
	{
		return null;
	}

	@Override
	public void setProxy(Proxy proxy)
	{
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public Proxy getProxy()
	{
		return null;
	}

	@Override
	public List<ArtifactRepository> getMirroredRepositories()
	{
		return null;
	}

	@Override
	public void setMirroredRepositories(List<ArtifactRepository> mirroredRepositories)
	{
		throw new UnsupportedOperationException("not yet implemented");
	}
}
