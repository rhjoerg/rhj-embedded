package ch.rhj.embedded.maven.repository;

import static ch.rhj.embedded.maven.MavenTestsConstants.EMBEDDED_ID;
import static ch.rhj.embedded.maven.MavenTestsConstants.EMBEDDED_POM;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URL;

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
		Model model1 = modelRepository.get(EMBEDDED_POM);
		Model model2 = modelRepository.get(EMBEDDED_POM);

		assertTrue(model1 == model2);
	}

	@Test
	public void testEmbeddedModel() throws Exception
	{
		URL url = container.getContainerRealm().getResource("META-INF/maven/org.apache.maven/maven-compat/pom.xml");
		Model model = modelRepository.get(url);

		assertEquals("maven-compat", model.getArtifactId());
	}

	@Test
	public void testProperties() throws Exception
	{
		Model model = modelRepository.get(EMBEDDED_POM);

		assertEquals(EMBEDDED_ID, model.getId());
	}
}
