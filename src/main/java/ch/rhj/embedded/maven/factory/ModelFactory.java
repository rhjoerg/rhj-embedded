package ch.rhj.embedded.maven.factory;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.io.ModelReader;

@Named
public class ModelFactory
{
	private static final Map<String, ?> OPTIONS = Map.of(ModelReader.IS_STRICT, true);

	private final ModelReader reader;

	@Inject
	public ModelFactory(ModelReader reader)
	{
		this.reader = reader;
	}

	public Model createModel(URL pomUrl) throws Exception
	{
		try (InputStream input = pomUrl.openStream())
		{
			Model model = reader.read(input, OPTIONS);

			populate(model);

			return model;
		}
	}

	public Model create(Path pomPath) throws Exception
	{
		pomPath = pomPath.toAbsolutePath().normalize();

		Model model = createModel(pomPath.toUri().toURL());

		model.setPomFile(pomPath.toFile());

		return model;
	}

	private void populate(Model model)
	{
		Parent parent = model.getParent();

		if (parent != null)
		{
			setGroupId(model, parent);
			setVersion(model, parent);
		}
	}

	private void setGroupId(Model model, Parent parent)
	{
		if (model.getGroupId() == null)
		{
			model.setGroupId(parent.getGroupId());
		}
	}

	private void setVersion(Model model, Parent parent)
	{
		if (model.getVersion() == null)
		{
			model.setVersion(parent.getVersion());
		}
	}
}
