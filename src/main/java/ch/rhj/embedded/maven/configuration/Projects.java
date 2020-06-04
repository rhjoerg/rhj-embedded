package ch.rhj.embedded.maven.configuration;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;

@Named
public class Projects
{
	private final Models models;

	private final Map<Path, MavenProject> projects = new HashMap<>();

	@Inject
	public Projects(Models models)
	{
		this.models = models;
	}

	public synchronized MavenProject get(Path pom) throws IOException
	{
		pom = pom.toAbsolutePath();

		MavenProject project = projects.get(pom);

		if (project == null)
		{
			Model model = models.get(pom);

			project = new MavenProject(model);
			project.setFile(model.getPomFile());

			projects.put(pom, project);
		}

		return project;
	}
}
