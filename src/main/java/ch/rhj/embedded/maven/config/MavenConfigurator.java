package ch.rhj.embedded.maven.config;

import java.util.List;

import ch.rhj.embedded.maven.context.MavenContext;

public interface MavenConfigurator
{
	public List<Integer> positions();

	public void configure(MavenContext context) throws Exception;
}
