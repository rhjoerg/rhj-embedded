package ch.rhj.embedded.maven.context;

import org.apache.maven.model.Model;

public class ContextModel
{
	private final Model model;

	public ContextModel(Model model)
	{
		this.model = model;
	}

	public Model model()
	{
		return model;
	}

}
