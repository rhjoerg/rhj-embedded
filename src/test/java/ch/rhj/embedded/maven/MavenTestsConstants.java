package ch.rhj.embedded.maven;

import java.nio.file.Path;
import java.nio.file.Paths;

public interface MavenTestsConstants
{
	public final static String GROUP_ID = "ch.rhj";
	public final static String ARTIFACT_ID = "rhj-embedded";
	public final static String VERSION = "0.0.1-SNAPSHOT";

	public final static Path BASEDIR = Paths.get("").toAbsolutePath();
	public final static Path POM_PATH = BASEDIR.resolve("pom.xml");
}
