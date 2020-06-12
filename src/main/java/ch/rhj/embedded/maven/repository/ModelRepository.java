package ch.rhj.embedded.maven.repository;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
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

	public synchronized Model get(URL pomUrl) throws Exception
	{
		Model model = models.get(pomUrl);

		if (model == null)
		{
			try (InputStream input = pomUrl.openStream())
			{
				model = reader.read(input, options);
				populate(model);
				models.put(pomUrl, model);
			}
		}

		return model;
	}

	public Model get(Path pomPath) throws Exception
	{
		pomPath = pomPath.toAbsolutePath().normalize();

		Model model = get(pomPath.toUri().toURL());

		model.setPomFile(pomPath.toFile());

		return model;
	}

	private void populate(Model model)
	{
		Parent parent = model.getParent();

		if (parent != null)
		{
			if (model.getGroupId() == null)
			{
				model.setGroupId(parent.getGroupId());
			}

			if (model.getVersion() == null)
			{
				model.setVersion(parent.getVersion());
			}
		}
	}
}
