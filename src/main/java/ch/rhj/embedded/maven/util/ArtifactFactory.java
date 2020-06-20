package ch.rhj.embedded.maven.util;

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
	public final static String DEFAULT_SCOPE = "compile";

	private final ArtifactHandlerManager artifactHandlerManager;

	@Inject
	public ArtifactFactory(ArtifactHandlerManager artifactHandlerManager)
	{
		this.artifactHandlerManager = artifactHandlerManager;
	}

	public Artifact createPomArtifact(MavenProject project)
	{
		Artifact projectArtifact = project.getArtifact();

		String groupId = projectArtifact.getGroupId();
		String artifactId = projectArtifact.getArtifactId();
		String version = projectArtifact.getVersion();
		String scope = projectArtifact.getScope();
		String type = "pom";
		String classifier = project.getArtifact().getClassifier();

		Artifact pomArtifact = createArtifact(groupId, artifactId, version, scope, type, classifier);

		pomArtifact.setFile(project.getFile());
		pomArtifact.setResolved(true);

		return pomArtifact;
	}

	public Artifact createArtifact(String groupId, String artifactId, String version, String scope, String type, String classifier)
	{
		ArtifactHandler artifactHandler = artifactHandlerManager.getArtifactHandler(type);

		return new DefaultArtifact(groupId, artifactId, version, scope, type, classifier, artifactHandler);
	}
}
