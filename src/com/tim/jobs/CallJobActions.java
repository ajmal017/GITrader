package com.tim.jobs;

import java.util.Date;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerUtils;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdSchedulerFactory;

public class CallJobActions {

	public static void main(String[] args) throws SchedulerException, InterruptedException {
		// TODO Auto-generated method stub
		
		SchedulerFactory sf = new StdSchedulerFactory();
		Scheduler sched = sf.getScheduler();
		
	 	JobKey _Key1= new JobKey("dummyJobName2");
    	if (sched.checkExists(_Key1))
    			sched.deleteJob(_Key1);
		
		// define the job and tie it to our HelloJob class
		JobDetail job = JobBuilder.newJob(Trading_Actions.class)
				.withIdentity("dummyJobName2", "group2").build();
	 
		// compute a time that is on the next round minute
		// Date runTime = TriggerUtils.  (new Date());

		// compute a time that is on the next round minute
		//Date runTime = evenMinuteDate(new Date());

		// Trigger the job to run on the next round minute
		Trigger trigger = TriggerBuilder
				.newTrigger()
				.withIdentity("dummyTriggerName2", "group2")			
				.build();
				 
		// Tell quartz to schedule the job using our trigger
		Scheduler scheduler = new StdSchedulerFactory().getScheduler();
		scheduler.start();
		scheduler.scheduleJob(job, trigger);
 
		
		

	}

}
