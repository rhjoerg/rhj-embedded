package ch.rhj.embedded.maven.build;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.installer.ArtifactInstaller;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;

import ch.rhj.embedded.maven.context.MavenContext;
import ch.rhj.embedded.maven.factory.artifact.ArtifactFactory;
import ch.rhj.embedded.maven.util.SessionRunner;

@Named
public class ProjectInstaller
{
	private final ArtifactFactory artifactFactory;

	private final SessionRunner sessionRunner;

	private final ArtifactInstaller artifactInstaller;

	@Inject
	public ProjectInstaller(ArtifactFactory artifactFactory, SessionRunner sessionRunner, ArtifactInstaller artifactInstaller)
	{
		this.artifactFactory = artifactFactory;
		this.sessionRunner = sessionRunner;
		this.artifactInstaller = artifactInstaller;
	}

	public void install(MavenContext context, MavenProject project, ArtifactRepository repository) throws Exception
	{
		Artifact jarArtifact = project.getArtifact();
		Artifact pomArtifact = createPomArtifact(project, artifactFactory);
		MavenSession session = context.mavenSession();

		sessionRunner.runInMavenSession(session, () -> install(jarArtifact, repository));
		sessionRunner.runInMavenSession(session, () -> install(pomArtifact, repository));
	}

	private void install(Artifact artifact, ArtifactRepository repository)
	{
		try
		{
			File source = artifact.getFile();

			artifactInstaller.install(source, artifact, repository);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
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
