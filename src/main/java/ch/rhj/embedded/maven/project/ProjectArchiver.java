package ch.rhj.embedded.maven.project;

import java.io.File;
import java.nio.file.Path;
import java.util.Set;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.handler.manager.ArtifactHandlerManager;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Build;
import org.apache.maven.model.PluginManagement;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.archiver.manager.ArchiverManager;

import ch.rhj.embedded.maven.build.MavenSessions;
import ch.rhj.embedded.maven.factory.ExecutionRequestFactory;
import ch.rhj.embedded.maven.factory.ProjectFactory;

@Named
public class ProjectArchiver
{
	private final static Set<String> DEFAULT_INCLUDES = Set.of("**/**");
	private final static Set<String> DEFAULT_EXCLUDES = Set.of("**/package.html");

	private final ProjectFactory projectFactory;

	private final ArtifactHandlerManager artifactHandlerManager;

	private final ArchiverManager archiverManager;

	private final ExecutionRequestFactory requestFactory;

	private final MavenSessions mavenSessions;

	@Inject
	public ProjectArchiver(ProjectFactory projectFactory, ArtifactHandlerManager artifactHandlerManager, ArchiverManager archiverManager,
			ExecutionRequestFactory requestFactory, MavenSessions mavenSessions)
	{
		this.projectFactory = projectFactory;
		this.artifactHandlerManager = artifactHandlerManager;
		this.archiverManager = archiverManager;
		this.requestFactory = requestFactory;
		this.mavenSessions = mavenSessions;
	}

	public void archive(MavenProject project, Path outputDirectory) throws Exception
	{
		File outputFile = getOutputFile(project, outputDirectory);
		MavenArchiver archiver = createMavenArchiver(project, outputFile);
		MavenArchiveConfiguration configuration = createArchiveConfiguration();

		archive(project, archiver, configuration);

		project.getArtifact().setFile(outputFile);
	}

	public MavenProject archive(Path pomFile, Path outputDirectory) throws Exception
	{
		MavenProject project = projectFactory.create(pomFile);

		archive(project, outputDirectory);

		return project;
	}

	private void archive(MavenProject project, MavenArchiver archiver, MavenArchiveConfiguration configuration) throws Exception
	{
		try
		{
			MavenExecutionRequest request = requestFactory.createExecutionRequest(project);

			mavenSessions.run(request, session -> archive(project, archiver, configuration, session));
		}
		catch (RuntimeException e)
		{
			throw Exception.class.cast(e.getCause());
		}
	}

	private void archive(MavenProject project, MavenArchiver archiver, MavenArchiveConfiguration configuration, MavenSession session)
	{
		try
		{
			archiver.createArchive(session, project, configuration);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	private MavenArchiver createMavenArchiver(MavenProject project, File outputFile) throws Exception
	{
		MavenArchiver mavenArchiver = new MavenArchiver();
		JarArchiver jarArchiver = createJarArchiver(project);

		mavenArchiver.setArchiver(jarArchiver);
		mavenArchiver.setOutputFile(outputFile);

		return mavenArchiver;
	}

	private JarArchiver createJarArchiver(MavenProject project) throws Exception
	{
		String extension = getExtension(project);
		Archiver archiver = archiverManager.getArchiver(extension);
		JarArchiver jarArchiver = JarArchiver.class.cast(archiver);

		populate(project, jarArchiver);

		return jarArchiver;
	}

	private void populate(MavenProject project, JarArchiver archiver)
	{
		File directory = new File(project.getBasedir(), project.getBuild().getOutputDirectory());
		String[] includes = getIncludes(project).toArray(String[]::new);
		String[] excludes = getExcludes(project).toArray(String[]::new);

		archiver.addDirectory(directory, includes, excludes);
	}

	private File getOutputFile(MavenProject project, Path outputDirectory)
	{
		String fileName = getFinalName(project) + "." + getExtension(project);

		return outputDirectory.resolve(fileName).toFile();
	}

	private String getFinalName(MavenProject project)
	{
		String finalName = project.getBuild().getFinalName();

		if (finalName == null || finalName.isBlank())
		{
			finalName = project.getArtifactId() + "-" + project.getVersion();
		}

		return finalName;
	}

	private MavenArchiveConfiguration createArchiveConfiguration()
	{
		MavenArchiveConfiguration configuration = new MavenArchiveConfiguration();

		configuration.setForced(true);

		return configuration;
	}

	private String getExtension(MavenProject project)
	{
		ArtifactHandler artifactHandler = artifactHandlerManager.getArtifactHandler(project.getPackaging());

		return artifactHandler.getExtension();
	}

	private Set<String> getIncludes(MavenProject project)
	{
		Set<String> includes = new TreeSet<>();

		includes.addAll(getIncludes(project.getPluginManagement()));
		includes.addAll(getIncludes(project.getBuild()));

		if (includes.isEmpty())
		{
			includes.addAll(DEFAULT_INCLUDES);
		}

		return includes;
	}

	private Set<String> getExcludes(MavenProject project)
	{
		Set<String> excludes = new TreeSet<>();

		excludes.addAll(getExcludes(project.getPluginManagement()));
		excludes.addAll(getExcludes(project.getBuild()));
		excludes.addAll(DEFAULT_EXCLUDES);

		return excludes;
	}

	private Set<String> getIncludes(Build build)
	{
		Set<String> includes = new TreeSet<>();

		build.getPlugins().stream().map(p -> p.getConfiguration()).filter(c -> c != null).forEach(c -> System.out.println(c));

		return includes;
	}

	private Set<String> getExcludes(Build build)
	{
		Set<String> excludes = new TreeSet<>();

		build.getPlugins().stream().map(p -> p.getConfiguration()).filter(c -> c != null).forEach(c -> System.out.println(c));

		return excludes;
	}

	private Set<String> getIncludes(PluginManagement pluginManagement)
	{
		Set<String> includes = new TreeSet<>();

		if (pluginManagement != null)
		{
			pluginManagement.getPlugins().stream().map(p -> p.getConfiguration()).filter(c -> c != null).forEach(c -> System.out.println(c));
		}

		return includes;
	}

	private Set<String> getExcludes(PluginManagement pluginManagement)
	{
		Set<String> excludes = new TreeSet<>();

		if (pluginManagement != null)
		{
			pluginManagement.getPlugins().stream().map(p -> p.getConfiguration()).filter(c -> c != null).forEach(c -> System.out.println(c));
		}

		return excludes;
	}
}
