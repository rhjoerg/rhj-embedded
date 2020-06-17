package ch.rhj.embedded.maven.build;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.installer.ArtifactInstaller;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.project.MavenProject;

import ch.rhj.embedded.maven.context.MavenContext;
import ch.rhj.embedded.maven.context.MavenContextFactory;
import ch.rhj.embedded.maven.factory.artifact.ArtifactFactory;
import ch.rhj.embedded.maven.factory.repository.RepositoryFactory;

@Named
public class ProjectInstaller
{
	private final MavenContextFactory mavenContextFactory;

	private final RepositoryFactory repositoryFactory;
	private final ArtifactFactory artifactFactory;

	private final MavenSessionRunner sessionRunner;

	private final ArtifactInstaller artifactInstaller;

	@Inject
	public ProjectInstaller(MavenContextFactory mavenContextFactory, RepositoryFactory repositoryFactory, ArtifactFactory artifactFactory,
			MavenSessionRunner sessionRunner, ArtifactInstaller artifactInstaller)
	{
		this.mavenContextFactory = mavenContextFactory;
		this.repositoryFactory = repositoryFactory;
		this.artifactFactory = artifactFactory;
		this.sessionRunner = sessionRunner;
		this.artifactInstaller = artifactInstaller;
	}

	public void install(MavenProject project, ArtifactRepository repository) throws Exception
	{
		Context context = new Context(project, artifactFactory);
		MavenContext mavenContext = mavenContextFactory.createContext(project.getFile().toPath());

		sessionRunner.run(mavenContext, session -> install(context, repository));
	}

	public ArtifactRepository install(MavenProject project, String id, Path repositoryPath) throws Exception
	{
		ArtifactRepository repository = repositoryFactory.createRepository(id, repositoryPath);

		install(project, repository);

		return repository;
	}

	private void install(Context context, ArtifactRepository repository)
	{
		try
		{
			for (Artifact artifact : context.artifacts())
			{
				File source = artifact.getFile();

				artifactInstaller.install(source, artifact, repository);
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public static class Context
	{
		private final Artifact jarArtifact;
		private final Artifact pomArtifact;

		public Context(MavenProject project, ArtifactFactory artifactFactory)
		{
			this.jarArtifact = project.getArtifact();
			this.pomArtifact = createPomArtifact(project, artifactFactory);
		}

		public List<Artifact> artifacts()
		{
			return List.of(jarArtifact, pomArtifact);
		}

		private static Artifact createPomArtifact(MavenProject project, ArtifactFactory artifactFactory)
		{
			String groupId = project.getGroupId();
			String artifactId = project.getArtifactId();
			String version = project.getVersion();
			String packaging = "pom";
			Artifact artifact = artifactFactory.createArtifact(groupId, artifactId, version, packaging);

			artifact.setFile(project.getFile());

			return artifact;
		}
	}
}
