package ch.rhj.embedded.maven;

import java.nio.file.Path;
import java.nio.file.Paths;

public interface MavenTestsConstants
{
	public final static String EMBEDDED_ID = "ch.rhj:rhj-embedded:jar:0.0.1-SNAPSHOT";
	public final static Path EMBEDDED_BASEDIR = Paths.get("").toAbsolutePath();
	public final static Path EMBEDDED_POM = EMBEDDED_BASEDIR.resolve("pom.xml");

	public final static String PLUGIN_ID = "ch.rhj:foo-maven-plugin:jar:0.0.1-SNAPSHOT";
	public final static Path PLUGIN_BASEDIR = EMBEDDED_BASEDIR.resolve("foo-maven-plugin");
	public final static Path PLUGIN_POM = PLUGIN_BASEDIR.resolve("pom.xml");
}
