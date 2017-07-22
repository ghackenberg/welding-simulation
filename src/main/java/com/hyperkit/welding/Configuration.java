package com.hyperkit.welding;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.hyperkit.welding.annotations.DoubleParameter;
import com.hyperkit.welding.annotations.IntegerParameter;
import com.hyperkit.welding.annotations.Parameter;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;

public abstract class Configuration {
	
	private static final Color GRAY_0 = Color.DARK_GRAY;
	private static final Color GRAY_1 = Color.DARK_GRAY.brighter().brighter();
	private static final Color GRAY_2 = Color.DARK_GRAY.brighter().brighter().brighter();

	private String title;

	public Configuration(String title) {
		this.title = title;
	}

	public JPanel createPanel() {

		// Find parameters and properties

		List<Parameter> parameters = new ArrayList<>();
		Map<Parameter, Method> methods = new HashMap<>();

		for (Method method : getClass().getMethods()) {
			boolean name = method.getName().endsWith("Property");
			boolean returns = Property.class.isAssignableFrom(method.getReturnType());
			Parameter parameter = method.getAnnotation(Parameter.class);

			if (name && returns && parameter != null) {
				parameters.add(parameter);
				methods.put(parameter, method);
			}
		}

		// Sort parameters

		parameters.sort(new Comparator<Parameter>() {
			@Override
			public int compare(Parameter first, Parameter second) {
				if (first.order() != second.order()) {
					return first.order() - second.order();
				} else {
					return first.name().compareTo(second.name());
				}
			}
		});
		
		// Create layout
		
		GridLayout layout = new GridLayout(parameters.size() + 1, 3, 1, 1);

		// Create panel

		JPanel panel = new JPanel();

		panel.setLayout(layout);
		panel.setPreferredSize(new Dimension(400, 200));

		panel.add(createBoldLabel("Name", Color.BLACK, GRAY_1));
		panel.add(createBoldLabel("Wert", Color.BLACK, GRAY_1));
		panel.add(createBoldLabel("Einheit", Color.BLACK, GRAY_1));

		for (Parameter parameter : parameters) {
			try {
				Method method = methods.get(parameter);
				Property<?> property = (Property<?>) method.invoke(this);

				// Add name

				panel.add(createBoldLabel(parameter.name(), Color.BLACK, GRAY_2));

				// Add value

				if (property instanceof IntegerProperty) {
					IntegerParameter integer_parameter = method.getAnnotation(IntegerParameter.class);

					int value = (int) property.getValue();
					int min = integer_parameter.min();
					int max = integer_parameter.max();
					int step = integer_parameter.step();

					SpinnerModel model = new SpinnerNumberModel(value, min, max, step);
					
					JSpinner spinner = new JSpinner(model);
					
					spinner.addChangeListener(new ChangeListener() {
						@Override
						public void stateChanged(ChangeEvent event) {
							((IntegerProperty) property).set((int) spinner.getValue());
						}
					});

					updateStyle(spinner, Color.BLACK, GRAY_2);

					panel.add(spinner);
				} else if (property instanceof DoubleProperty) {
					DoubleParameter double_parameter = method.getAnnotation(DoubleParameter.class);

					double value = (double) property.getValue();
					double min = double_parameter.min();
					double max = double_parameter.max();
					double step = double_parameter.step();

					SpinnerModel model = new SpinnerNumberModel(value, min, max, step);
					
					JSpinner spinner = new JSpinner(model);
					
					spinner.addChangeListener(new ChangeListener() {
						@Override
						public void stateChanged(ChangeEvent event) {
							((DoubleProperty) property).set((double) spinner.getValue());
						}
					});

					updateStyle(spinner, Color.BLACK, GRAY_2);

					panel.add(spinner);
				} else {
					throw new IllegalStateException("Property type not supported: " + property.getClass().getName());
				}

				// Add unit

				panel.add(createNormalLabel(parameter.unit(), Color.DARK_GRAY, GRAY_2));

			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}

		// Create frame

		JPanel frame = new JPanel();

		frame.setLayout(new BorderLayout());
		frame.add(createBoldLabel(title, Color.WHITE, GRAY_0), BorderLayout.PAGE_START);
		frame.add(panel, BorderLayout.CENTER);

		return frame;
	}

	private static JLabel createBoldLabel(String content, Color foregroundColor, Color backgroundColor) {
		JLabel result = createNormalLabel(content, foregroundColor, backgroundColor);

		result.setFont(new Font(result.getFont().getName(), Font.BOLD, result.getFont().getSize()));

		return result;
	}

	private static JLabel createNormalLabel(String content, Color foregroundColor, Color backgroundColor) {
		JLabel result = new JLabel(content);

		updateStyle(result, foregroundColor, backgroundColor);

		return result;
	}

	private static void updateStyle(JComponent component, Color foregroundColor, Color backgroundColor) {
		component.setBorder(new EmptyBorder(5, 5, 5, 5));
		component.setForeground(foregroundColor);
		component.setBackground(backgroundColor);
		component.setOpaque(true);
	}

}
