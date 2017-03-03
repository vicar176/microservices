package com.mcmcg.dia.batchscheduler.job;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.calendar.CronCalendar;

import com.mcmcg.dia.batchmanager.domain.BatchProfileWithAction;
import com.mcmcg.dia.batchmanager.domain.Frequency;
import com.mcmcg.dia.batchmanager.entity.BatchProfile;
import com.mcmcg.dia.batchscheduler.service.model.BatchProfileJob;

/**
 * 
 * @author jaleman
 *
 */

public final class JobScheduler {


	/**
	 * 
	 * @param batchProfileId
	 * @param action
	 */
	public static void schedule(BatchProfileWithAction batchProfileWithAction) throws JobExecutionException{
		try {
			String action = batchProfileWithAction.getAction().getDescription();
			Long batchProfileId = batchProfileWithAction.getBatchProfile().getBatchProfileId();
			// Adding Version 
			Integer version = batchProfileWithAction.getBatchProfile().getVersion();
			
			String jobName = "Job-" + action + "-" + batchProfileId;
			SchedulerFactory schfa = new StdSchedulerFactory();
			Scheduler sch = schfa.getScheduler();
			JobDetail job = JobBuilder.newJob(BatchProfileJob.class)
					.withIdentity(jobName, action.toString()) 
					.build();

			job.getJobDataMap().put("batchProfileId", batchProfileId);
			job.getJobDataMap().put("action", action);
			job.getJobDataMap().put("version", version);
			
			Trigger trigger = TriggerBuilder.newTrigger().withIdentity("batchTrigger" + batchProfileId, action)
					.startNow().withSchedule(createCron(batchProfileWithAction.getBatchProfile())).build();

			sch.scheduleJob(job, trigger);
			sch.start();
		} catch (SchedulerException e) {
			throw new JobExecutionException(e.getMessage(), e);
		}
	}
	
	/**
	 * 
	 * @param batchProfileId
	 * @param action
	 * @throws JobExecutionException
	 */
	public static void reschedule(BatchProfileWithAction batchProfileWithAction) throws JobExecutionException{
		try {
			String action = batchProfileWithAction.getAction().getDescription();
			Long batchProfileId = batchProfileWithAction.getBatchProfile().getBatchProfileId();

			SchedulerFactory schfa = new StdSchedulerFactory();
			Scheduler sch = schfa.getScheduler();
			Trigger trigger = sch.getTrigger(new TriggerKey("batchTrigger" + batchProfileId, action));
			
			TriggerBuilder builder = trigger.getTriggerBuilder();
			
			trigger = builder.withSchedule(createCron(batchProfileWithAction.getBatchProfile())).build();
			
			sch.rescheduleJob(trigger.getKey(), trigger);
			
		} catch (SchedulerException e) {
			throw new JobExecutionException(e.getMessage(), e);
		}
	}
	
	/**
	 * 
	 * @param batchProfile
	 * @return
	 */
	private static CronScheduleBuilder createCron(BatchProfile batchProfile){
		
		CronScheduleBuilder builder = null;
		Calendar calendarTime = GregorianCalendar.getInstance();
		Calendar calendarDate = GregorianCalendar.getInstance();
		calendarTime.setTime(batchProfile.getScheduleTime());
		calendarDate.setTime(batchProfile.getScheduleDate());
		Frequency frequency=Frequency.Once;
		if(null!=batchProfile.getFrecuency())
	     frequency=Frequency.getIntegerMap().get(batchProfile.getFrecuency());
		switch (frequency){
		
		case Daily:
			builder = CronScheduleBuilder.dailyAtHourAndMinute(calendarTime.get(Calendar.HOUR_OF_DAY), calendarTime.get(Calendar.MINUTE));
			break;
		case Weekly:
			builder = CronScheduleBuilder.weeklyOnDayAndHourAndMinute(calendarDate.get(Calendar.DAY_OF_WEEK), calendarTime.get(Calendar.HOUR_OF_DAY), calendarTime.get(Calendar.MINUTE));
			break;
		case Monthly:
			builder = CronScheduleBuilder.monthlyOnDayAndHourAndMinute(calendarDate.get(Calendar.DAY_OF_MONTH), calendarTime.get(Calendar.HOUR_OF_DAY), calendarTime.get(Calendar.MINUTE));
			break;	
		case Once:	
			//This Schedules the Job at given day and time
			builder=CronScheduleBuilder.cronSchedule(String.format("%1$s %2$s %3$s %4$s %5$s %6$s %7$s",  calendarTime.get(Calendar.SECOND), calendarTime.get(Calendar.MINUTE), calendarTime.get(Calendar.HOUR_OF_DAY),  calendarDate.get(Calendar.DAY_OF_MONTH),calendarDate.get(Calendar.MONTH) ,calendarDate.get(Calendar.DAY_OF_WEEK) , calendarDate.get(Calendar.YEAR)));			
		}
		
		return builder;
		
	}
}
