package ch.rhj.embedded.maven.build;

import static ch.rhj.embedded.maven.MavenTestsConstants.EMBEDDED_POM;
import static ch.rhj.embedded.maven.MavenTestsConstants.TEST_OUTPUT_DIRECTORY;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Test;

import ch.rhj.embedded.maven.WithMaven;
import ch.rhj.embedded.maven.context.MavenContext;
import ch.rhj.embedded.maven.context.MavenContextFactory;
import ch.rhj.embedded.maven.factory.artifact.ArtifactFactory;

@WithMaven
public class ProjectInstallerTests
{
	private final static Path OUTPUT_DIRECTORY = TEST_OUTPUT_DIRECTORY.resolve("ProjectInstallerTests");
	private final static Path STAGING_PATH = OUTPUT_DIRECTORY.resolve("staging");
	private final static Path REPOSITORY_PATH = OUTPUT_DIRECTORY.resolve("repository");

	@Inject
	private MavenContextFactory mavenContextFactory;

	@Inject
	private ProjectArchiver archiver;

	@Inject
	private ArtifactFactory factory;

	@Inject
	private ProjectInstaller installer;

	@Inject
	@Named("default")
	ArtifactRepositoryLayout layout;

	@Test
	public void test() throws Exception
	{
		MavenContext context = mavenContextFactory.createContext(EMBEDDED_POM);
		MavenProject project = archiver.archive(context, STAGING_PATH);
		ArtifactRepository repository = installer.install(project, "ProjectInstallerTests", REPOSITORY_PATH);
		Artifact artifact = factory.createArtifact(project);

		artifact = repository.find(artifact);

		String path = layout.pathOf(artifact);
		String expected = REPOSITORY_PATH.resolve(path).toFile().toString();
		String actual = artifact.getFile().toString();

		assertEquals(expected, actual);
	}
}
