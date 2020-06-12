package ch.rhj.embedded.maven.repository;

import java.io.File;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.model.Model;
import org.apache.maven.repository.internal.MavenWorkspaceReader;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.repository.WorkspaceRepository;

import ch.rhj.embedded.maven.project.ProjectRepository;

@Named("reactor")
public class ProjectWorkspaceReader implements MavenWorkspaceReader
{
	private final ProjectRepository projectRepository;
	private final WorkspaceRepository workspaceRepository;

	@Inject
	public ProjectWorkspaceReader(ProjectRepository projectRepository) throws Exception
	{
		this.projectRepository = projectRepository;
		this.workspaceRepository = new WorkspaceRepository("reactor", Set.of());
	}

	public String getUrl()
	{
		return projectRepository.getUrl();
	}

	@Override
	public WorkspaceRepository getRepository()
	{
		return workspaceRepository;
	}

	@Override
	public File findArtifact(Artifact artifact)
	{
		return null;
	}

	@Override
	public List<String> findVersions(Artifact artifact)
	{
		return List.of();
	}

	@Override
	public Model findModel(Artifact artifact)
	{
		return null;
	}
}
