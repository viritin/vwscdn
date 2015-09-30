package com.vaadin.wscdn.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({FIELD, TYPE, METHOD})
@Retention(RUNTIME)
public @interface WidgetSet {

    WidgetSetType value();

}
