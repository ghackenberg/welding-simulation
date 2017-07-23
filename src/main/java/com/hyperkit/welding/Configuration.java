package com.hyperkit.welding;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hyperkit.welding.annotations.Parameter;

import javafx.beans.property.Property;

public abstract class Configuration {

	private String title;
	private List<Parameter> parameters = new ArrayList<>();
	private Map<Parameter, Method> methods = new HashMap<>();

	public Configuration(String title) {
		this.title = title;
		
		// Find parameters and properties
	
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
	}
	
	public String getTitle() {
		return title;
	}

	public List<Parameter> getParameters() {
		return parameters;
	}
	
	public Map<Parameter, Method> getMethods() {
		return methods;
	}

}
