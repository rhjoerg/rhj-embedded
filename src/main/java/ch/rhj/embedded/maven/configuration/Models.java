package ch.rhj.embedded.maven.configuration;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.io.ModelReader;

@Named
public class Models
{
	private final Map<String, ?> options = Map.of(ModelReader.IS_STRICT, true);

	private final ModelReader modelReader;

	private final Map<Path, Model> models = new HashMap<>();

	@Inject
	public Models(ModelReader modelReader)
	{
		this.modelReader = modelReader;
	}

	public synchronized Model get(Path pom) throws IOException
	{
		pom = pom.toAbsolutePath();
		Model model = models.get(pom);

		if (model == null)
		{
			model = modelReader.read(pom.toFile(), options);
			populate(model);
			models.put(pom, model);
		}

		return model;
	}

	private void populate(Model model)
	{
		Parent parent = model.getParent();

		if (parent == null)
		{
			return;
		}

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
