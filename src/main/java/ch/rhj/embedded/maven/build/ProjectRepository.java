package ch.rhj.embedded.maven.build;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.MavenArtifactRepository;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.project.MavenProject;

import ch.rhj.embedded.maven.factory.LayoutProvider;
import ch.rhj.embedded.maven.factory.RepositoryFactory;

@Named
public class ProjectRepository extends MavenArtifactRepository
{
	public final static String ID = "project";

	public final static Path STAGING_PATH = Paths.get("target", "project-staging").toAbsolutePath();
	public final static Path REPOSITORY_PATH = Paths.get("target", "project-repository").toAbsolutePath();

	private final Set<Path> installed = new HashSet<>();

	private final ProjectArchiver archiver;
	private final ProjectInstaller installer;

	@Inject
	public ProjectRepository(RepositoryFactory repositoryFactory, LayoutProvider layoutProvider, ProjectArchiver archiver, ProjectInstaller installer)
			throws Exception
	{
		this(repositoryFactory.createUrl(REPOSITORY_PATH), layoutProvider.getDefaultLayout(), repositoryFactory.createPolicy(), archiver, installer);
	}

	private ProjectRepository(String url, ArtifactRepositoryLayout layout, ArtifactRepositoryPolicy policy, ProjectArchiver archiver,
			ProjectInstaller installer)
	{
		super(ID, url, layout, policy, policy);

		this.archiver = archiver;
		this.installer = installer;
	}

	public void install(Path pomPath) throws Exception
	{
		pomPath = pomPath.toAbsolutePath().normalize();

		if (installed.contains(pomPath))
		{
			return;
		}

		MavenProject project = archiver.archive(pomPath, STAGING_PATH);

		installer.install(project, this);
		installed.add(pomPath);
	}
}
