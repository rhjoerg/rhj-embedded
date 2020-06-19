package ch.rhj.embedded.maven.config;

import java.util.List;

public interface ConfiguratorPositions
{
	public final static List<Integer> PROPERTIES_CONFIGURATOR_POSITIONS = List.of(0);
	public final static List<Integer> SETTINGS_CONFIGURATOR_POSITIONS = List.of(10);
	public final static List<Integer> PROFILES_CONFIGURATOR_POSITIONS = List.of(20);
	public final static List<Integer> AUTHENTICATIONS_CONFIGURATOR_POSITIONS = List.of(30);
	public final static List<Integer> REPOSITORIES_CONFIGURATOR_POSITIONS = List.of(40);
	public final static List<Integer> EXECUTION_REQUEST_CONFIGURATOR_POSITIONS = List.of(50);
	public final static List<Integer> SESSION_CONFIGURATOR_POSITIONS = List.of(60);
	public final static List<Integer> MODEL_RESOLVER_CONFIGURATOR_POSITIONS = List.of(70);
	public final static List<Integer> MODEL_REQUEST_CONFIGURATOR_POSITIONS = List.of(80);
	public final static List<Integer> MODEL_CONFIGURATOR_POSITIONS = List.of(90);
	public final static List<Integer> PROJECT_REQUEST_CONFIGURATOR_POSITIONS = List.of(100);
	public final static List<Integer> PROJECT_CONFIGURATOR_POSITIONS = List.of(110);
}
