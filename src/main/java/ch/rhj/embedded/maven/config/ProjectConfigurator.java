package ch.rhj.embedded.maven.config;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;

import ch.rhj.embedded.maven.context.MavenContext;
import ch.rhj.embedded.maven.factory.artifact.ArtifactFactory;

@Named
public class ProjectConfigurator implements MavenConfigurator
{
	private final ArtifactFactory artifactFactory;

	@Inject
	public ProjectConfigurator(ArtifactFactory artifactFactory)
	{
		this.artifactFactory = artifactFactory;
	}

	@Override
	public List<Integer> positions()
	{
		return ConfiguratorPositions.PROJECT_CONFIGURATOR_POSITIONS;
	}

	@Override
	public void configure(MavenContext context) throws Exception
	{
		Model model = context.contextModel().model();
		MavenProject project = new MavenProject(model);

		project.setPomFile(context.pomPath().toFile());
		project.setArtifact(artifactFactory.createArtifact(project));

		context.project(project);
	}
}
