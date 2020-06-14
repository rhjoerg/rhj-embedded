package ch.rhj.embedded.maven.factory.model;

import static ch.rhj.embedded.maven.MavenTestsConstants.EMBEDDED_ID;
import static ch.rhj.embedded.maven.MavenTestsConstants.EMBEDDED_POM;
import static ch.rhj.embedded.maven.MavenTestsConstants.PLUGIN_ID;
import static ch.rhj.embedded.maven.MavenTestsConstants.PLUGIN_POM;
import static ch.rhj.embedded.maven.MavenTestsConstants.TARGET_ID;
import static ch.rhj.embedded.maven.MavenTestsConstants.TARGET_POM;
import static ch.rhj.embedded.maven.MavenTestsConstants.TEST_OUTPUT_DIRECTORY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.building.ModelBuilder;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelBuildingResult;
import org.junit.jupiter.api.Test;

import ch.rhj.embedded.maven.WithMaven;
import ch.rhj.embedded.maven.build.RepositorySessionRunner;

@WithMaven
public class ModelFactoryTests
{
	private static final Path OUTPUT_DIRECTORY = TEST_OUTPUT_DIRECTORY.resolve("ModelFactoryTests");

	@Inject
	private ModelFactory modelFactory;

	@Inject
	private RepositorySessionRunner sessionRunner;

	@Inject
	private ModelRequestFactory requestFactory;

	@Inject
	private ModelBuilder modelBuilder;

	@Test
	public void testEmbedded() throws Exception
	{
		Model model = modelFactory.createModel(EMBEDDED_POM);

		assertEquals(EMBEDDED_ID, model.getId());
	}

	@Test
	public void testPlugin() throws Exception
	{
		Model model = modelFactory.createModel(PLUGIN_POM);

		assertEquals(PLUGIN_ID, model.getId());
	}

	@Test
	public void testTarget() throws Exception
	{
		Model model = modelFactory.createModel(TARGET_POM);

		assertEquals(TARGET_ID, model.getId());
	}

	@Test
	public void testModelBuilder() throws Exception
	{
		testModelBuilder(EMBEDDED_POM);
		testModelBuilder(PLUGIN_POM);
	}

	private void testModelBuilder(Path pomPath) throws Exception
	{
		String[] goals = { "clean", "deploy", "site" };

		sessionRunner.run(pomPath, goals, session ->
		{
			ModelBuildingResult result;

			try
			{
				ModelBuildingRequest request = requestFactory.createRequest(session, pomPath);

				result = modelBuilder.build(request);
				result.getProblems().forEach(p -> System.out.println(p.getMessage()));

				if (result.getProblems().size() > 0)
				{
					fail();
				}

				Model model = result.getEffectiveModel();
				List<Plugin> plugins = model.getBuild().getPlugins();
				String ids = plugins.stream().map(p -> p.getId()).sorted().collect(Collectors.joining("\r\n"));
				Path path = OUTPUT_DIRECTORY.resolve(model.getArtifactId() + ".plugins.txt");

				Files.createDirectories(OUTPUT_DIRECTORY);
				Files.deleteIfExists(path);
				Files.writeString(path, ids);
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		});
	}
}
