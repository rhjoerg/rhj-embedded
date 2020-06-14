package ch.rhj.maven.embedded.foo;

import java.nio.file.Paths;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import ch.rhj.embedded.maven.Builder;
import ch.rhj.embedded.maven.WithMaven;

@WithMaven
public class FooMojoTests
{
	@Inject
	private Builder builder;

	@Test
	public void test() throws Exception
	{
		System.out.println("--- FooMojoTests ---");
		System.out.println(Paths.get("").toAbsolutePath());

		// builder.build(TARGET_POM, "clean", "verify");
	}
}
