package ch.rhj.embedded.maven;

import java.nio.file.Path;
import java.nio.file.Paths;

public interface MavenTestsConstants
{
	public final static String EMBEDDED_GROUP_ID = "ch.rhj";
	public final static String EMBEDDED_ARTIFACT_ID = "rhj-embedded";
	public final static String EMBEDDED_TYPE = "jar";
	public final static String EMBEDDED_VERSION = "0.0.1-SNAPSHOT";

	public final static String EMBEDDED_ID = //
			EMBEDDED_GROUP_ID + ":" + EMBEDDED_ARTIFACT_ID + ":" + EMBEDDED_TYPE + ":" + EMBEDDED_VERSION;

	public final static Path EMBEDDED_BASEDIR = Paths.get("").toAbsolutePath();
	public final static Path EMBEDDED_POM = EMBEDDED_BASEDIR.resolve("pom.xml");

	public final static String PLUGIN_GROUP_ID = "ch.rhj";
	public final static String PLUGIN_ARTIFACT_ID = "foo-maven-plugin";
	public final static String PLUGIN_TYPE = "maven-plugin";
	public final static String PLUGIN_VERSION = "0.0.1-SNAPSHOT";

	public final static String PLUGIN_ID = //
			PLUGIN_GROUP_ID + ":" + PLUGIN_ARTIFACT_ID + ":" + PLUGIN_TYPE + ":" + PLUGIN_VERSION;

	public final static Path PLUGIN_BASEDIR = EMBEDDED_BASEDIR.resolve("foo-maven-plugin");
	public final static Path PLUGIN_POM = PLUGIN_BASEDIR.resolve("pom.xml");

	public final static String TARGET_ID = "ch.rhj:foo-target-project:jar:0.0.1-SNAPSHOT";
	public final static Path TARGET_BASEDIR = PLUGIN_BASEDIR.resolve("target-project");
	public final static Path TARGET_POM = TARGET_BASEDIR.resolve("pom.xml");

	public final static Path TEST_OUTPUT_DIRECTORY = Paths.get("target", "test-output").toAbsolutePath();
}
