package com.hyperkit.welding.renderers;

import com.hyperkit.welding.Progress;
import com.hyperkit.welding.Search;
import com.hyperkit.welding.configurations.Render3DConfiguration;
import com.hyperkit.welding.exceptions.SearchException;

public class Renderer3D extends Renderer {

	private Search search;
	private Render3DConfiguration configuration;

	public Renderer3D(Search search, Render3DConfiguration configuration) {
		this.search = search;
		this.configuration = configuration;
	}

	public void generateDataset(Progress progress) throws SearchException {
		
	}

}
