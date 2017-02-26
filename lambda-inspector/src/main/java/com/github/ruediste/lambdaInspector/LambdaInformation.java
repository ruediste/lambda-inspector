package com.github.ruediste.lambdaInspector;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LambdaInformation {

    /**
     * Internal name of the class containing the implementation method
     */
    String implMethodClassName();

    String implMethodName();

    String implMethodDesc();

    Class<?>samClass();

    String samMethodName();

    String samMethodDesc();
}
