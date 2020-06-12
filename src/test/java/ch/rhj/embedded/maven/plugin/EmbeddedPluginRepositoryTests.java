package ch.rhj.embedded.maven.plugin;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.descriptor.MojoDescriptor;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugin.descriptor.PluginDescriptorBuilder;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.junit.jupiter.api.Test;

import ch.rhj.embedded.maven.WithMaven;

@WithMaven
public class EmbeddedPluginRepositoryTests
{
	@Inject
	private DefaultPlexusContainer container;

	@Test
	public void test() throws Exception
	{
		System.out.println("--- EmbeddedPluginRepositoryTests ---");

		ArrayList<ClassLoader> loaders = new ArrayList<>();

		for (ClassRealm realm = container.getLookupRealm(); realm != null; realm = realm.getParentRealm())
		{
			loaders.add(realm);
		}

		for (ClassLoader loader = container.getLookupRealm().getParent(); loader != null; loader = loader.getParent())
		{
			loaders.add(loader);
		}

		Set<URL> urls = new HashSet<>();
		PluginDescriptorBuilder pluginDescriptorBuilder = new PluginDescriptorBuilder();

		loaders.forEach(loader -> urls.addAll(findPluginXmls(loader)));

		urls.forEach(url ->
		{

			try (InputStream input = url.openStream())
			{
				try (Reader reader = new InputStreamReader(input, UTF_8))
				{
					PluginDescriptor pluginDescriptor = pluginDescriptorBuilder.build(reader);
					List<Artifact> artifacts = pluginDescriptor.getArtifacts();
					List<MojoDescriptor> mojos = pluginDescriptor.getMojos();

					System.out.println(pluginDescriptor.getId());

					if (artifacts != null)
					{
						artifacts.forEach(a -> System.out.println(a));
					}

					if (mojos != null)
					{
						mojos.forEach(m -> System.out.println(m));
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		});
	}

	private List<URL> findPluginXmls(ClassLoader loader)
	{
		try
		{
			return Collections.list(loader.getResources("META-INF/maven/plugin.xml"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return List.of();
		}
	}
}
