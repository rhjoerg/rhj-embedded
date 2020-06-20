package ch.rhj.embedded.maven.repository;

import static ch.rhj.embedded.maven.MavenTestsConstants.EMBEDDED_ARTIFACT_ID;
import static ch.rhj.embedded.maven.MavenTestsConstants.EMBEDDED_GROUP_ID;
import static ch.rhj.embedded.maven.MavenTestsConstants.EMBEDDED_ID;
import static ch.rhj.embedded.maven.MavenTestsConstants.EMBEDDED_TYPE;
import static ch.rhj.embedded.maven.MavenTestsConstants.EMBEDDED_VERSION;
import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.inject.Inject;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.repository.RepositorySystem;
import org.junit.jupiter.api.Test;

import ch.rhj.embedded.maven.WithMaven;

@WithMaven
public class RepositorySystemTests
{
	@Inject
	private RepositorySystem repositorySystem;

	@Test
	public void test()
	{
		Artifact artifact = repositorySystem.createArtifact(EMBEDDED_GROUP_ID, EMBEDDED_ARTIFACT_ID, EMBEDDED_VERSION, "compile", EMBEDDED_TYPE);

		assertEquals(EMBEDDED_ID, artifact.getId());
	}
}
