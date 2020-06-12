package ch.rhj.embedded.maven.repository;

import static java.util.Optional.ofNullable;

import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.handler.manager.ArtifactHandlerManager;
import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;

@Named
public class ProjectRepository
{
	private final ModelRepository modelRepository;

	private final ArtifactHandlerManager artifactHandlerManager;

	private final Map<String, MavenProject> projects = new TreeMap<>();

	@Inject
	public ProjectRepository(ModelRepository modelRepository, ArtifactHandlerManager artifactHandlerManager)
	{
		this.modelRepository = modelRepository;
		this.artifactHandlerManager = artifactHandlerManager;
	}

	public MavenProject get(Model model)
	{
		String id = model.getId();
		MavenProject project = projects.get(id);

		if (project == null)
		{
			project = new MavenProject(model);
			populate(project);

			projects.put(id, project);
		}

		return project;
	}

	public MavenProject get(Path pomPath) throws Exception
	{
		return get(modelRepository.get(pomPath));
	}

	private void populate(MavenProject project)
	{
		Model model = project.getModel();
		Build build = project.getBuild();

		project.setFile(model.getPomFile());
		project.setArtifact(newArtifact(project));

		build.setDirectory(ofNullable(build.getDirectory()).orElse("target"));

		build.setSourceDirectory(ofNullable(build.getSourceDirectory()).orElse("src/main/java"));
		build.setOutputDirectory(ofNullable(build.getOutputDirectory()).orElse("target/classes"));

		build.setTestSourceDirectory(ofNullable(build.getTestSourceDirectory()).orElse("src/test/java"));
		build.setTestOutputDirectory(ofNullable(build.getTestOutputDirectory()).orElse("target/test-classes"));
	}

	private Artifact newArtifact(MavenProject project)
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
