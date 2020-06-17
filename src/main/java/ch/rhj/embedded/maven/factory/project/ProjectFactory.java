package ch.rhj.embedded.maven.factory.project;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;

import ch.rhj.embedded.maven.context.MavenContext;
import ch.rhj.embedded.maven.factory.artifact.ArtifactFactory;
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

	public MavenProject createProject(MavenContext context) throws Exception
	{
		Model model = modelFactory.createModel(context);
		MavenProject project = new MavenProject(model);

		project.setPomFile(context.pomPath().toFile());
		project.setArtifact(artifactFactory.createArtifact(project));

		return project;
	}
}
