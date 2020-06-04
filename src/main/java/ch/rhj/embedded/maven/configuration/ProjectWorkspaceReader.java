package ch.rhj.embedded.maven.configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.artifact.ArtifactUtils;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.artifact.ProjectArtifact;
import org.apache.maven.repository.internal.MavenWorkspaceReader;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.repository.WorkspaceRepository;

@Named
public class ProjectWorkspaceReader implements MavenWorkspaceReader
{
	private final MavenProject project;
	private final ProjectArtifact artifact;
	private final String versionLessKey;
	private final String versionedKey;
	private final WorkspaceRepository repository;

	@Inject
	public ProjectWorkspaceReader(Projects projects) throws IOException
	{
		project = projects.get(Paths.get("pom.xml"));
		artifact = new ProjectArtifact(project);
		versionLessKey = ArtifactUtils.versionlessKey(artifact);
		versionedKey = ArtifactUtils.key(artifact);
		repository = new WorkspaceRepository("project", Set.of(project.getId()));
	}

	@Override
	public WorkspaceRepository getRepository()
	{
		return repository;
	}

	@Override
	public File findArtifact(Artifact artifact)
	{
		String key = ArtifactUtils.key(artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion());

		if (versionedKey.equals(key))
		{
			return new File(project.getBuild().getOutputDirectory());
		}

		return null;
	}

	@Override
	public List<String> findVersions(Artifact artifact)
	{
		String key = ArtifactUtils.versionlessKey(artifact.getGroupId(), artifact.getArtifactId());

		if (versionLessKey.equals(key))
		{
			return List.of(this.artifact.getVersion());
		}

		return List.of();
	}

	@Override
	public Model findModel(Artifact artifact)
	{
		throw new UnsupportedOperationException("not yet implemented");
	}
}
