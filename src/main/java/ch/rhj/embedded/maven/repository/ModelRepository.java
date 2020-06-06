package ch.rhj.embedded.maven.repository;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.ModelReader;

@Named
public class ModelRepository
{
	private final ModelReader reader;

	private final Map<URL, Model> models = new HashMap<>();

	@Inject
	public ModelRepository(ModelReader reader)
	{
		this.reader = reader;
	}
}
