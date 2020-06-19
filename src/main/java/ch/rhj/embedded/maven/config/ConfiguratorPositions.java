package ch.rhj.embedded.maven.config;

import java.util.List;

public interface ConfiguratorPositions
{
	public final static int PHASE_2 = 100;

	public final static List<Integer> PROPERTIES_CONFIGURATOR_POSITIONS = List.of(0);
	public final static List<Integer> SETTINGS_CONFIGURATOR_POSITIONS = List.of(10);
	public final static List<Integer> PROFILES_CONFIGURATOR_POSITIONS = List.of(20, PHASE_2 + 20);
	public final static List<Integer> AUTHENTICATIONS_CONFIGURATOR_POSITIONS = List.of(30);
	public final static List<Integer> REPOSITORIES_CONFIGURATOR_POSITIONS = List.of(40, PHASE_2 + 40);
	public final static List<Integer> EXECUTION_REQUEST_CONFIGURATOR_POSITIONS = List.of(50, PHASE_2 + 50);
	public final static List<Integer> SESSION_CONFIGURATOR_POSITIONS = List.of(60);
	public final static List<Integer> PROJECT_REQUEST_CONFIGURATOR_POSITIONS = List.of(70);
	public final static List<Integer> PROJECT_CONFIGURATOR_POSITIONS = List.of(80);
}
