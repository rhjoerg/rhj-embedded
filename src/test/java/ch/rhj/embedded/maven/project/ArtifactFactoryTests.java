package ch.rhj.embedded.maven.project;

import static ch.rhj.embedded.maven.MavenTestsConstants.EMBEDDED_ARTIFACT_ID;
import static ch.rhj.embedded.maven.MavenTestsConstants.EMBEDDED_GROUP_ID;
import static ch.rhj.embedded.maven.MavenTestsConstants.EMBEDDED_ID;
import static ch.rhj.embedded.maven.MavenTestsConstants.EMBEDDED_TYPE;
import static ch.rhj.embedded.maven.MavenTestsConstants.EMBEDDED_VERSION;
import static ch.rhj.embedded.maven.MavenTestsConstants.PLUGIN_ARTIFACT_ID;
import static ch.rhj.embedded.maven.MavenTestsConstants.PLUGIN_GROUP_ID;
import static ch.rhj.embedded.maven.MavenTestsConstants.PLUGIN_ID;
import static ch.rhj.embedded.maven.MavenTestsConstants.PLUGIN_TYPE;
import static ch.rhj.embedded.maven.MavenTestsConstants.PLUGIN_VERSION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.inject.Inject;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Test;

import ch.rhj.embedded.maven.WithMaven;

@WithMaven
public class ArtifactFactoryTests
{
	@Inject
	private ArtifactFactory factory;

	@Test
	public void testEmbedded() throws Exception
	{
		MavenProject project = mock(MavenProject.class);

		when(project.getGroupId()).thenReturn(EMBEDDED_GROUP_ID);
		when(project.getArtifactId()).thenReturn(EMBEDDED_ARTIFACT_ID);
		when(project.getVersion()).thenReturn(EMBEDDED_VERSION);
		when(project.getPackaging()).thenReturn(EMBEDDED_TYPE);

		Artifact artifact = factory.create(project);

		assertEquals(EMBEDDED_ID, artifact.getId());
		assertEquals("jar", artifact.getArtifactHandler().getExtension());
	}

	@Test
	public void testPlugin() throws Exception
	{
		MavenProject project = mock(MavenProject.class);

		when(project.getGroupId()).thenReturn(PLUGIN_GROUP_ID);
		when(project.getArtifactId()).thenReturn(PLUGIN_ARTIFACT_ID);
		when(project.getVersion()).thenReturn(PLUGIN_VERSION);
		when(project.getPackaging()).thenReturn(PLUGIN_TYPE);

		Artifact artifact = factory.create(project);

		assertEquals(PLUGIN_ID, artifact.getId());
		assertEquals("jar", artifact.getArtifactHandler().getExtension());
	}
}
