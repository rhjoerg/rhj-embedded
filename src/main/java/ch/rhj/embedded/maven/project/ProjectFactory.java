package ch.rhj.embedded.maven.project;

import static java.util.Optional.ofNullable;

import java.io.File;
import java.nio.file.Path;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;

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

	public MavenProject create(Model model)
	{
		MavenProject project = new MavenProject(model);

		populate(project);

		return project;
	}

	public MavenProject create(Path pomPath) throws Exception
	{
		Model model = modelFactory.create(pomPath);

		return create(model);
	}

	private void populate(MavenProject project)
	{
		populateProject(project);
		populateBuild(project.getBuild());
	}

	private void populateProject(MavenProject project)
	{
		File pomFile = project.getModel().getPomFile();
		Artifact artifact = artifactFactory.create(project);

		project.setPomFile(pomFile);
		project.setArtifact(artifact);
	}

	private void populateBuild(Build build)
	{
		build.setDirectory(ofNullable(build.getDirectory()).orElse("target"));

		build.setSourceDirectory(ofNullable(build.getSourceDirectory()).orElse("src/main/java"));
		build.setOutputDirectory(ofNullable(build.getOutputDirectory()).orElse("target/classes"));

		build.setTestSourceDirectory(ofNullable(build.getTestSourceDirectory()).orElse("src/test/java"));
		build.setTestOutputDirectory(ofNullable(build.getTestOutputDirectory()).orElse("target/test-classes"));

		build.setScriptSourceDirectory(ofNullable(build.getScriptSourceDirectory()).orElse("src/main/scripts"));
	}
}
