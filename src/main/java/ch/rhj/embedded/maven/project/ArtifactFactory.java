package ch.rhj.embedded.maven.project;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.handler.manager.ArtifactHandlerManager;
import org.apache.maven.project.MavenProject;

@Named
public class ArtifactFactory
{
	private final ArtifactHandlerManager artifactHandlerManager;

	@Inject
	public ArtifactFactory(ArtifactHandlerManager artifactHandlerManager)
	{
		this.artifactHandlerManager = artifactHandlerManager;
	}

	public Artifact create(MavenProject project)
	{
		String groupId = project.getGroupId();
		String artifactId = project.getArtifactId();
		String version = project.getVersion();
		String scope = "compile";
		String type = project.getPackaging();
		String classifier = null;
		ArtifactHandler artifactHandler = artifactHandlerManager.getArtifactHandler(type);

		return new DefaultArtifact(groupId, artifactId, version, scope, type, classifier, artifactHandler);
	}
}
