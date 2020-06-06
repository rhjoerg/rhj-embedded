package ch.rhj.embedded.maven.repository;

import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;

@Named
public class ProjectRepository
{
	private final ModelRepository modelRepository;

	private final Map<String, MavenProject> projects = new TreeMap<>();

	@Inject
	public ProjectRepository(ModelRepository modelRepository)
	{
		this.modelRepository = modelRepository;
	}

	public MavenProject get(Model model)
	{
		String id = model.getId();
		MavenProject project = projects.get(id);

		if (project == null)
		{
			project = new MavenProject(model);
			project.setPomFile(model.getPomFile());

			projects.put(id, project);
		}

		return project;
	}

	public MavenProject get(Path pomPath) throws Exception
	{
		return get(modelRepository.get(pomPath));
	}
}
