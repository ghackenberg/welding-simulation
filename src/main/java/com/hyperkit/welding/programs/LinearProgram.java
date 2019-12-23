package com.hyperkit.welding.programs;

import com.hyperkit.welding.Program;
import com.hyperkit.welding.configurations.models.LinearModelConfiguration;
import com.hyperkit.welding.models.LinearModel;

public class LinearProgram extends Program<LinearModelConfiguration, LinearModel> {

	public static void main(String[] args) {
		new LinearProgram().run(args);
	}

	@Override
	protected LinearModelConfiguration getModelConfiguration() {
		return new LinearModelConfiguration();
	}

	@Override
	protected LinearModel getModel(LinearModelConfiguration configuration) {
		return new LinearModel(configuration);
	}

}
