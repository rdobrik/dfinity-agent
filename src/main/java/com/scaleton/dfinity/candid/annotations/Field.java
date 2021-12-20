package com.scaleton.dfinity.candid.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.scaleton.dfinity.candid.types.Type;

@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface Field {
	public Type value();
}
