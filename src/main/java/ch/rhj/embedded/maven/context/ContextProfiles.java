package ch.rhj.embedded.maven.context;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.maven.model.Profile;

public class ContextProfiles
{
	private final Map<String, Profile> profiles = new TreeMap<>();

	private final TreeSet<String> activeProfileIds = new TreeSet<String>();
	private final TreeSet<String> inactiveProfileIds = new TreeSet<String>();

	public List<String> activeProfileIds()
	{
		return new ArrayList<>(activeProfileIds);
	}

	public List<String> inactiveProfileIds()
	{
		return new ArrayList<>(inactiveProfileIds);
	}

	public List<Profile> allProfiles()
	{
		return new ArrayList<>(profiles.values());
	}

	public List<Profile> activeProfiles()
	{
		return allProfiles().stream().filter(profile -> activeProfileIds.contains(profile.getId())).collect(toList());
	}

	public void add(Profile profile, boolean active)
	{
		String id = profile.getId();

		profiles.put(id, profile);
		added(id, active);
	}

	private void added(String id, boolean active)
	{
		activeProfileIds.remove(id);
		inactiveProfileIds.remove(id);

		if (active)
		{
			activeProfileIds.add(id);
		}
		else
		{
			inactiveProfileIds.add(id);
		}
	}
}
