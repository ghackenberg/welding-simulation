package com.hyperkit.welding.loaders;

import javax.swing.JFrame;
import javax.swing.JProgressBar;

import org.jfree.chart.ChartPanel;
import org.jfree.data.xy.XYSeriesCollection;

import com.hyperkit.welding.Progress;
import com.hyperkit.welding.Range;
import com.hyperkit.welding.exceptions.SearchException;
import com.hyperkit.welding.renderers.RendererXY;

public class LoaderXY extends Loader2D<RendererXY> {
	
	private Range min_x;
	private Range max_x;
	private double z;

	public LoaderXY(JFrame frame, JProgressBar progress_bar, RendererXY render, ChartPanel chart_panel, Range min_x, Range max_x, double z) {
		super(frame, progress_bar, render, chart_panel, "XZ-Schweiﬂprofil", "L‰nge", "Tiefe");
		
		this.min_x = min_x;
		this.max_x = max_x;
		this.z = z;
	}

	@Override
	protected XYSeriesCollection generateDataset(Progress progress) throws SearchException {
		return getRenderer().generateDataset(min_x, max_x, z, progress);
	}
	
	@Override
	protected String getAnnotation() {
		return "z = " + z;
	}

}
