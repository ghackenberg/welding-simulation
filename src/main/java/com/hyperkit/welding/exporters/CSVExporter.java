package com.hyperkit.welding.exporters;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;

import com.hyperkit.welding.Exporter;

public class CSVExporter extends Exporter {

	@Override
	public void export(List<XYSeries> list, File file) {
		try (FileWriter writer = new FileWriter(file)) {
			int count = list.stream().map(series -> series.getItemCount()).reduce(0, Math::max);
			
			// Head
			for (int column = 0; column < list.size(); column++) {
				writer.write(list.get(column).getKey().toString() + " - X");
				writer.write(";");
				writer.write(list.get(column).getKey().toString() + " - Y");
				writer.write(column == list.size() - 1 ? "\n" : ";");
			}
			
			// Body
			for (int item = 0; item < count; item++) {
				for (int column = 0; column < list.size(); column++) {
					if (list.get(column).getItems().size() > item) {
						Object data = list.get(column).getItems().get(item);
						if (data instanceof XYDataItem) {
							XYDataItem point = (XYDataItem) data;
							writer.write(FORMAT.format(point.getXValue()));
							writer.write(";");
							writer.write(FORMAT.format(point.getYValue()));
						} else {
							throw new IllegalStateException("Item type not supported: " + data.getClass().getName());
						}
					} else {
						writer.write(";");
					}
					writer.write(column == list.size() - 1 ? "\n" : ";");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
