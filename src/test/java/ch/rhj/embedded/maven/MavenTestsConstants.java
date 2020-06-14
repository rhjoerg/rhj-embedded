package ch.rhj.embedded.maven;

import java.nio.file.Path;
import java.nio.file.Paths;

public interface MavenTestsConstants
{
	public final static Path BASE_DIR = Paths.get("").toAbsolutePath();
	public final static Path TEST_PROJECTS_DIR = BASE_DIR.resolve("test-projects");

	public final static Path TEST_OUTPUT_DIRECTORY = Paths.get("target", "test-output").toAbsolutePath();

	// ----------------------------------------------------------------------------------------------------------------

	public final static String EMBEDDED_GROUP_ID = "ch.rhj";
	public final static String EMBEDDED_ARTIFACT_ID = "rhj-embedded";
	public final static String EMBEDDED_TYPE = "jar";
	public final static String EMBEDDED_VERSION = "0.0.1-SNAPSHOT";

	public final static String EMBEDDED_ID = //
			EMBEDDED_GROUP_ID + ":" + EMBEDDED_ARTIFACT_ID + ":" + EMBEDDED_TYPE + ":" + EMBEDDED_VERSION;

	public final static Path EMBEDDED_BASEDIR = BASE_DIR;
	public final static Path EMBEDDED_POM = EMBEDDED_BASEDIR.resolve("pom.xml");

	// ----------------------------------------------------------------------------------------------------------------

	public final static String PLUGIN_GROUP_ID = "ch.rhj";
	public final static String PLUGIN_ARTIFACT_ID = "rhj-embedded-plugin-project";
	public final static String PLUGIN_TYPE = "maven-plugin";
	public final static String PLUGIN_VERSION = "0.0.1-SNAPSHOT";

	public final static String PLUGIN_ID = //
			PLUGIN_GROUP_ID + ":" + PLUGIN_ARTIFACT_ID + ":" + PLUGIN_TYPE + ":" + PLUGIN_VERSION;

	public final static Path PLUGIN_BASEDIR = TEST_PROJECTS_DIR.resolve("plugin-project");
	public final static Path PLUGIN_POM = PLUGIN_BASEDIR.resolve("pom.xml");

	// ----------------------------------------------------------------------------------------------------------------

	public final static String TARGET_GROUP_ID = "ch.rhj";
	public final static String TARGET_ARTIFACT_ID = "rhj-embedded-target-project";
	public final static String TARGET_TYPE = "jar";
	public final static String TARGET_VERSION = "0.0.1-SNAPSHOT";

	public final static String TARGET_ID = //
			TARGET_GROUP_ID + ":" + TARGET_ARTIFACT_ID + ":" + TARGET_TYPE + ":" + TARGET_VERSION;

	public final static Path TARGET_BASEDIR = TEST_PROJECTS_DIR.resolve("target-project");
	public final static Path TARGET_POM = TARGET_BASEDIR.resolve("pom.xml");

}
