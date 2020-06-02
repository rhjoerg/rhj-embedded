package ch.rhj.embedded.maven;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.inject.Inject;

import org.apache.maven.Maven;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3ReaderEx;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Test;

@WithMaven
public class MavenExtensionsTest
{
	@Inject
	private Maven maven;

	@Test
	public void test()
	{
		assertNotNull(maven);
	}

	@Test
	public void experiment() throws Exception
	{
		Path basePath = Paths.get("").toAbsolutePath();
		Path pomPath = basePath.resolve("pom.xml");

		try (InputStream input = Files.newInputStream(pomPath))
		{
			MavenXpp3ReaderEx reader = new MavenXpp3ReaderEx();
			Model model = reader.read(input, true, null);
			String groupId = model.getGroupId() == null ? model.getParent().getGroupId() : model.getGroupId();
			String artifactId = model.getArtifactId();
			String version = model.getVersion() == null ? model.getParent().getVersion() : model.getVersion();

			assertEquals("ch.rhj", groupId);
			assertEquals("rhj-embedded", artifactId);
			assertEquals("0.0.1-SNAPSHOT", version);

			MavenProject project = new MavenProject(model);

			project.setFile(pomPath.toFile());
			project.setPomFile(pomPath.toFile());

			assertEquals(basePath.toFile(), project.getBasedir());
		}
	}
}
