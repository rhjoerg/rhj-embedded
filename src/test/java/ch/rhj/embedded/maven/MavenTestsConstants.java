package ch.rhj.embedded.maven;

import java.nio.file.Path;
import java.nio.file.Paths;

public interface MavenTestsConstants
{
	public final static String EMBEDDED_GROUP_ID = "ch.rhj";
	public final static String EMBEDDED_ARTIFACT_ID = "rhj-embedded";
	public final static String EMBEDDED_PACKAGING = "jar";
	public final static String EMBEDDED_VERSION = "0.0.1-SNAPSHOT";

	public final static String EMBEDDED_ID = //
			EMBEDDED_GROUP_ID + ":" + EMBEDDED_ARTIFACT_ID + ":" + EMBEDDED_PACKAGING + ":" + EMBEDDED_VERSION;

	public final static Path EMBEDDED_BASEDIR = Paths.get("").toAbsolutePath();
	public final static Path EMBEDDED_POM = EMBEDDED_BASEDIR.resolve("pom.xml");
}
