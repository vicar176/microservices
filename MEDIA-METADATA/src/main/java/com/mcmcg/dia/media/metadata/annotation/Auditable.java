/**
 * 
 */
package com.mcmcg.dia.media.metadata.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.mcmcg.dia.media.metadata.util.EventCode;

/**
 * @author Jose Aleman
 *
 */
@Target( { ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {
	EventCode eventCode() default EventCode.START;
}
