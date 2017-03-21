package org.pale.chatcitizen.Command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)

public @interface Cmd {
	String name() default "";
	String desc() default "";
	String usage() default "";
	String permission() default "";
	int argc() default -1; // means "varargs"
	boolean player() default false; // requires a player
	boolean cz() default false; // requires a selected citizen
}
