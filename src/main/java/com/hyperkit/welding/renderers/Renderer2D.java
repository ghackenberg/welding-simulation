package com.hyperkit.welding.renderers;

import org.jfree.data.xy.XYSeriesCollection;

import com.hyperkit.welding.Progress;
import com.hyperkit.welding.exceptions.SearchException;

public abstract class Renderer2D extends Renderer {

	public abstract XYSeriesCollection generateDataset(Progress progress) throws SearchException;
	
}
