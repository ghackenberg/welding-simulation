package com.hyperkit.welding;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.hyperkit.welding.annotations.DoubleParameter;
import com.hyperkit.welding.annotations.IntegerParameter;
import com.hyperkit.welding.annotations.LongParameter;
import com.hyperkit.welding.annotations.Parameter;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.Property;

public class Configurator {
	
	private static final Color GRAY_0 = Color.DARK_GRAY;
	private static final Color GRAY_1 = Color.DARK_GRAY.brighter().brighter();
	private static final Color GRAY_2 = Color.DARK_GRAY.brighter().brighter().brighter();
	
	private static final NumberFormat FORMAT = NumberFormat.getInstance();
	
	private List<Configuration> configurations = new ArrayList<>();
	
	public void addConfiguration(Configuration configuration) {
		configurations.add(configuration);
	}
	
	public JPanel createPanel() {
		
		// Create layout
		
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();

		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;

		// Create panel

		JPanel panel = new JPanel();

		panel.setLayout(layout);
		panel.setPreferredSize(new Dimension(600, 200));
		
		int row = 0;

		for (Configuration configuration : configurations) {
			
			constraints.gridx = 0;
			constraints.gridy = row;
			constraints.gridwidth = 5;
			panel.add(createBoldLabel(configuration.getTitle(), Color.WHITE, GRAY_0), constraints);
			
			row = row + 1;

			constraints.gridwidth = 1;
			constraints.gridy = row;
			panel.add(createBoldLabel("Name", Color.BLACK, GRAY_1), constraints);
			
			constraints.gridx = 1;
			panel.add(createBoldLabel("Wert", Color.BLACK, GRAY_1), constraints);
			
			constraints.gridx = 2;
			panel.add(createBoldLabel("Minimum", Color.BLACK, GRAY_1), constraints);
			
			constraints.gridx = 3;
			panel.add(createBoldLabel("Maximum", Color.BLACK, GRAY_1), constraints);
			
			constraints.gridx = 4;
			panel.add(createBoldLabel("Einheit", Color.BLACK, GRAY_1), constraints);
			
			row = row + 1;
			
			for (Parameter parameter : configuration.getParameters()) {
				try {
					Method method = configuration.getMethods().get(parameter);
					Property<?> property = (Property<?>) method.invoke(configuration);
	
					// Add name
	
					constraints.gridx = 0;
					constraints.gridy = row;
					panel.add(createBoldLabel(parameter.name(), Color.BLACK, GRAY_2), constraints);
	
					// Add value
					
					if (property instanceof BooleanProperty) {
						boolean value = (Boolean) property.getValue();
						
						JCheckBox checkbox = new JCheckBox("", value);
						
						checkbox.addActionListener(action -> {
							((BooleanProperty) property).setValue(checkbox.isSelected());
						});
	
						updateStyle(checkbox, Color.BLACK, GRAY_2);
						
						constraints.gridx = 1;
						panel.add(checkbox, constraints);
						
						constraints.gridx = 2;
						panel.add(createLimitLabel("k.A.", Color.BLACK, GRAY_2), constraints);
						
						constraints.gridx = 3;
						panel.add(createLimitLabel("k.A.", Color.BLACK, GRAY_2), constraints);
						
					} else if (property instanceof IntegerProperty) {
						IntegerParameter integer_parameter = method.getAnnotation(IntegerParameter.class);
	
						int value = (Integer) property.getValue();
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

						constraints.gridx = 1;
						panel.add(spinner, constraints);
						
						constraints.gridx = 2;
						panel.add(createLimitLabel(min == Integer.MIN_VALUE ? "k.A." : FORMAT.format(min), Color.BLACK, GRAY_2), constraints);
						
						constraints.gridx = 3;
						panel.add(createLimitLabel(max == Integer.MAX_VALUE ? "k.A." : FORMAT.format(max), Color.BLACK, GRAY_2), constraints);
					} else if (property instanceof LongProperty) {
						LongParameter long_parameter = method.getAnnotation(LongParameter.class);
						
						long value = (Long) property.getValue();
						long min = long_parameter.min();
						long max = long_parameter.max();
						/*
						long step = long_parameter.step();
						*/
						
						JTextField field = new JTextField(FORMAT.format(value));
						
						field.setHorizontalAlignment(JTextField.RIGHT);
						field.addFocusListener(new FocusListener() {
							@Override
							public void focusLost(FocusEvent event) {
								long value;
								try {
									value = FORMAT.parse(field.getText()).longValue();
								} catch (ParseException exception) {
									value = min;
								}
								if (value < min) {
									value = min;
								}
								if (value > max) {
									value = max;
								}
								((LongProperty) property).set(value);
								field.setText(FORMAT.format(value));
							}
							@Override
							public void focusGained(FocusEvent event) {
								// Ignore
							}
						});
						field.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent event) {
								long value;
								try {
									value = FORMAT.parse(field.getText()).longValue();
								} catch (ParseException exception) {
									value = min;
								}
								if (value < min) {
									value = min;
								}
								if (value > max) {
									value = max;
								}
								((LongProperty) property).set(value);
								field.setText(FORMAT.format(value));
							}
						});
	
						//updateStyle(field, Color.BLACK, GRAY_2);

						constraints.gridx = 1;
						panel.add(field, constraints);
						
						constraints.gridx = 2;
						panel.add(createLimitLabel(min == Long.MIN_VALUE ? "k.A." : FORMAT.format(min), Color.BLACK, GRAY_2), constraints);
						
						constraints.gridx = 3;
						panel.add(createLimitLabel(max == Long.MAX_VALUE ? "k.A." : FORMAT.format(max), Color.BLACK, GRAY_2), constraints);
					} else if (property instanceof DoubleProperty) {
						DoubleParameter double_parameter = method.getAnnotation(DoubleParameter.class);
						
						double value = (Double) property.getValue();
						double min = double_parameter.min();
						double max = double_parameter.max();
						/*
						double step = double_parameter.step();
						*/
						
						JTextField field = new JTextField(FORMAT.format(value));
						
						field.setHorizontalAlignment(JTextField.RIGHT);
						field.addFocusListener(new FocusListener() {
							@Override
							public void focusLost(FocusEvent event) {
								double value;
								try {
									value = FORMAT.parse(field.getText()).doubleValue();
								} catch (ParseException exception) {
									value = min;
								}
								if (value < min) {
									value = min;
								}
								if (value > max) {
									value = max;
								}
								((DoubleProperty) property).set(value);
								field.setText(FORMAT.format(value));
							}
							@Override
							public void focusGained(FocusEvent event) {
								// Ignore
							}
						});
						field.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent event) {
								double value;
								try {
									value = FORMAT.parse(field.getText()).doubleValue();
								} catch (ParseException exception) {
									value = min;
								}
								if (value < min) {
									value = min;
								}
								if (value > max) {
									value = max;
								}
								((DoubleProperty) property).set(value);
								field.setText(FORMAT.format(value));
							}
						});
	
						//updateStyle(field, Color.BLACK, GRAY_2);

						constraints.gridx = 1;
						panel.add(field, constraints);
						
						constraints.gridx = 2;
						panel.add(createLimitLabel(min == -Double.MAX_VALUE ? "k.A." : FORMAT.format(min), Color.BLACK, GRAY_2), constraints);
						
						constraints.gridx = 3;
						panel.add(createLimitLabel(max == Double.MAX_VALUE ? "k.A." : FORMAT.format(max), Color.BLACK, GRAY_2), constraints);
					} else {
						throw new IllegalStateException("Property type not supported: " + property.getClass().getName());
					}
	
					// Add unit
	
					constraints.gridx = 4;
					panel.add(createNormalLabel(parameter.unit(), Color.DARK_GRAY, GRAY_2), constraints);
					
					row = row + 1;
	
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
		
		return panel;
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
	
	private static JLabel createLimitLabel(String content, Color foregroundColor, Color backgroundColor) {
		JLabel result = new JLabel(content);
		
		result.setHorizontalAlignment(JLabel.RIGHT);

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
