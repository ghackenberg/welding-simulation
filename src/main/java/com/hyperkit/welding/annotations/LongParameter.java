package com.hyperkit.welding.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LongParameter {
	long min() default Long.MIN_VALUE;
	long max() default Long.MAX_VALUE;
	long step() default 1;
}
