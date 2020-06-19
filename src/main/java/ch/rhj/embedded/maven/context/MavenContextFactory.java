package ch.rhj.embedded.maven.context;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import javax.inject.Inject;
import javax.inject.Named;

import ch.rhj.embedded.maven.config.MavenConfigurator;

@Named
public class MavenContextFactory
{
	private final List<MavenConfigurator> configurators;

	@Inject
	public MavenContextFactory(List<MavenConfigurator> configurators)
	{
		this.configurators = new ArrayList<>(configurators);
	}

	public MavenContext createContext(Path pomPath, String... goals) throws Exception
	{
		MavenContext context = new MavenContext(pomPath, goals);

		for (MavenConfigurator configurator : buildConfiguratorList())
		{
			configurator.configure(context);
		}

		return context;
	}

	private List<MavenConfigurator> buildConfiguratorList()
	{
		TreeMap<Integer, MavenConfigurator> map = new TreeMap<>();

		for (MavenConfigurator configurator : configurators)
		{
			configurator.positions().forEach(position -> map.put(position, configurator));
		}

		ArrayList<MavenConfigurator> result = new ArrayList<>();

		for (Integer key : map.keySet())
		{
			result.add(map.get(key));
		}

		return result;
	}
}
