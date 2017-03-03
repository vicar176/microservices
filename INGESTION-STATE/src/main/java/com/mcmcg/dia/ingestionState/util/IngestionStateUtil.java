package com.mcmcg.dia.ingestionState.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.mcmcg.dia.ingestionState.model.domain.IngestionStep;
import com.mcmcg.dia.ingestionState.model.domain.WorkflowShutdownState;
import com.mcmcg.dia.ingestionState.model.domain.WorkflowState;
import com.mcmcg.dia.ingestionState.model.entity.IngestionStepEntity;
import com.mcmcg.dia.ingestionState.model.entity.WorkflowShutdownStateEntity;
import com.mcmcg.dia.ingestionState.model.entity.WorkflowStateEntity;

/**
 * 
 * @author Victor Arias
 *
 */
public class IngestionStateUtil {

	private static final Logger LOG = Logger.getLogger(IngestionStateUtil.class);

	public static final String DATE_FORMAT_LONG = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	public static final String DATE_FORMAT_SHORT = "MM/dd/yyyy";

	/**
	 * Convert a String into a Date using a given format
	 * 
	 * @param input
	 * @param format
	 * @return Date
	 * @throws ParseException
	 */

	public static IngestionStep convertIngestionStepEntityToDomain(IngestionStepEntity entity) throws ParseException {
		IngestionStep domain = null;
		if (entity != null) {
			domain = new IngestionStep();
			BeanUtils.copyProperties(entity, domain);
		}

		return domain;
	}

	public static IngestionStepEntity convertIngestionStepToEntity(IngestionStep domain) {
		IngestionStepEntity entity = null;
		if (domain != null) {
			entity = new IngestionStepEntity();
			BeanUtils.copyProperties(domain, entity);
		}

		return entity;
	}

	public static WorkflowState convertWorkflowStateEntitytoDomain(WorkflowStateEntity entity) throws ParseException {
		WorkflowState domain = null;
		if (entity != null) {
			domain = new WorkflowState();
			BeanUtils.copyProperties(entity, domain);
		}
		return domain;
	}

	public static WorkflowStateEntity convertWorkflowStateToEntity(WorkflowState domain) {
		WorkflowStateEntity entity = null;
		if (domain != null) {
			entity = new WorkflowStateEntity();
			BeanUtils.copyProperties(domain, entity);
		}
		return entity;
	}
	
	public static WorkflowShutdownStateEntity convertWorkflowShutdownStateToEntity(WorkflowShutdownState domain) {
		WorkflowShutdownStateEntity entity = null;
		if (domain != null) {
			entity = new WorkflowShutdownStateEntity();
			BeanUtils.copyProperties(domain, entity);
		}
		return entity;
	}
	
	public static WorkflowShutdownState convertWorkflowShutdownStateEntityToDomain(WorkflowShutdownStateEntity entity) {
		WorkflowShutdownState domain = null;
		if (entity != null) {
			domain = new WorkflowShutdownState();
			BeanUtils.copyProperties(entity, domain);
		}
		return domain;
	}

	public static Date convertToDate(String input, String dateFormat) {

		Date date = null;
		if (input == null || dateFormat == null) {
			LOG.info("convertToDate null -> null");
			return null;
		}
		try {
			date = new SimpleDateFormat(dateFormat).parse(input);
		} catch (ParseException e) {
			LOG.info(String.format("convertToDate - ParseException (input = %s and dateFormat = %s)", input,
					dateFormat));
			return null;
		}

		LOG.info(String.format("convertToDate - (input = %s and dateFormat = %s) -> %s", input, dateFormat,
				date.toString()));
		return date;
	}

	public static String formatDate(Date date) {
		return getFormater(false).format(date);
	}

	public static String formatDateShort(Date date) {
		return getFormater(true).format(date);
	}

	public static Date parseDate(String date) throws ParseException {
		return getFormater(false).parse(date);
	}

	private static SimpleDateFormat getFormater(boolean isShort) {
		TimeZone tz = TimeZone.getTimeZone("UTC");
		SimpleDateFormat formater;
		if (isShort) {
			String dateLong = DATE_FORMAT_SHORT;
			formater = new SimpleDateFormat(dateLong);
		} else {
			formater = new SimpleDateFormat(DATE_FORMAT_LONG);
		}
		formater.setTimeZone(tz);
		return formater;
	}

}
