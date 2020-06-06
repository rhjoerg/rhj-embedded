package ch.rhj.embedded.maven.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.inject.Inject;

import org.apache.maven.model.Model;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.junit.jupiter.api.Test;

import ch.rhj.embedded.maven.WithMaven;

@WithMaven
public class ModelRepositoryTests
{
	@Inject
	private ModelRepository modelRepository;

	@Inject
	private DefaultPlexusContainer container;

	@Test
	public void testCaching() throws Exception
	{
		Path path = Paths.get("pom.xml");
		Model model1 = modelRepository.get(path);
		Model model2 = modelRepository.get(path);

		assertTrue(model1 == model2);
	}

	@Test
	public void testEmbeddedModel() throws Exception
	{
		URL url = container.getContainerRealm().getResource("META-INF/maven/org.apache.maven/maven-embedder/pom.xml");
		Model model = modelRepository.get(url);

		assertEquals("maven-embedder", model.getArtifactId());
	}
}
