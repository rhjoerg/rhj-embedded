package ch.rhj.embedded.maven.configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.RepositoryUtils;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.artifact.ProjectArtifact;
import org.apache.maven.repository.internal.MavenWorkspaceReader;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.repository.WorkspaceRepository;
import org.eclipse.aether.util.artifact.ArtifactIdUtils;

@Named("reactor")
public class ProjectWorkspaceReader implements MavenWorkspaceReader
{
	private final MavenProject project;
	private final Artifact artifact;
	private final WorkspaceRepository repository;

	@Inject
	public ProjectWorkspaceReader(Projects projects) throws IOException
	{
		project = projects.get(Paths.get("pom.xml"));
		artifact = RepositoryUtils.toArtifact(new ProjectArtifact(project));
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
		if (ArtifactIdUtils.equalsId(this.artifact, artifact))
		{
			return new File(project.getBuild().getOutputDirectory());
		}

		return null;
	}

	@Override
	public List<String> findVersions(Artifact artifact)
	{
		if (ArtifactIdUtils.equalsVersionlessId(this.artifact, artifact))
		{
			return List.of(this.artifact.getVersion());
		}

		return List.of();
	}

	@Override
	public Model findModel(Artifact artifact)
	{
		if (ArtifactIdUtils.equalsId(this.artifact, artifact))
		{
			return project.getModel();
		}

		return null;
	}
}
