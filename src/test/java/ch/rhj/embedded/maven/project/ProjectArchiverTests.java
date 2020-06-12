package ch.rhj.embedded.maven.project;

import static ch.rhj.embedded.maven.MavenTestsConstants.EMBEDDED_POM;
import static ch.rhj.embedded.maven.MavenTestsConstants.TEST_OUTPUT_DIRECTORY;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.file.Path;

import javax.inject.Inject;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Test;

import ch.rhj.embedded.maven.WithMaven;

@WithMaven
public class ProjectArchiverTests
{
	private final Path OUTPUT_DIRECTORY = TEST_OUTPUT_DIRECTORY.resolve("ProjectArchiverTests");
	@Inject
	private ProjectArchiver archiver;

	@Test
	public void test() throws Exception
	{
		MavenProject project = archiver.archive(EMBEDDED_POM, OUTPUT_DIRECTORY);
		Artifact artifact = project.getArtifact();

		assertNotNull(artifact.getFile());
	}
}
