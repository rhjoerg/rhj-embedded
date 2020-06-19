package ch.rhj.embedded.maven.config;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.model.Model;
import org.apache.maven.model.building.ModelBuilder;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelBuildingResult;
import org.apache.maven.model.building.ModelProblem;
import org.codehaus.plexus.logging.Logger;

import ch.rhj.embedded.maven.context.ContextModel;
import ch.rhj.embedded.maven.context.MavenContext;

@Named
public class ModelConfigurator implements MavenConfigurator
{
	private final Logger logger;
	private final ModelBuilder modelBuilder;

	@Inject
	public ModelConfigurator(Logger logger, ModelBuilder modelBuilder)
	{
		this.logger = logger;
		this.modelBuilder = modelBuilder;
	}

	@Override
	public List<Integer> positions()
	{
		return ConfiguratorPositions.MODEL_CONFIGURATOR_POSITIONS;
	}

	@Override
	public void configure(MavenContext context) throws Exception
	{
		ModelBuildingRequest request = context.modelRequest();
		ModelBuildingResult result = modelBuilder.build(request);

		validate(result);

		Model effectiveModel = result.getEffectiveModel();
		ContextModel contextModel = new ContextModel(effectiveModel);

		context.contextModel(contextModel);
	}

	private void validate(ModelBuildingResult result) throws Exception
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
