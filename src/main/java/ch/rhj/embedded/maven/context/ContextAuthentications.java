package ch.rhj.embedded.maven.context;

import java.util.Map;
import java.util.TreeMap;

import org.apache.maven.artifact.repository.Authentication;

public class ContextAuthentications
{
	private final Map<String, Authentication> authentications;

	public ContextAuthentications(Map<String, Authentication> authentications)
	{
		this.authentications = new TreeMap<>(authentications);
	}

	public Authentication authentication(String server)
	{
		return authentications.get(server);
	}
}
