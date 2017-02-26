package com.github.ruediste.lambdaInspector;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation added to lambda classes to provide information about the
 * implementation method of the lambda.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LambdaInformation {

    /**
     * Class defining the implementation method
     */
    Class<?> implMethodClass();

    /**
     * Name of the implementation method
     */
    String implMethodName();

    /**
     * Descriptor of the implementation method
     */
    String implMethodDesc();

    /**
     * Class declaring the single abstract method implemented by the lambda
     */
    Class<?> samClass();

    /**
     * Name of the single abstract method in the interface of the lambda
     */
    String samMethodName();

    /**
     * Descriptor of the single abstact method in the interface of the lambda
     */
    String samMethodDesc();
}
