package ch.rhj.embedded.maven;

import static ch.rhj.embedded.maven.MavenTestsConstants.EMBEDDED_ARTIFACT_ID;
import static ch.rhj.embedded.maven.MavenTestsConstants.EMBEDDED_BASEDIR;
import static ch.rhj.embedded.maven.MavenTestsConstants.EMBEDDED_GROUP_ID;
import static ch.rhj.embedded.maven.MavenTestsConstants.EMBEDDED_PACKAGING;
import static ch.rhj.embedded.maven.MavenTestsConstants.EMBEDDED_POM;
import static ch.rhj.embedded.maven.MavenTestsConstants.EMBEDDED_VERSION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.DefaultRepositoryRequest;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
import org.apache.maven.repository.RepositorySystem;
import org.apache.maven.wagon.Wagon;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.junit.jupiter.api.Test;

import ch.rhj.embedded.maven.configuration.Models;
import ch.rhj.embedded.maven.configuration.ProjectArtifactRepository;
import ch.rhj.embedded.maven.configuration.ProjectWagon;
import ch.rhj.embedded.plexus.Plexi;

@WithMaven
public class MavenExtensionsTest
{
	@Inject
	private Models models;

	@Inject
	private RepositorySystem repositorySystem;

	@Inject
	private ArtifactResolver artifactResolver;

	@Inject
	private ProjectArtifactRepository projectArtifactRepository;

	@Inject
	private DefaultPlexusContainer plexusContainer;

	@Test
	public void testArtifact() throws Exception
	{
		Plexi.inject(plexusContainer, Wagon.class, "project", new ProjectWagon());

		List<Wagon> wagons = plexusContainer.lookupList(Wagon.class);

		System.out.println("-- wagons --");
		wagons.forEach(w -> System.out.println(w));

		DefaultRepositoryRequest repositoryRequest = new DefaultRepositoryRequest();
		ArtifactRepository localRepository = repositorySystem.createDefaultLocalRepository();
		List<ArtifactRepository> remoteRepositories = new ArrayList<>();

		remoteRepositories.add(projectArtifactRepository);

		repositoryRequest.setLocalRepository(localRepository);
		repositoryRequest.setRemoteRepositories(remoteRepositories);

		ArtifactResolutionRequest artifactRequest = new ArtifactResolutionRequest(repositoryRequest);
		Artifact artifact = repositorySystem.createArtifact(EMBEDDED_GROUP_ID, EMBEDDED_ARTIFACT_ID, EMBEDDED_VERSION, EMBEDDED_PACKAGING);

		artifactRequest.setArtifact(artifact);

		ArtifactResolutionResult result = artifactResolver.resolve(artifactRequest);

		if (result.hasExceptions())
		{
			System.out.println("-- exceptions --");
			result.getExceptions().forEach(e -> e.printStackTrace());
		}

		if (result.hasMissingArtifacts())
		{
			System.out.println("-- missing artifacts --");
			result.getMissingArtifacts().forEach(a -> System.out.println(a));
		}

		assertTrue(result.isSuccess());
	}

	@Test
	public void testMavenProject() throws Exception
	{
		Model model = models.read(EMBEDDED_POM, true);
		MavenProject project = new MavenProject(model);

		project.setFile(model.getPomFile());

		assertEquals(EMBEDDED_BASEDIR.toFile(), project.getBasedir());
	}
}
