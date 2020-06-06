package ch.rhj.embedded.maven.repository;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.ModelReader;

@Named
public class ModelRepository
{
	private final Map<String, ?> options = Map.of(ModelReader.IS_STRICT, true);

	private final ModelReader reader;

	private final Map<URL, Model> models = new HashMap<>();

	@Inject
	public ModelRepository(ModelReader reader)
	{
		this.reader = reader;
	}

	public synchronized Model get(URL url) throws Exception
	{
		Model model = models.get(url);

		if (model == null)
		{
			try (InputStream input = url.openStream())
			{
				model = reader.read(input, options);
				models.put(url, model);
			}
		}

		return model;
	}

	public Model get(Path path) throws Exception
	{
		return get(path.toAbsolutePath().normalize().toUri().toURL());
	}
}
