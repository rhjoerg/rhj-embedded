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
import org.codehaus.plexus.PlexusContainer;

import ch.rhj.embedded.maven.context.MavenContext;
import ch.rhj.embedded.maven.context.MavenContextFactory;
import ch.rhj.embedded.maven.factory.LayoutProvider;

@Named
public class ProjectRepository extends MavenArtifactRepository
{
	public final static String ID = "project";

	public final static Path STAGING_PATH = Paths.get("target", "project-staging").toAbsolutePath();
	public final static Path REPOSITORY_PATH = Paths.get("target", "project-repository").toAbsolutePath();

	private final Set<Path> installed = new HashSet<>();

	private final PlexusContainer container;

	private MavenContextFactory mavenContextFactory;

	private ProjectArchiver projectArchiver;
	private ProjectInstaller projectInstaller;

	@Inject
	public ProjectRepository(PlexusContainer container, LayoutProvider layoutProvider) throws Exception
	{
		super(ID, url(), layoutProvider.getDefaultLayout(), createDefaultPolicy(), createDefaultPolicy());

		this.container = container;
	}

	public void install(Path pomPath) throws Exception
	{
		pomPath = pomPath.toAbsolutePath().normalize();

		if (installed.contains(pomPath))
		{
			return;
		}

		ProjectArchiver archiver = getProjectArchiver();
		ProjectInstaller installer = getProjectInstaller();

		MavenContext context = getMavenContextFactory().createContext(pomPath);
		MavenProject project = archiver.archive(context, STAGING_PATH);

		installer.install(project, this);
		installed.add(pomPath);
	}

	private MavenContextFactory getMavenContextFactory() throws Exception
	{
		if (mavenContextFactory == null)
		{
			mavenContextFactory = container.lookup(MavenContextFactory.class);
		}

		return mavenContextFactory;
	}

	private ProjectArchiver getProjectArchiver() throws Exception
	{
		if (projectArchiver == null)
		{
			projectArchiver = container.lookup(ProjectArchiver.class);
		}

		return projectArchiver;
	}

	private ProjectInstaller getProjectInstaller() throws Exception
	{
		if (projectInstaller == null)
		{
			projectInstaller = container.lookup(ProjectInstaller.class);
		}

		return projectInstaller;
	}

	private static String url() throws Exception
	{
		return REPOSITORY_PATH.toAbsolutePath().normalize().toUri().toURL().toString();
	}
}
