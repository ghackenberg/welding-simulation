package com.hyperkit.welding;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public abstract class Exporter {
	
	protected static final DecimalFormat FORMAT = new DecimalFormat("0.00");
	
	public void export(XYSeriesCollection collection, File file) {
		List<XYSeries> list = new ArrayList<>();
		
		for (Object object: collection.getSeries()) {
			if (object instanceof XYSeries) {
				list.add((XYSeries) object);
			} else {
				throw new IllegalStateException("Series type not supported: " + object.getClass().getName());
			}
		}
		
		export(list, file);
	}

	public abstract void export(List<XYSeries> list, File file);
	
}
