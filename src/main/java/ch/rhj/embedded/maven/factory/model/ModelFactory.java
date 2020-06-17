package ch.rhj.embedded.maven.factory.model;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.model.Model;
import org.apache.maven.model.building.ModelBuilder;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelBuildingResult;
import org.eclipse.aether.RepositorySystemSession;

import ch.rhj.embedded.maven.build.RepositorySessionRunner;
import ch.rhj.embedded.maven.context.MavenContext;

@Named
public class ModelFactory
{
	private final RepositorySessionRunner sessionRunner;

	private final ModelRequestFactory requestFactory;

	private final ModelBuilder modelBuilder;

	@Inject
	public ModelFactory(RepositorySessionRunner sessionRunner, ModelRequestFactory requestFactory, ModelBuilder modelBuilder)
	{
		this.sessionRunner = sessionRunner;
		this.requestFactory = requestFactory;
		this.modelBuilder = modelBuilder;
	}

	public Model createModel(MavenContext context) throws Exception
	{
		ModelResult modelResult = new ModelResult();

		try
		{
			sessionRunner.run(context, session -> createModel(session, context, modelResult));
		}
		catch (RuntimeException e)
		{
			throw Exception.class.cast(e.getCause());
		}

		return modelResult.model;
	}

	private void createModel(RepositorySystemSession session, MavenContext context, ModelResult modelResult)
	{
		try
		{
			ModelBuildingRequest request = requestFactory.createRequest(session, context);
			ModelBuildingResult modelBuildingResult = modelBuilder.build(request);

			validate(modelBuildingResult);

			modelResult.model = modelBuildingResult.getEffectiveModel();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	private void validate(ModelBuildingResult modelBuildingResult) throws Exception
	{
		Exception exception = modelBuildingResult.getProblems().stream() //
				.filter(p -> p.getException() != null) //
				.map(p -> p.getException()) //
				.findFirst().orElse(null);

		if (exception != null)
		{
			throw exception;
		}
	}

	private static class ModelResult
	{
		public Model model;
	}
}
