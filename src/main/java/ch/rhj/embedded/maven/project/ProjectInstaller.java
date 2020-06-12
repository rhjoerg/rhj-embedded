package ch.rhj.embedded.maven.project;

import static org.apache.maven.artifact.repository.ArtifactRepositoryPolicy.CHECKSUM_POLICY_IGNORE;
import static org.apache.maven.artifact.repository.ArtifactRepositoryPolicy.UPDATE_POLICY_ALWAYS;

import java.io.File;
import java.nio.file.Path;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.installer.ArtifactInstaller;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.MavenArtifactRepository;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.project.MavenProject;

import ch.rhj.embedded.maven.build.MavenSessions;
import ch.rhj.embedded.maven.build.RequestFactory;

@Named
public class ProjectInstaller
{
	private final static ArtifactRepositoryPolicy POLICY = new ArtifactRepositoryPolicy(true, UPDATE_POLICY_ALWAYS, CHECKSUM_POLICY_IGNORE);

	private final ArtifactRepositoryLayout layout;

	private final RequestFactory requestFactory;

	private final MavenSessions mavenSessions;

	private final ArtifactInstaller artifactInstaller;

	@Inject
	public ProjectInstaller(@Named("default") ArtifactRepositoryLayout layout, RequestFactory requestFactory, MavenSessions mavenSessions,
			ArtifactInstaller artifactInstaller)
	{
		this.layout = layout;
		this.requestFactory = requestFactory;
		this.mavenSessions = mavenSessions;
		this.artifactInstaller = artifactInstaller;
	}

	public void install(MavenProject project, ArtifactRepository repository) throws Exception
	{
		File source = project.getArtifact().getFile();

		install(source, project, repository);
	}

	public ArtifactRepository install(MavenProject project, Path repositoryPath) throws Exception
	{
		ArtifactRepository repository = createRepository(repositoryPath);

		install(project, repository);

		return repository;
	}

	private void install(File source, MavenProject project, ArtifactRepository repository) throws Exception
	{
		try
		{
			MavenExecutionRequest request = createExecutionRequest(project);
			Artifact artifact = project.getArtifact();

			mavenSessions.run(request, session -> install(source, artifact, repository));
		}
		catch (RuntimeException e)
		{
			throw Exception.class.cast(e.getCause());
		}
	}

	private void install(File source, Artifact artifact, ArtifactRepository repository)
	{
		try
		{
			artifactInstaller.install(source, artifact, repository);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	private ArtifactRepository createRepository(Path repositoryPath) throws Exception
	{
		String url = repositoryPath.toUri().toURL().toString();
		MavenArtifactRepository repository = new MavenArtifactRepository("project", url, layout, POLICY, POLICY);

		return repository;
	}

	private MavenExecutionRequest createExecutionRequest(MavenProject project) throws Exception
	{
		Path pomPath = project.getFile().toPath();
		String[] goals = {};

		return requestFactory.createExecutionRequest(pomPath, goals);
	}
}
