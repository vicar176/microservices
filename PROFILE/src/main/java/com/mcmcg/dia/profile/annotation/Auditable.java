/**
 * 
 */
package com.mcmcg.dia.profile.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.mcmcg.dia.profile.util.EventCode;

/**
 * @author Jose Aleman
 *
 */
@Target( { ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {
	EventCode eventCode() default EventCode.START;
}
