package ch.rhj.embedded.maven.context;

import java.nio.file.Path;
import java.util.Properties;

import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.settings.Settings;
import org.eclipse.aether.RepositorySystemSession;

public class MavenContext
{
	private final Path pomPath;
	private final Path basedir;
	private final String[] goals;

	private Properties systemProperties;
	private Properties userProperties;

	private Settings settings;
	private ContextProfiles profiles;

	private ContextAuthentications authentications;
	private ContextRepositories repositories;

	private MavenExecutionRequest executionRequest;

	private RepositorySystemSession repositorySession;
	private MavenSession mavenSession;

	private ProjectBuildingRequest projectRequest;
	private MavenProject project;

	public MavenContext(Path pomPath, String... goals)
	{
		this.pomPath = pomPath.toAbsolutePath().normalize();
		this.basedir = this.pomPath.getParent();
		this.goals = goals.clone();
	}

	public Path pomPath()
	{
		return pomPath;
	}

	public Path basedir()
	{
		return basedir;
	}

	public String[] goals()
	{
		return goals.clone();
	}

	public Properties systemProperties()
	{
		return systemProperties;
	}

	public void systemProperties(Properties systemProperties)
	{
		this.systemProperties = systemProperties;
	}

	public Properties userProperties()
	{
		return userProperties;
	}

	public void userProperties(Properties userProperties)
	{
		this.userProperties = userProperties;
	}

	public Settings settings()
	{
		return settings;
	}

	public void settings(Settings settings)
	{
		this.settings = settings;
	}

	public ContextProfiles profiles()
	{
		return profiles;
	}

	public void profiles(ContextProfiles profiles)
	{
		this.profiles = profiles;
	}

	public ContextAuthentications authentications()
	{
		return authentications;
	}

	public void authentications(ContextAuthentications authentications)
	{
		this.authentications = authentications;
	}

	public ContextRepositories repositories()
	{
		return repositories;
	}

	public void repositories(ContextRepositories repositories)
	{
		this.repositories = repositories;
	}

	public MavenExecutionRequest executionRequest()
	{
		return executionRequest;
	}

	public void executionRequest(MavenExecutionRequest executionRequest)
	{
		this.executionRequest = executionRequest;
	}

	public RepositorySystemSession repositorySession()
	{
		return repositorySession;
	}

	public void repositorySession(RepositorySystemSession repositorySession)
	{
		this.repositorySession = repositorySession;
	}

	public MavenSession mavenSession()
	{
		return mavenSession;
	}

	public void mavenSession(MavenSession mavenSession)
	{
		this.mavenSession = mavenSession;
	}

	public ProjectBuildingRequest projectRequest()
	{
		return projectRequest;
	}

	public void projectRequest(ProjectBuildingRequest projectRequest)
	{
		this.projectRequest = projectRequest;
	}

	public MavenProject project()
	{
		return project;
	}

	public void project(MavenProject project)
	{
		this.project = project;
	}

	public Model model()
	{
		return project == null ? null : project.getModel();
	}
}
