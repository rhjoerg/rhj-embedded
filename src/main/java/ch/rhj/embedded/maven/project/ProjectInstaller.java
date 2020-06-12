package ch.rhj.embedded.maven.project;

import java.io.File;
import java.nio.file.Path;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.installer.ArtifactInstaller;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.project.MavenProject;

import ch.rhj.embedded.maven.build.MavenSessions;
import ch.rhj.embedded.maven.build.RepositoryFactory;
import ch.rhj.embedded.maven.factory.ExecutionRequestFactory;

@Named
public class ProjectInstaller
{
	private final RepositoryFactory repositoryFactory;

	private final ExecutionRequestFactory requestFactory;

	private final MavenSessions mavenSessions;

	private final ArtifactInstaller artifactInstaller;

	@Inject
	public ProjectInstaller(RepositoryFactory repositoryFactory, ExecutionRequestFactory requestFactory, MavenSessions mavenSessions,
			ArtifactInstaller artifactInstaller)
	{
		this.repositoryFactory = repositoryFactory;
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
		ArtifactRepository repository = repositoryFactory.createRepository("project", repositoryPath);

		install(project, repository);

		return repository;
	}

	private void install(File source, MavenProject project, ArtifactRepository repository) throws Exception
	{
		try
		{
			MavenExecutionRequest request = requestFactory.createExecutionRequest(project);
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
}
