package ch.rhj.embedded.maven.factory;

import static ch.rhj.embedded.maven.MavenTestsConstants.EMBEDDED_ID;
import static ch.rhj.embedded.maven.MavenTestsConstants.EMBEDDED_POM;
import static ch.rhj.embedded.maven.MavenTestsConstants.PLUGIN_ID;
import static ch.rhj.embedded.maven.MavenTestsConstants.PLUGIN_POM;
import static ch.rhj.embedded.maven.MavenTestsConstants.TARGET_ID;
import static ch.rhj.embedded.maven.MavenTestsConstants.TARGET_POM;
import static java.util.stream.Collectors.toList;
import static org.apache.maven.settings.SettingsUtils.convertFromSettingsProfile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.maven.RepositoryUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;
import org.apache.maven.model.building.DefaultModelBuildingRequest;
import org.apache.maven.model.building.ModelBuilder;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelBuildingResult;
import org.apache.maven.model.resolution.ModelResolver;
import org.apache.maven.project.ProjectBuildingRequest.RepositoryMerging;
import org.apache.maven.project.ProjectModelResolver;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.RequestTrace;
import org.eclipse.aether.impl.RemoteRepositoryManager;
import org.eclipse.aether.repository.RemoteRepository;
import org.junit.jupiter.api.Test;

import ch.rhj.embedded.maven.WithMaven;
import ch.rhj.embedded.maven.build.ProjectRepository;
import ch.rhj.embedded.maven.build.RepositorySessionRunner;

@WithMaven
public class ModelFactoryTests
{
	@Inject
	private ModelFactory modelFactory;

	@Inject
	private ProfilesFactory profilesFactory;

	@Inject
	private PropertiesFactory propertiesFactory;

	@Inject
	private RepositorySessionRunner sessionRunner;

	@Inject
	private RepositorySystem repositorySystem;

	@Inject
	private RemoteRepositoryManager repositoryManager;

	@Inject
	private ProjectRepository projectRepository;

	@Inject
	private RepositoryFactory repositoryFactory;

	@Inject
	private ModelBuilder builder;

	@Test
	public void testEmbedded() throws Exception
	{
		Model model = modelFactory.create(EMBEDDED_POM);

		assertEquals(EMBEDDED_ID, model.getId());
	}

	@Test
	public void testPlugin() throws Exception
	{
		Model model = modelFactory.create(PLUGIN_POM);

		assertEquals(PLUGIN_ID, model.getId());
	}

	@Test
	public void testTarget() throws Exception
	{
		Model model = modelFactory.create(TARGET_POM);

		assertEquals(TARGET_ID, model.getId());
	}

	@Test
	public void testModelBuilder() throws Exception
	{
		DefaultModelBuildingRequest request = new DefaultModelBuildingRequest();
		List<Profile> activeProfiles = createActiveProfiles(EMBEDDED_POM);
		List<String> activeProfileIds = activeProfiles.stream().map(p -> p.getId()).collect(toList());

		request.setPomFile(EMBEDDED_POM.toFile());
		request.setValidationLevel(ModelBuildingRequest.VALIDATION_LEVEL_STRICT);
		request.setProcessPlugins(true);
		request.setTwoPhaseBuilding(true);
		request.setProfiles(activeProfiles);
		request.setActiveProfileIds(activeProfileIds);
		request.setSystemProperties(propertiesFactory.createSystemProperties());
		request.setUserProperties(propertiesFactory.createUserProperties(EMBEDDED_POM));

		sessionRunner.run(session ->
		{
			ModelBuildingResult result;

			try
			{
				request.setModelResolver(createModelResolver(EMBEDDED_POM, session));
				result = builder.build(request);
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}

			result.getProblems().forEach(p -> System.out.println(p.getMessage()));

			if (result.getProblems().size() > 0)
			{
				fail();
			}

		}, EMBEDDED_POM);
	}

	private ModelResolver createModelResolver(Path pomPath, RepositorySystemSession session) throws Exception
	{
		RequestTrace trace = new RequestTrace(null);
		List<RemoteRepository> repositories = createRemoteRepositories(pomPath);

		return new ProjectModelResolver(session, trace, repositorySystem, repositoryManager, repositories, RepositoryMerging.REQUEST_DOMINANT, null);
	}

	private List<RemoteRepository> createRemoteRepositories(Path pomPath) throws Exception
	{
		List<RemoteRepository> repositories = new ArrayList<>();

		repositories.add(RepositoryUtils.toRepo(projectRepository));

		repositoryFactory.createRepositories(profilesFactory.activeProfiles(pomPath)).stream() //
				.map(r -> RepositoryUtils.toRepo(r)) //
				.forEach(r -> repositories.add(r));

		return repositories;
	}

	private List<Profile> createActiveProfiles(Path pomPath) throws Exception
	{
		return profilesFactory.activeProfiles(pomPath).stream() //
				.map(p -> convertFromSettingsProfile(p)) //
				.collect(toList());
	}
}
