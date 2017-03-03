package com.mcmcg.dia.account.metadata.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.mcmcg.dia.account.metadata.util.EventCode;



@Target( { ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {
	EventCode eventCode() default EventCode.START;
}
