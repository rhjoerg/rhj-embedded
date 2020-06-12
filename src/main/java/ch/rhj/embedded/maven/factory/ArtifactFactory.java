package ch.rhj.embedded.maven.factory;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;
import org.apache.maven.repository.RepositorySystem;

@Named
public class ArtifactFactory
{
	private final RepositorySystem repositorySystem;

	@Inject
	public ArtifactFactory(RepositorySystem repositorySystem)
	{
		this.repositorySystem = repositorySystem;
	}

	public Artifact createArtifact(String groupId, String artifactId, String version, String packaging)
	{
		return repositorySystem.createArtifact(groupId, artifactId, version, packaging);
	}

	public Artifact createArtifact(MavenProject project)
	{
		String groupId = project.getGroupId();
		String artifactId = project.getArtifactId();
		String version = project.getVersion();
		String packaging = project.getPackaging();

		return createArtifact(groupId, artifactId, version, packaging);
	}
}
