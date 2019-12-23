package com.hyperkit.welding.programs;

import com.hyperkit.welding.Program;
import com.hyperkit.welding.configurations.models.CircularModelConfiguration;
import com.hyperkit.welding.models.CircularModel;

public class CircularProgram extends Program<CircularModelConfiguration, CircularModel> {

	public static void main(String[] args) {
		new CircularProgram().run(args);
	}

	@Override
	protected CircularModelConfiguration getModelConfiguration() {
		return new CircularModelConfiguration();
	}

	@Override
	protected CircularModel getModel(CircularModelConfiguration configuration) {
		return new CircularModel(configuration);
	}

}
