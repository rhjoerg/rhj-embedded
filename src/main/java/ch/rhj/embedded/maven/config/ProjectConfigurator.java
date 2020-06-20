package ch.rhj.embedded.maven.config;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.model.building.ModelProblem;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.project.ProjectBuildingResult;
import org.codehaus.plexus.logging.Logger;

import ch.rhj.embedded.maven.context.MavenContext;

@Named
public class ProjectConfigurator implements MavenConfigurator
{
	private final Logger logger;
	private final ProjectBuilder projectBuilder;

	@Inject
	public ProjectConfigurator(Logger logger, ProjectBuilder projectBuilder)
	{
		this.logger = logger;
		this.projectBuilder = projectBuilder;
	}

	@Override
	public List<Integer> positions()
	{
		return ConfiguratorPositions.PROJECT_CONFIGURATOR_POSITIONS;
	}

	@Override
	public void configure(MavenContext context) throws Exception
	{
		ProjectBuildingRequest request = context.projectRequest();
		ProjectBuildingResult result = projectBuilder.build(context.pomPath().toFile(), request);

		validate(result);

		MavenProject project = result.getProject();

		request.setProject(project);
		context.project(project);
	}

	private void validate(ProjectBuildingResult result) throws Exception
	{
		Exception exception = null;

		for (ModelProblem problem : result.getProblems())
		{
			logger.warn(problem.getMessage());

			if (exception == null)
			{
				exception = problem.getException();
			}
		}

		if (exception != null)
		{
			throw exception;
		}
	}
}
