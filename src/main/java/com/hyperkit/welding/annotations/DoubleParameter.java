package com.hyperkit.welding.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DoubleParameter {
	double min() default -Double.MAX_VALUE;
	double max() default Double.MAX_VALUE;
	double step() default 1;
}
