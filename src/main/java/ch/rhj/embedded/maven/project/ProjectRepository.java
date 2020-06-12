package ch.rhj.embedded.maven.project;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.MavenArtifactRepository;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;

import ch.rhj.embedded.maven.factory.LayoutProvider;
import ch.rhj.embedded.maven.factory.RepositoryFactory;

@Named
public class ProjectRepository extends MavenArtifactRepository
{
	public final static String ID = "project";

	public final static Path STAGING_PATH = Paths.get("target", "project-staging").toAbsolutePath();
	public final static Path REPOSITORY_PATH = Paths.get("target", "project-repository").toAbsolutePath();

	@Inject
	public ProjectRepository(RepositoryFactory repositoryFactory, LayoutProvider layoutProvider) throws Exception
	{
		this(repositoryFactory.createUrl(REPOSITORY_PATH), layoutProvider.getDefaultLayout(), repositoryFactory.createPolicy());
	}

	protected ProjectRepository(String url, ArtifactRepositoryLayout layout, ArtifactRepositoryPolicy policy)
	{
		super(ID, url, layout, policy, policy);
	}
}
