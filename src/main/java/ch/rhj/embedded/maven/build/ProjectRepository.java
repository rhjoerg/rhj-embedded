package ch.rhj.embedded.maven.build;

import static ch.rhj.embedded.maven.factory.repository.RepositoryPolicies.createDefaultPolicy;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.artifact.repository.MavenArtifactRepository;
import org.apache.maven.project.MavenProject;

import ch.rhj.embedded.maven.factory.LayoutProvider;

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
	public ProjectRepository(LayoutProvider layoutProvider, ProjectArchiver archiver, ProjectInstaller installer) throws Exception
	{
		super(ID, url(), layoutProvider.getDefaultLayout(), createDefaultPolicy(), createDefaultPolicy());

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

	private static String url() throws Exception
	{
		return REPOSITORY_PATH.toAbsolutePath().normalize().toUri().toURL().toString();
	}
}
