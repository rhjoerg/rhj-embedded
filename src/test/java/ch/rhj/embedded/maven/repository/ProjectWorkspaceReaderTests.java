package ch.rhj.embedded.maven.repository;

import static ch.rhj.embedded.maven.MavenTestsConstants.EMBEDDED_BASEDIR;
import static ch.rhj.embedded.maven.MavenTestsConstants.EMBEDDED_POM;
import static java.util.Optional.ofNullable;
import static org.apache.maven.artifact.repository.ArtifactRepositoryPolicy.CHECKSUM_POLICY_IGNORE;
import static org.apache.maven.artifact.repository.ArtifactRepositoryPolicy.UPDATE_POLICY_ALWAYS;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.installer.ArtifactInstaller;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.MavenArtifactRepository;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.junit.jupiter.api.Test;

import ch.rhj.embedded.maven.WithMaven;
import ch.rhj.embedded.maven.build.MavenSessions;
import ch.rhj.embedded.maven.factory.ExecutionRequestFactory;
import ch.rhj.embedded.maven.factory.ProjectFactory;

@WithMaven
public class ProjectWorkspaceReaderTests
{
	@Inject
	private ExecutionRequestFactory requestFactory;

	@Inject
	private ProjectFactory projectFactory;

	@Inject
	private ArtifactInstaller artifactInstaller;

	@Inject
	@Named("default")
	private ArtifactRepositoryLayout layout;

	@Inject
	private MavenSessions mavenSessions;

	@Inject
	private Map<String, Archiver> archivers;

	@Test
	public void testPackAndInstall() throws Exception
	{
		Path targetPath = EMBEDDED_BASEDIR.resolve("target");
		Path stagingPath = targetPath.resolve("project-repository-staging");
		Path repositoryPath = targetPath.resolve("project-repository");
		MavenProject project = projectFactory.create(EMBEDDED_POM);
		Artifact artifact = project.getArtifact();

		File classesDirecory = targetPath.resolve("classes").toFile();

		String finalName = ofNullable(project.getBuild().getFinalName()).orElse(project.getArtifactId() + "-" + project.getVersion());
		File jarFile = new File(stagingPath.toFile(), finalName + ".jar");

		String[] includes = { "**/**" };
		String[] excludes = { "**/package.html" };

		MavenArchiver archiver = new MavenArchiver();
		MavenArchiveConfiguration archive = new MavenArchiveConfiguration();

		archiver.setArchiver((JarArchiver) archivers.get("jar"));
		archiver.setOutputFile(jarFile);
		archive.setForced(true);

		archiver.getArchiver().addDirectory(classesDirecory, includes, excludes);

		String url = repositoryPath.toUri().toURL().toString();
		ArtifactRepositoryPolicy policy = new ArtifactRepositoryPolicy(true, UPDATE_POLICY_ALWAYS, CHECKSUM_POLICY_IGNORE);
		MavenArtifactRepository repository = new MavenArtifactRepository("project", url, layout, policy, policy);
		MavenExecutionRequest executionRequest = requestFactory.createExecutionRequest(project);

		mavenSessions.run(executionRequest, mavenSession ->
		{
			try
			{
				archiver.createArchive(mavenSession, project, archive);
				artifactInstaller.install(jarFile, artifact, repository);
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		});
	}
}
