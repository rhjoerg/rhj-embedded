package ch.rhj.embedded.maven.factory;

import java.nio.file.Path;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;

import ch.rhj.embedded.maven.factory.model.ModelFactory;

@Named
public class ProjectFactory
{
	private final ModelFactory modelFactory;
	private final ArtifactFactory artifactFactory;

	@Inject
	public ProjectFactory(ModelFactory modelFactory, ArtifactFactory artifactFactory)
	{
		this.modelFactory = modelFactory;
		this.artifactFactory = artifactFactory;
	}

	public MavenProject createProject(Path pomPath, String... goals) throws Exception
	{
		Model model = modelFactory.createModel(pomPath, goals);
		MavenProject project = new MavenProject(model);

		project.setPomFile(pomPath.toFile());
		project.setArtifact(artifactFactory.createArtifact(project));

		return project;
	}
}
