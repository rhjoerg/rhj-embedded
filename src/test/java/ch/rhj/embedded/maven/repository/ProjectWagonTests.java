package ch.rhj.embedded.maven.repository;

import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.inject.Inject;

import org.apache.maven.wagon.Wagon;
import org.eclipse.aether.transport.wagon.WagonProvider;
import org.junit.jupiter.api.Test;

import ch.rhj.embedded.maven.WithMaven;

@WithMaven
public class ProjectWagonTests
{
	@Inject
	private WagonProvider wagonProvider;

	@Test
	public void test() throws Exception
	{
		Wagon wagon = wagonProvider.lookup("project");

		assertTrue(wagon instanceof ProjectWagon);
	}
}
