package ch.rhj.embedded.maven;

import static org.junit.platform.commons.support.AnnotationSupport.findAnnotation;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.codehaus.plexus.PlexusContainer;
import org.junit.jupiter.api.extension.ExtensionContext;

import ch.rhj.embedded.maven.repository.ProjectRepository;
import ch.rhj.embedded.plexus.PlexusExtension;

public class MavenExtension extends PlexusExtension
{
	@Override
	protected Set<String> getExclusions(ExtensionContext context)
	{
		Set<String> exclusions = super.getExclusions(context);

		exclusions.add("org.apache.maven.ReactorReader");

		return exclusions;
	}

	@Override
	public void beforeAll(ExtensionContext context) throws Exception
	{
		super.beforeAll(context);

		PlexusContainer container = getContainer(context);
		ProjectRepository repository = container.lookup(ProjectRepository.class);
		Set<Path> pomPaths = discoverPomPaths(context);

		for (Path pomPath : pomPaths)
		{
			repository.install(pomPath);
		}
	}

	protected Set<Path> discoverPomPaths(ExtensionContext context)
	{
		Set<Path> paths = new HashSet<>();
		Set<String> names = discoverPomNames(context);

		names.stream().map(n -> Paths.get(n).toAbsolutePath().normalize()).forEach(p -> paths.add(p));

		return paths;
	}

	protected Set<String> discoverPomNames(ExtensionContext context)
	{
		Set<String> names = new TreeSet<>();
		Class<?> testClass = context.getRequiredTestClass();

		findAnnotation(testClass, MavenPoms.class).ifPresent(a -> names.addAll(List.of(a.value())));
		findAnnotation(testClass, WithMaven.class).ifPresent(a -> names.addAll(List.of(a.poms())));

		return names;
	}
}
